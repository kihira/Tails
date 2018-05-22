package uk.kihira.gltf;

import uk.kihira.gltf.spec.BufferView;
import uk.kihira.gltf.spec.Accessor.ComponentType;

public class IndiciesBuffer extends VertexBuffer {

    public IndiciesBuffer(BufferView bufferView, ComponentType type, int offset, int count) {
        super(bufferView, type, offset, count);
    }

    public void bind(int vertexIndex) {
        bufferView.bind();
    }
}