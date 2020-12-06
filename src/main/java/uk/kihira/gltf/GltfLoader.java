package uk.kihira.gltf;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;
import org.lwjgl.BufferUtils;
import uk.kihira.gltf.animation.Animation;
import uk.kihira.gltf.animation.AnimationPath;
import uk.kihira.gltf.animation.Channel;
import uk.kihira.gltf.animation.Sampler;
import uk.kihira.gltf.spec.Accessor;
import uk.kihira.gltf.spec.BufferView;
import uk.kihira.gltf.spec.MeshPrimitive;
import uk.kihira.tails.common.ByteBufferInputStream;
import uk.kihira.tails.common.Tails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class GltfLoader {
    private static final Gson gson = new Gson();
    private static final int JSON_CHUNK = 0x4E4F534A;
    private static final int BIN_CHUNK = 0x004E4942;

    // Temp cache values
    private static final ArrayList<Accessor> accessors = new ArrayList<>();
    private static final ArrayList<BufferView> bufferViews = new ArrayList<>();
    private static final TreeMap<Integer, Node> nodeCache = new TreeMap<>();
    private static final ArrayList<Mesh> meshCache = new ArrayList<>();

    public static Model LoadGlbFile(File file) throws IOException {
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
        ByteBuffer binData = BufferUtils.createByteBuffer(data.length);
        binData.put(data);
        binData.rewind();

        // Get scene
        if (!root.has("scenes")) {
            throw new GltfException("At least one scene must be defined in the asset");
        }
        if (root.has("scene") && root.get("scene").getAsInt() >= root.get("scenes").getAsJsonArray().size()) {
            throw new GltfException("Main scene defined is greater then the number of scenes available");
        }
        JsonObject scene = root.getAsJsonArray("scenes").get(root.has("scene") ? root.get("scene").getAsInt() : 0).getAsJsonObject();

        // Read JSON data and start loading stuff
        // Load buffer views
        for (JsonElement element : root.get("bufferViews").getAsJsonArray()) {
            BufferView bufferView = gson.fromJson(element, BufferView.class);
            bufferView.setData((ByteBuffer) binData.slice().position(bufferView.byteOffset).limit(bufferView.byteOffset + bufferView.byteLength));
            bufferViews.add(bufferView);
        }

        // Load accessors
        for (JsonElement element : root.get("accessors").getAsJsonArray()) {
            accessors.add(gson.fromJson(element, Accessor.class));
        }

        // Load meshes
        root.get("meshes").getAsJsonArray().forEach(meshJson -> {
            ArrayList<Geometry> geometries = new ArrayList<>();
            meshJson.getAsJsonObject().get("primitives").getAsJsonArray().forEach(
                    primitive -> geometries.add(loadPrimitive(gson.fromJson(primitive, MeshPrimitive.class))));
            meshCache.add(new Mesh(geometries));
        });

        // Load textures
        ArrayList<ResourceLocation> textures = new ArrayList<>();
        if (root.has("images")) 
        {
            root.get("images").getAsJsonArray().forEach(imageJson -> 
            {
                JsonObject imageObj = imageJson.getAsJsonObject();
                ByteBuffer bufferView = bufferViews.get(imageObj.get("bufferView").getAsInt()).getData();
                String mimeType = imageObj.get("mimeType").getAsString();
                String name = getName(imageObj, "Image_" + textures.size());

                try (ByteBufferInputStream is = new ByteBufferInputStream(bufferView)) 
                {
                    DynamicTexture texture = new DynamicTexture(NativeImage.read(is));
                    // TODO we're assuming that we have one texture, and giving it the same name/id as the main model file. This is the same one as defined in the part definition file
                    ResourceLocation texResLoc = new ResourceLocation( Tails.MOD_ID, FilenameUtils.getBaseName(file.getName()));

                    Minecraft.getInstance().getTextureManager().loadTexture(texResLoc, texture);
                    textures.add(texResLoc);
                } catch (IOException e) 
                {
                    Tails.LOGGER.error("Failed to load texture " + name, e);
                }
            });
        }
        else {
            // todo default texture
            textures.add(TextureManager.RESOURCE_LOCATION_EMPTY);
            Tails.LOGGER.warn("No texture exists for model: " + file.getName());
        }

        // Load nodes
        int[] sceneNodes = gson.fromJson(scene.get("nodes"), int[].class);
        JsonArray nodeJsonArray = root.get("nodes").getAsJsonArray();
        ArrayList<Node> rootNodes = new ArrayList<>();
        for (int index : sceneNodes) {
            rootNodes.add(loadNode(nodeJsonArray, index));
        }

        // Load animations
        HashMap<String, Animation> animations = new HashMap<>();
        if (root.has("animations")) {
            for (JsonElement element : root.get("animations").getAsJsonArray()) {
                JsonObject object = element.getAsJsonObject();
                String name = getName(object, "default");
                ArrayList<Sampler> samplers = gson.fromJson(object.get("samplers"), new TypeToken<ArrayList<Sampler>>(){}.getType());
                ArrayList<Channel> channels = new ArrayList<>();

                for (JsonElement channelElement : object.get("channels").getAsJsonArray()) {
                    JsonObject channelObject = channelElement.getAsJsonObject();
                    JsonObject targetObject = channelObject.get("target").getAsJsonObject();
                    Sampler sampler = samplers.get(channelObject.get("sampler").getAsInt());
                    channels.add(new Channel(
                            sampler,
                            accessors.get(sampler.output).type,
                            bufferViews.get(accessors.get(sampler.input).bufferView).getData().asFloatBuffer(),
                            // TODO: output data could be other then float, need to convert (non float ones are normalised)
                            bufferViews.get(accessors.get(sampler.output).bufferView).getData().asFloatBuffer(),
                            nodeCache.get(targetObject.get("node").getAsInt()),
                            gson.fromJson(targetObject.get("path"), AnimationPath.class)
                    ));
                }
                animations.put(name, new Animation(channels));
            }
        }

        return new Model(new ArrayList<>(nodeCache.values()), rootNodes, animations, textures);
    }

    public static void clearCache() {
        meshCache.clear();
        nodeCache.clear();
        bufferViews.clear();
        accessors.clear();
    }

    private static String getName(JsonObject object, String defaultName) {
        if (object.has("name")) {
            return object.get("name").getAsString();
        }
        return defaultName;
    }

    private static Geometry loadPrimitive(MeshPrimitive primitive) {
        Geometry geometry = new Geometry(primitive.mode.gl);

        for (Map.Entry<MeshPrimitive.Attribute, Integer> attribute : primitive.attributes.entrySet()) {
            Accessor accessor = GltfLoader.accessors.get(attribute.getValue());
            int itemBytes = accessor.type.size * accessor.componentType.size;

            BufferView bufferView = bufferViews.get(accessor.bufferView);
            VertexBuffer buffer = new VertexBuffer(bufferView, accessor);
            geometry.setBuffer(attribute.getKey(), buffer);
        }

        if (primitive.indices != null) {
            Accessor accessor = GltfLoader.accessors.get(primitive.indices);
            BufferView bufferView = bufferViews.get(accessor.bufferView);
            VertexBuffer buffer = new VertexBuffer(bufferView, accessor);
            geometry.setIndices(buffer);
        }

        return geometry;
    }

    private static Node loadNode(JsonArray nodeJsonArray, int index) {
        if (nodeCache.containsKey(index)) {
            return nodeCache.get(index);
        }
        JsonObject nodeJson = nodeJsonArray.get(index).getAsJsonObject();
        Node node;
        ArrayList<Node> children = null;

        // Load children if there any
        if (nodeJson.has("children")) {
            int[] childrenIndexes = gson.fromJson(nodeJson.get("children"), int[].class);
            children = new ArrayList<>(childrenIndexes.length);
            for (int childIndex : childrenIndexes) {
                children.add(loadNode(nodeJsonArray, childIndex));
            }
        }

        // Load matrix, or TSR values 
        if (nodeJson.has("matrix")) {
            node = new Node(children, gson.fromJson(nodeJson.get("matrix"), float[].class));
        } else {
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
            node = new Node(children, translation, rotation, scale);
        }

        // NOTE: depends on meshes being preloaded into the cache
        if (nodeJson.has("mesh")) {
            node.setMesh(meshCache.get(nodeJson.get("mesh").getAsInt()));
        }

        nodeCache.put(index, node);
        return node;
    }

    private static int readUnsignedInt(DataInputStream stream) throws IOException {
        return Integer.reverseBytes(stream.readInt());
    }
}
