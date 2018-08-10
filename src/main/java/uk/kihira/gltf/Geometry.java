package uk.kihira.gltf;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import uk.kihira.gltf.spec.MeshPrimitive.Attribute;
import uk.kihira.tails.client.PartRenderer;
import uk.kihira.tails.common.IDisposable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map.Entry;

@ParametersAreNonnullByDefault
public final class Geometry implements IDisposable {
    private final int drawMode;
    private int vao = -1;
    private int vertexCount;

    private HashMap<Attribute, VertexBuffer> buffers = new HashMap<>();
    private VertexBuffer indicesBuffer;

    public Geometry(int drawMode) {
        this.drawMode = drawMode;
    }

    private void bind() {
        if (GLContext.getCapabilities().OpenGL30) {
            vao = GL30.glGenVertexArrays();
            PartRenderer.glBindVertexArray(vao);
        }

        for (Entry<Attribute, VertexBuffer> buffer : buffers.entrySet()) {
            buffer.getValue().bind(buffer.getKey().index);
            if (buffer.getKey() == Attribute.POSITION) {
                vertexCount = buffer.getValue().getCount();
            }
        }

        if (indicesBuffer != null) {
            // Bypass bind on VertexBuffer
            indicesBuffer.bufferView.bind();
        }

        if (GLContext.getCapabilities().OpenGL30) {
            PartRenderer.glBindVertexArray(0);
        }
    }

    public void render() {
        if (GLContext.getCapabilities().OpenGL30) {
            if (vao == -1) {
                bind();
            }
            PartRenderer.glBindVertexArray(vao);
        }
        else {
            bind(); // Binds the buffers and pointers
        }

        if (indicesBuffer != null) {
            GL11.glDrawElements(drawMode, indicesBuffer.getCount(), indicesBuffer.getType().gl, indicesBuffer.getOffset());
        }
        else {
            GL11.glDrawArrays(drawMode, 0, vertexCount);
        }
    }

    public void setBuffer(Attribute key, VertexBuffer vertexBuffer) {
        dispose();
        buffers.put(key, vertexBuffer);
    }

    public void setIndices(@Nullable VertexBuffer vertexBuffer) {
        dispose();
        indicesBuffer = vertexBuffer;
        if (indicesBuffer != null) {
            indicesBuffer.bufferView.target = GL15.GL_ELEMENT_ARRAY_BUFFER;
            vertexCount = indicesBuffer.count;
        }
    }

    @Override
    public void dispose() {
        if (vao != -1) {
            GL30.glDeleteVertexArrays(vao);
            vao = -1;
        }
        buffers.values().forEach(VertexBuffer::dispose);
    }
}