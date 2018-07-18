package uk.kihira.gltf;

import org.lwjgl.opengl.GL20;

import uk.kihira.gltf.spec.BufferView;
import uk.kihira.gltf.spec.Accessor.ComponentType;
import uk.kihira.tails.common.IDisposable;

/**
 * This is basically an implementation of an Accessor
 */
public class VertexBuffer {
    protected final BufferView bufferView;
    protected final ComponentType type;
    protected final int offset;
    protected final int count;

    public VertexBuffer(BufferView bufferView, ComponentType type, int offset, int count) {
        this.bufferView = bufferView;
        this.type = type;
        this.offset = offset;
        this.count = count;
    }

    public void bind(int vertexIndex) {
        bufferView.bind();
        GL20.glEnableVertexAttribArray(vertexIndex);
        GL20.glVertexAttribPointer(vertexIndex, type.size, type.gl, false, bufferView.byteStride, offset);
    }

    public BufferView getBufferView() {
        return bufferView;
    }

    public int getStride() {
        return bufferView.byteStride;
    }

    public int getOffset() {
        return offset;
    }

    public ComponentType getType() {
        return type;
    }

    public int getCount() {
        return count;
    }
}