package uk.kihira.gltf;

import com.google.gson.Gson;
import uk.kihira.gltf.spec.BufferView;
import uk.kihira.gltf.spec.Gltf;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.HashMap;

public class GltfLoader {
    private static final Gson gson = new Gson();
    private static final int JSON_CHUNK = 0x4E4F534A;
    private static final int BIN_CHUNK = 0x004E4942;

    // Temp cache values. bad practice, need to rewrite
    private static Gltf gltf;
    private static ByteBuffer binData;

    public static HashMap<BufferView, ByteBuffer> byteBufferCache = new HashMap<>();

    public static Gltf LoadGlbFile(File file) throws IOException {
        DataInputStream stream = new DataInputStream(new FileInputStream(file));
        int magic = readUnsignedInt(stream);
        int version = readUnsignedInt(stream);
        int length = readUnsignedInt(stream);

        if (magic != 0x46546C67) throw new IllegalArgumentException("File specified is not in the GLB format!");
        if (version != 2) throw new IllegalArgumentException("GLB File is not version 2");

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
    public static ByteBuffer LoadBufferView(Gltf gltf, ByteBuffer binData, int bufferViewIndex) {
        return LoadBufferView(gltf, binData, gltf.bufferViews.get(bufferViewIndex));
    }

    public static ByteBuffer LoadBufferView(Gltf gltf, ByteBuffer binData, BufferView bufferView) {
        if (byteBufferCache.containsKey(bufferView)) return byteBufferCache.get(bufferView);

        ByteBuffer buffer = (ByteBuffer) binData.slice().position(bufferView.byteOffset).limit(bufferView.byteLength);
        byteBufferCache.put(bufferView, buffer);
        return buffer;
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
