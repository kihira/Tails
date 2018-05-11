package uk.kihira.gltf;

import java.nio.ByteBuffer;

public class Attribute {
    private ByteBuffer buffer;
    private int stride = 0;
    private int type;

    public Attribute(ByteBuffer buffer, Integer stride, int type) {
        this.buffer = buffer;
        this.stride = stride;
        this.type = type;
    }

	/**
	 * @return the buffer
	 */
	public ByteBuffer getBuffer() {
		return buffer;
	}

	/**
	 * @return the stride
	 */
	public int getStride() {
		return stride;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
}