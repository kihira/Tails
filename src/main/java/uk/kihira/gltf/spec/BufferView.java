package uk.kihira.gltf.spec;

import net.minecraft.client.renderer.OpenGlHelper;
import uk.kihira.tails.common.IDisposable;

import java.nio.ByteBuffer;

public class BufferView implements IDisposable {
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
    public Integer target = OpenGlHelper.GL_ARRAY_BUFFER;

    /**
     * NON SPEC
     */
    private int vbo = -1;
    private ByteBuffer data;

    public void setData(ByteBuffer buffer) {
        dispose();
        data = buffer;
    }

    public void bind() {
        if (vbo == -1) {
            vbo = OpenGlHelper.glGenBuffers();
            OpenGlHelper.glBindBuffer(target, vbo);
            OpenGlHelper.glBufferData(target, data, OpenGlHelper.GL_STATIC_DRAW);
        }
        else {
            OpenGlHelper.glBindBuffer(target, vbo);
        }
    }

    public ByteBuffer getData() {
        return data;
    }

    @Override
	public void dispose() {
        if (vbo != -1) {
            OpenGlHelper.glDeleteBuffers(vbo);
            vbo = -1;
        }
	}
}