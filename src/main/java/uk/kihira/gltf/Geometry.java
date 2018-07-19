package uk.kihira.gltf;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import uk.kihira.gltf.spec.MeshPrimitive.Attribute;
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
    private VertexBuffer indiciesBuffer;

    public Geometry(int drawMode) {
        this.drawMode = drawMode;
    }

    private void build() {
        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        for (Entry<Attribute, VertexBuffer> buffer : buffers.entrySet()) {
            buffer.getValue().bind(buffer.getKey().index);
            if (buffer.getKey() == Attribute.POSITION) {
                vertexCount = buffer.getValue().getCount();
            }
        }

        if (indiciesBuffer != null) {
            // Bypass bind on VertexBuffer
            indiciesBuffer.bufferView.bind();
        }
        GL30.glBindVertexArray(0);
    }

    public void render() {
        if (GLContext.getCapabilities().OpenGL30) {
            if (vao == -1) {
                build();
            }
            GL30.glBindVertexArray(vao);
        }

        if (indiciesBuffer != null) {
            indiciesBuffer.bind(0);
            GL11.glDrawElements(drawMode, indiciesBuffer.getCount(), indiciesBuffer.getType().gl, indiciesBuffer.getOffset());
        }
        else {
            GL11.glDrawArrays(drawMode, 0, vertexCount);
        }
    }

    public void setBuffer(Attribute key, VertexBuffer vertexBuffer) {
        dispose();
        buffers.put(key, vertexBuffer);
    }

    public void setIndicies(@Nullable VertexBuffer vertexBuffer) {
        dispose();
        indiciesBuffer = vertexBuffer;
        if (indiciesBuffer != null) {
            indiciesBuffer.bufferView.target = GL15.GL_ELEMENT_ARRAY_BUFFER;
            vertexCount = indiciesBuffer.count;
        }
    }

    @Override
    public void dispose() {
        GL30.glDeleteVertexArrays(vao);
        vao = -1;
    }
}