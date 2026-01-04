package uk.kihira.gltf.spec;

import com.mojang.blaze3d.opengl.GlStateManager;
import org.lwjgl.opengl.GL15;
import uk.kihira.tails.common.IDisposable;

import java.nio.ByteBuffer;

public class BufferView implements IDisposable
{
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
    private transient int vbo = -1;
    private transient ByteBuffer data;

    public void setData(ByteBuffer buffer)
    {
        dispose();
        data = buffer;
    }

    public void bind()
    {
        if (vbo == -1)
        {
            vbo = GlStateManager._glGenBuffers();
            GlStateManager._glBindBuffer(target, vbo);
            GlStateManager._glBufferData(target, data, GL15.GL_STATIC_DRAW);
        }
        else
        {
            GlStateManager._glBindBuffer(target, vbo);
        }
    }

    public ByteBuffer getData()
    {
        return data;
    }

    @Override
	public void dispose()
    {
        if (vbo != -1)
        {
            GlStateManager._glDeleteBuffers(vbo);
            vbo = -1;
        }
	}
}