package uk.kihira.gltf;

import org.lwjgl.opengl.GL20;
import uk.kihira.gltf.spec.Accessor;
import uk.kihira.gltf.spec.Accessor.ComponentType;
import uk.kihira.gltf.spec.BufferView;
import uk.kihira.tails.common.IDisposable;

/**
 * This is basically an implementation of an Accessor that automatically sets up pointers
 */
public class VertexBuffer implements IDisposable {
    protected final BufferView bufferView;
    protected final Accessor accessor;

    public VertexBuffer(BufferView bufferView, Accessor accessor) {
        this.bufferView = bufferView;
        this.accessor = accessor;
    }

    public void bind(int vertexIndex) {
        bufferView.bind();

        GL20.glEnableVertexAttribArray(vertexIndex);
        GL20.glVertexAttribPointer(vertexIndex, accessor.type.size, accessor.componentType.gl, false, bufferView.byteStride, accessor.byteOffset);
    }

    public BufferView getBufferView() {
        return bufferView;
    }

    public int getStride() {
        return bufferView.byteStride;
    }

    public int getOffset() {
        return accessor.byteOffset;
    }

    public ComponentType getComponentType() {
        return accessor.componentType;
    }

    public int getCount() {
        return accessor.count;
    }

    @Override
    public void dispose() {
        bufferView.dispose();
    }
}