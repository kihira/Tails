package uk.kihira.gltf;

import uk.kihira.gltf.spec.Accessor;

public class Attribute {
    private int offset;
    private int stride;
    private int bufferView;
    private Accessor.ComponentType type;

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