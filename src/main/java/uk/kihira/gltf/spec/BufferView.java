package uk.kihira.gltf.spec;

import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;

/**
 * A view into a buffer generally representing a subset of the buffer.
 */
public class BufferView {
    /**
     * The length of the bufferView in bytes.
     */
    public int byteLength = 0;

    /**
     * The offset into the buffer in bytes.
     */
    public int byteOffset = 0;

    /**
     * The stride, in bytes, between vertex attributes.
     * When this is not defined, data is tightly packed.
     * When two or more accessors use the same bufferView, this field must be defined.
     */
    public int byteStride = 0;

    /**
     * The target that the GPU buffer should be bound to.
     *
     * Values: 34962 (ARRAY_BUFFER), 34963 (ELEMENT_ARRAY_BUFFER)
     */
    public Integer target = GL15.GL_ARRAY_BUFFER;

    /**
     * NON SPEC
     */
    private int vbo = -1;
    private ByteBuffer data;

    public void setData(ByteBuffer buffer) {
        if (vbo != -1) {
            // Force a rebuild of the buffer
            GL15.glDeleteBuffers(vbo);
            vbo = -1;
        }
        data = buffer;
    }

    public void bind() {
        if (vbo == -1) {
            vbo = GL15.glGenBuffers();
            GL15.glBindBuffer(target, vbo);
            GL15.glBufferData(target, data, GL15.GL_STATIC_DRAW);
        }
        else {
            GL15.glBindBuffer(target, vbo);
        }
    }
}