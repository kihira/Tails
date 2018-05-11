package uk.kihira.gltf;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

// TODO: things that have a shared buffer (ie strided stuff), upload the same thing twice.
// Should maybe upload the BufferView instead and have a flag for it?
class Geometry {
    private int drawMode = GL11.GL_TRIANGLES;

    private Attribute positionBuffer;
    private Attribute normalBuffer;
    private Attribute texCoordBuffer;
    private Attribute indiciesBuffer;

    private int positionBufferId;
    private int normalBufferId;
    private int texCoordBufferId;
    private int indiciesBufferId;

	/**
	 * @param positionBuffer the positionBuffer to set
	 */
	public void setPositionBuffer(Attribute positionBuffer) {
		this.positionBuffer = positionBuffer;
	}

	/**
	 * @param normalBuffer the normalBuffer to set
	 */
	public void setNormalBuffer(Attribute normalBuffer) {
		this.normalBuffer = normalBuffer;
	}

	/**
	 * @param texCoordBuffer the texCoordBuffer to set
	 */
	public void setTexCoordBuffer(Attribute texCoordBuffer) {
		this.texCoordBuffer = texCoordBuffer;
	}

	/**
	 * @param indiciesBuffer the indiciesBuffer to set
	 */
	public void setIndiciesBuffer(Attribute indiciesBuffer) {
		this.indiciesBuffer = indiciesBuffer;
    }

    // Not used currently
    public void uploadBuffers() {
        positionBufferId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer.getBuffer(), GL15.GL_STATIC_DRAW);

        normalBufferId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalBuffer.getBuffer(), GL15.GL_STATIC_DRAW);

        texCoordBufferId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texCoordBufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texCoordBuffer.getBuffer(), GL15.GL_STATIC_DRAW);

        indiciesBufferId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indiciesBufferId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiciesBuffer.getBuffer(), GL15.GL_STATIC_DRAW);
    }

    public void render() {
        // this is an older way of rendering (immediate mode) but its similar to how MC does it
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, positionBuffer.getStride(), positionBuffer.getBuffer());

        if (normalBuffer != null) {
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glVertexPointer(3, GL11.GL_FLOAT, normalBuffer.getStride(), normalBuffer.getBuffer());
        }
        if (texCoordBuffer != null) {
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GL11.glNormalPointer(GL11.GL_FLOAT, texCoordBuffer.getStride(), texCoordBuffer.getBuffer()); // TODO could be either float or short
        }

        if (indiciesBuffer != null) {
            GL11.glDrawElements(drawMode, indiciesBuffer.getBuffer());
        } else {
            GL11.glDrawArrays(drawMode, indiciesBuffer.getStride(), positionBuffer.getBuffer().remaining() / 3);
        }
    }
}