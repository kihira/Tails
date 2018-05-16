package uk.kihira.gltf;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import uk.kihira.gltf.animation.Animation;
import uk.kihira.gltf.animation.Channel;
import uk.kihira.gltf.spec.Accessor;
import uk.kihira.gltf.spec.AnimationSampler;
import uk.kihira.gltf.spec.BufferView;
import uk.kihira.gltf.spec.Gltf;
import uk.kihira.gltf.spec.MeshPrimitive;
import uk.kihira.gltf.spec.Node;
import uk.kihira.gltf.spec.Animation.ChannelSpec;
import uk.kihira.gltf.spec.Animation.AnimationPath;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class GltfLoader {
    private static final Gson gson = new Gson();
    private static final int JSON_CHUNK = 0x4E4F534A;
    private static final int BIN_CHUNK = 0x004E4942;

    public static HashMap<Integer, Geometry> primitiveCache = new HashMap<>();

    // Temp cache values
    private static ByteBuffer binData;
    public static final ArrayList<Accessor> accessors = new ArrayList<>();
    public static final ArrayList<BufferView> bufferViews = new ArrayList<>();
    public static final TreeMap<Integer, ByteBuffer> bufferViewBufferCache = new TreeMap<>();
    public static final TreeMap<Integer, NodeImpl> nodeCache = new TreeMap<>();
    public static final TreeMap<Integer, MeshImpl> meshCache = new TreeMap<>();

    public Model LoadGlbFile(File file) throws IOException {
        DataInputStream stream = new DataInputStream(new FileInputStream(file));
        int magic = readUnsignedInt(stream);
        int version = readUnsignedInt(stream);
        int length = readUnsignedInt(stream);

        if (magic != 0x46546C67) throw new IllegalArgumentException("File specified is not in the GLB format!");
        if (version != 2) throw new IllegalArgumentException("GLB File is not version 2");

        // First chunk is always JSON
        int chunkLength = readUnsignedInt(stream);
        int chunkType = readUnsignedInt(stream);
        byte[] data = new byte[chunkLength];
        if (stream.read(data, 0, chunkLength) != chunkLength) {
            throw new IOException("Failed to read GLB file");
        }
        if (chunkType != JSON_CHUNK) {
            throw new IOException("Expected JSON data but didn't get it");
        }
        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(new String(data)).getAsJsonObject();

        // Load BIN data
        chunkLength = readUnsignedInt(stream);
        chunkType = readUnsignedInt(stream);
        data = new byte[chunkLength];
        if (stream.read(data, 0, chunkLength) != chunkLength) {
            throw new IOException("Failed to read GLB file");
        }
        if (chunkType != BIN_CHUNK) {
            throw new IOException("Expected BIN data but didn't get it");
        }
        binData = ByteBuffer.wrap(data);

        // Read JSON data and start loading stuff
        // Load buffer views
        for (JsonElement element : root.get("bufferViews").getAsJsonArray()) {
            bufferViews.add(gson.fromJson(element, BufferView.class));
        }

        // Load accessors
        for (JsonElement element : root.get("accessors").getAsJsonArray()) {
            accessors.add(gson.fromJson(element, Accessor.class));
        }

        // Load meshes
        JsonArray meshJsonArray = root.get("meshes").getAsJsonArray();
        for (int i = 0; i < meshJsonArray.size(); i++) {
            ArrayList<Geometry> geometries = new ArrayList<>();
            for (JsonElement primitive : meshJsonArray.get(i).getAsJsonObject().get("primitives").getAsJsonArray()) {
                // todo keep geometry loading in its own class? goes against almost everything else
                geometries.add(new Geometry(gson.fromJson(primitive, MeshPrimitive.class)));
            }
            meshCache.put(i, new MeshImpl(geometries));
        }

        // Load nodes
        int[] sceneNodes = gson.fromJson(root.get("scenes").getAsJsonArray().get(0).getAsJsonObject().get("nodes"), int[].class);
        JsonArray nodeJsonArray = root.get("nodes").getAsJsonArray();
        ArrayList<NodeImpl> rootNodes = new ArrayList<>();
        for (int index : sceneNodes) {
            rootNodes.add(LoadNode(nodeJsonArray, index));
        }

        // Load animations
        ArrayList<Animation> animations = new ArrayList<>();
        for (JsonElement element : root.get("animations").getAsJsonArray()) {
            JsonObject object = element.getAsJsonObject();
            // TODO probably want to use String name = object.get("name").getAsString();
            ArrayList<AnimationSampler> samplers = gson.fromJson(object.get("samplers"), new TypeToken<ArrayList<AnimationSampler>>(){}.getType());
            ArrayList<Channel> channels = new ArrayList<>();

            for (JsonElement channelElement : object.get("channels").getAsJsonArray()) {
                JsonObject channelObject = channelElement.getAsJsonObject();
                AnimationSampler sampler = samplers.get(channelObject.get("sampler").getAsInt());
                channels.add(new Channel(
                    sampler,
                    accessors.get(sampler.output).type,
                    GetBufferFromAccessor(sampler.input).asFloatBuffer(),
                    // TODO: output data could be other then float, need to convert (non float ones are normalised)
                    GetBufferFromAccessor(sampler.output).asFloatBuffer(),
                    nodeCache.get(channelObject.get("target").getAsJsonObject().get("node").getAsInt()),
                    gson.fromJson(channelObject.get("target").getAsJsonObject().get("path"), AnimationPath.class)
                ));
            }
            animations.add(new Animation(channels));
        }

        Model model = new Model(new ArrayList<NodeImpl>(nodeCache.values()), rootNodes, animations);

        return model;
    }

    private static NodeImpl LoadNode(JsonArray nodeJsonArray, int index) {
        if (nodeCache.containsKey(index)) {
            return nodeCache.get(index);
        }
        JsonObject nodeJson = nodeJsonArray.get(index).getAsJsonObject();
        NodeImpl node;
        ArrayList<NodeImpl> children = null;

        // Load children if there any
        if (nodeJson.has("children")) {
            int[] childrenIndexes = gson.fromJson(nodeJson.get("children"), int[].class);
            children = new ArrayList<>(childrenIndexes.length);
            for (int childIndex : childrenIndexes) {
                children.add(LoadNode(nodeJsonArray, childIndex));
            }
        }

        // Load matrix, or TSR values 
        if (nodeJson.has("matrix")) {
            node = new NodeImpl(children, gson.fromJson(nodeJson.get("matrix"), float[].class));
        }
        else if (!nodeJson.has("translation") && !nodeJson.has("rotation") && !!nodeJson.has("scale")) {
            node = new NodeImpl(children, new float[]{1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1});
        }
        else {
            float[] translation = new float[]{0, 0, 0};
            float[] rotation = new float[]{0, 0, 0, 1};
            float[] scale = new float[]{1, 1, 1};
            if (nodeJson.has("translation")) {
                translation = gson.fromJson(nodeJson.get("translation"), float[].class);
            }
            if (nodeJson.has("rotation")) {
                rotation = gson.fromJson(nodeJson.get("rotation"), float[].class);
            }
            if (nodeJson.has("scale")) {
                scale = gson.fromJson(nodeJson.get("scale"), float[].class);
            }
            node = new NodeImpl(children, translation, rotation, scale);
        }

        nodeCache.put(index, node);
        return node;
    }

    public static ByteBuffer GetBufferFromAccessor(int accessorIndex) {
        // TODO sparse support
        Accessor accessor = accessors.get(accessorIndex);
        return (ByteBuffer) GetBufferFromBufferView(accessor.bufferView).position(accessor.byteOffset).limit(accessor.count * accessor.type.size * accessor.componentType.size);
    }

    public static ByteBuffer GetBufferFromBufferView(int bufferViewIndex) {
        if (bufferViewBufferCache.containsKey(bufferViewIndex)) {
            return bufferViewBufferCache.get(bufferViewIndex);
        }
        BufferView bufferView = bufferViews.get(bufferViewIndex);
        ByteBuffer buffer = (ByteBuffer) binData.slice().position(bufferView.byteOffset).limit(bufferView.byteLength);
        bufferViewBufferCache.put(bufferViewIndex, buffer);
        return buffer;
    }

    private static int readUnsignedInt(DataInputStream stream) throws IOException {
        return Integer.reverseBytes(stream.readInt());
    }

    private static void clearCaches() {

    }
}
