package uk.kihira.gltf;

import com.google.gson.Gson;

import uk.kihira.gltf.spec.Accessor;
import uk.kihira.gltf.spec.BufferView;
import uk.kihira.gltf.spec.Gltf;
import uk.kihira.gltf.spec.Mesh;
import uk.kihira.gltf.spec.Mesh.Primitive;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.Map.Entry;

public class GltfLoader {
    private static final Gson gson = new Gson();
    private static final int JSON_CHUNK = 0x4E4F534A;
    private static final int BIN_CHUNK = 0x004E4942;

    // Temp cache values. bad practice, need to rewrite
    private static Gltf gltf;
    private static ByteBuffer binData;

    public static Gltf LoadGlbFile(File file) throws IOException {
        DataInputStream stream = new DataInputStream(new FileInputStream(file));
        int magic = readUnsignedInt(stream);
        int version = readUnsignedInt(stream);
        int length = readUnsignedInt(stream);

        if (magic != 0x46546C67)
            throw new IllegalArgumentException("File specified is not in the GLB format!");
        if (version != 2)
            throw new IllegalArgumentException("GLB File is not version 2");

        ReadChunk(stream);
        ReadChunk(stream);

        return gltf;
    }

    private static void ReadChunk(DataInputStream stream) throws IOException {
        int chunkLength = readUnsignedInt(stream);
        int chunkType = readUnsignedInt(stream);
        byte[] data = new byte[chunkLength];
        if (stream.read(data, 0, chunkLength) != chunkLength)
            throw new IOException("Failed to read GLB file");

        if (chunkType == JSON_CHUNK)
            gltf = gson.fromJson(new String(data), Gltf.class);
        else if (chunkType == BIN_CHUNK)
            binData = ByteBuffer.wrap(data);
        else
            throw new IOException("Unsupport chunk type");
    }

    /**
     * This automatically assumes that we're loading a GLB file (as shouldn't need
     * to support others)
     */
    private ByteBuffer LoadBufferView(Gltf gltf, ByteBuffer binData, int bufferViewIndex) {
        BufferView bufferView = gltf.bufferViews.get(bufferViewIndex);
        return (ByteBuffer) binData.slice().position(bufferView.byteOffset).limit(bufferView.byteLength);
    }

    private Attribute LoadAccessor(Gltf gltf, ByteBuffer binData, int accessorIndex) {
        Accessor accessor = gltf.accessors.get(accessorIndex);
        if (accessor.sparse != null)
            throw new IllegalArgumentException("Spare accessors are currently not supported");

        BufferView bufferView = gltf.bufferViews.get(accessor.bufferView);
        ByteBuffer bufferViewData = LoadBufferView(gltf, binData, accessor.bufferView);
        if (bufferView.byteStride != null) {
            return new Attribute((ByteBuffer) bufferViewData.slice().position(accessor.byteOffset)
                    .limit(accessor.count * accessor.type.size), bufferView.byteStride, accessor.type.size);
        } else {
            return new Attribute((ByteBuffer) bufferViewData.slice().position(accessor.byteOffset)
                    .limit(accessor.count * accessor.type.size), 0, accessor.type.size);
        }
    }

    private void LoadMesh(Gltf gltf, ByteBuffer binData, Mesh mesh) throws IOException {
        for (Primitive primitive : mesh.primitives) {
            Geometry geometry = new Geometry();

            // Load all attributes and their data
            for (Entry<Primitive.Attribute, Integer> attribute : primitive.attributes.entrySet()) {
                Attribute data = LoadAccessor(gltf, binData, attribute.getValue());
                switch (attribute.getKey()) {
                case POSITION:
                    geometry.setPositionBuffer(data);
                case NORMAL:
                    geometry.setNormalBuffer(data);
                case TEXCOORD_0:
                    geometry.setTexCoordBuffer(data);
                default:
                    throw new IOException("Attribute type not yet supported");
                }
            }

            // Load and set indicies if defined
            if (primitive.indicies != null) {
                geometry.setIndiciesBuffer(LoadAccessor(gltf, binData, primitive.indicies));
            }
        }
    }

    private static int readUnsignedInt(DataInputStream stream) throws IOException {
        return Integer.reverseBytes(stream.readInt());
    }

    public static void main(String[] args) {
        try {
            System.out.println(LoadGlbFile(Paths.get("./BoxTextured.glb").toFile()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
