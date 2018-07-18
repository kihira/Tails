package uk.kihira.gltf;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import uk.kihira.gltf.spec.Accessor;
import uk.kihira.gltf.spec.BufferView;
import uk.kihira.gltf.spec.MeshPrimitive;
import uk.kihira.gltf.spec.MeshPrimitive.Attribute;
import uk.kihira.tails.common.IDisposable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

public final class Geometry implements IDisposable {
    private final int drawMode;
    private int vao = -1;
    private int vertexCount;

    private HashMap<Attribute, VertexBuffer> buffers = new HashMap<>();
    private IndiciesBuffer indiciesBuffer;

    public Geometry(int drawMode) throws IOException {
        this.drawMode = drawMode;
    }

    public void build() {
        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        for (Entry<Attribute, VertexBuffer> buffer : buffers.entrySet()) {
            buffer.getValue().bind(buffer.getKey().index);
            if (buffer.getKey() == Attribute.POSITION) {
                vertexCount = buffer.getValue().getCount();
            }
        }
        GL30.glBindVertexArray(0);
    }

    public void render() {
        if (vao == -1) {
            build();
        }
        GL30.glBindVertexArray(vao);

        if (indiciesBuffer != null) {
            indiciesBuffer.bind(0);
            GL11.glDrawElements(drawMode, indiciesBuffer.getCount(), indiciesBuffer.getType().gl, indiciesBuffer.getOffset());
        }
        else {
            GL11.glDrawArrays(drawMode, 0, vertexCount);
        }
    }

    @Override
    public void dispose() {
        GL15.glDeleteBuffers(vertexBuffer);
        if (indiciesBuffer > 0) GL15.glDeleteBuffers(indiciesBuffer);
    }
}