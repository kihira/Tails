package uk.kihira.gltf;

import javax.annotation.Nullable;

/**
 * A buffer points to binary geometry, animation, or skins.
 */
public class Buffer {
    /**
     * The uri of the buffer.
     */
    @Nullable
    public String uri;

    /**
     * The length of the buffer in bytes.
     */
    public int byteLength;
}