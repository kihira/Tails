package uk.kihira.gltf.spec;

import org.lwjgl.opengl.GL15;

import uk.kihira.tails.common.IDisposable;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class BufferView implements IDisposable {
    /**
     * The length of the bufferView in bytes.
     */
    public int byteLength = 0;

    /**
     * The offset into the buffer in bytes.
     * Minimum: 0
     * Default: 0
     */
    public int byteOffset = 0;

    /**
     * The stride, in bytes, between vertex attributes.
     * When this is not defined, data is tightly packed.
     * When two or more accessors use the same bufferView, this field must be defined.
     *
     * Minimum: 4
     * Maximum: 252
     * Multiple Of: 4
     */
    public int byteStride = 0;

    /**
     * The target that the GPU buffer should be bound to.
     *
     * Values: 34962 (ARRAY_BUFFER), 34963 (ELEMENT_ARRAY_BUFFER)
     */
    @Nullable
    public Integer target;

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
            vbo = GL15.glGenBuffers();
            GL15.glBindBuffer(target, vbo);
            GL15.glBufferData(target, data, GL15.GL_STATIC_DRAW);
        }
        else {
            GL15.glBindBuffer(target, vbo);
        }
    }

	@Override
	public void dispose() {
        if (vbo != -1) {
            GL15.glDeleteBuffers(vbo);
            vbo = -1;
        }
	}
}