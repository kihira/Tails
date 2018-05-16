package uk.kihira.gltf;

import uk.kihira.gltf.spec.Accessor;
import uk.kihira.gltf.spec.BufferView;

public class Attribute {
    private int offset;
    private int stride;
    private int bufferView;
    private Accessor.ComponentType type;

    public Attribute(Accessor accessor, BufferView bufferView) {
        this(accessor.byteOffset, bufferView.byteStride, accessor.bufferView, accessor.componentType);
    }

    public Attribute(int offset, int stride, int bufferView, Accessor.ComponentType type) {
        this.offset = offset;
        this.stride = stride;
        this.bufferView = bufferView;
        this.type = type;
    }

    public int getStride() {
        return stride;
    }

    public int getOffset() {
        return offset;
    }

    public int getBufferView() {
        return bufferView;
    }

    public Accessor.ComponentType getType() {
        return type;
    }
}