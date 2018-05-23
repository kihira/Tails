package uk.kihira.gltf;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

import uk.kihira.gltf.spec.BufferView;
import uk.kihira.gltf.spec.Accessor.ComponentType;
import uk.kihira.gltf.spec.MeshPrimitive.Attribute;
import uk.kihira.tails.common.IDisposable;

/**
 * This is basically an implementation of an Accessor that automatically sets up pointers
 */
public class VertexBuffer implements IDisposable {
    protected final BufferView bufferView;
    protected final ComponentType type;
    protected final Attribute attribute;
    protected final int offset;
    protected final int count;

    public VertexBuffer(BufferView bufferView, ComponentType type, Attribute attribute, int offset, int count) {
        this.bufferView = bufferView;
        this.type = type;
        this.attribute = attribute;
        this.offset = offset;
        this.count = count;
    }

    public void bind(int vertexIndex) {
        bufferView.bind();

        if (GLContext.getCapabilities().OpenGL30) {
            GL20.glEnableVertexAttribArray(vertexIndex);
            GL20.glVertexAttribPointer(vertexIndex, type.size, type.gl, false, bufferView.byteStride, offset);
        }
        else {
            switch (attribute) {
                case POSITION:
                    GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                    GL11.glVertexPointer(3, type.gl, bufferView.byteStride, offset);
                    break;
                case NORMAL:
                    GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                    GL11.glNormalPointer(type.gl, bufferView.byteStride, offset);
                    break;
                case TEXCOORD_0:
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    GL11.glTexCoordPointer(2, type.gl, bufferView.byteStride, offset);
                    break;
                case COLOR_0:
                    GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                    GL11.glColorPointer(3, type.gl, bufferView.byteStride, offset);
                    break;
                default:
                    break;
            }
        }
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

	@Override
	public void dispose() {
		bufferView.dispose();
	}
}