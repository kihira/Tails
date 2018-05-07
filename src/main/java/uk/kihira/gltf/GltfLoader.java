package uk.kihira.gltf;

import com.google.gson.Gson;
import uk.kihira.gltf.spec.Gltf;

import java.io.*;

public class GltfLoader {
    private static final Gson gson = new Gson();
    private static final int JSON_CHUNK = 0x4E4F534A;
    private static final int BIN_CHUNK = 0x004E4942;

    public static Gltf LoadGltfFile(File file) throws FileNotFoundException {
        return gson.fromJson(new FileReader(file), Gltf.class);
    }

    public static Gltf LoadGlbFile(File file) throws IOException {
        DataInputStream stream = new DataInputStream(new FileInputStream(file));
        int magic = readUnsignedInt(stream);
        int version = readUnsignedInt(stream);
        int length = readUnsignedInt(stream);

        if (magic != 0x46546C67) throw new IllegalArgumentException("File specified is not in the GLB format!");
        if (version != 2) throw new IllegalArgumentException("GLB File is not version 2");

        Chunk chunk1 = new Chunk(readUnsignedInt(stream), readUnsignedInt(stream));
        if (stream.read(chunk1.data, 0, chunk1.length) != chunk1.length) {
            throw new IOException("Failed to properly read GLB file");
        }

        if (chunk1.length + 12 == length) {
            throw new IOException("GLB file is missing binary data");
        }

        Chunk chunk2 = new Chunk(readUnsignedInt(stream), readUnsignedInt(stream));
        if (stream.read(chunk2.data, 0, chunk2.length) != chunk2.length) {
            throw new IOException("Failed to properly read GLB file");
        }

        if (chunk1.type == JSON_CHUNK) return gson.fromJson(new String(chunk1.data), Gltf.class);
        else return gson.fromJson(new String(chunk2.data), Gltf.class);
    }

    private static int readUnsignedInt(DataInputStream stream) throws IOException {
        return Integer.reverseBytes(stream.readInt());
    }

    private static class Chunk {
        public int length;
        public int type;
        public byte[] data;

        public Chunk(int length, int type) {
            this.length = length;
            this.type = type;
            this.data = new byte[length];
        }
    }
}
