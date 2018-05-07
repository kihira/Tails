package uk.kihira.gltf;

import javax.annotation.Nullable;

/**
 * A view into a buffer generally representing a subset of the buffer.
 */
class BufferView {
    /**
     * The index of the buffer
     */
    public int buffer;

    /**
     * The length of the bufferView in bytes.
     */
    public int byteLength;
    
    /**
     * The offset into the buffer in bytes.
     * Minimum: 0
     * Default: 0
     */
    public int byteOffset = 0;

    /**
     * The stride, in bytes, between vertex attributes.
     * When this is not defined, data is tightly packed. 
     * When two or more accessors use the same bufferView, this field must be defined.
     * 
     * Minimum: 4
     * Maximum: 252
     * Multiple Of: 4
     */
    @Nullable
    public Integer byteStride;

    /**
     * The target that the GPU buffer should be bound to.
     * 
     * Values: 34962 (ARRAY_BUFFER), 34963 (ELEMENT_ARRAY_BUFFER)
     */
    @Nullable
    public Integer target;
}