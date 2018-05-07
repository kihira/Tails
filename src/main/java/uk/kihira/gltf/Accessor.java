package uk.kihira.gltf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A typed view into a bufferView.
 * 
 * A bufferView contains raw binary data.
 * An accessor provides a typed view into a bufferView or a subset of a bufferView similar to how WebGL's `vertexAttribPointer()` defines an attribute in a buffer.
 */
class Accessor {
    /**
     * The index of the bufferView. 
     * When not defined, accessor must be initialized with zeros; 
     * `sparse` property or extensions could override zeros with actual values.
     */
    @Nullable
    public Integer bufferView;

    /**
     * The offset relative to the start of the bufferView in bytes.
     * This must be a multiple of the size of the component datatype.
     * 
     * Minimum: 0
     * Default: 0
     */
    @Nullable
    public Integer byteOffset;

    /**
     * The datatype of components in the attribute.
     * 
     * All valid values correspond to WebGL enums.
     * The corresponding typed arrays are `Int8Array`, `Uint8Array`, `Int16Array`, `Uint16Array`, `Uint32Array`, and `Float32Array`, respectively.
     * 5125 (UNSIGNED_INT) is only allowed when the accessor contains indices, i.e., the accessor is only referenced by `primitive.indices`.
     *
     * Values: 5120 (BYTE), 5121 (UNSIGNED_BYTE), 5122 (SHORT), 5123 (UNSIGNED_SHORT), 5125 (UNSIGNED_INT), 5126 (FLOAT)
     */
    @Nonnull
    public int componentType;

    /**
     * Specifies whether integer data values should be normalized (`true`) to [0, 1] (for unsigned types) or [-1, 1] (for signed types), or converted directly (`false`) when they are accessed. 
     * This property is defined only for accessors that contain vertex attributes or animation output data.
     */
    public boolean normalized = false;

    /**
     * The number of attributes referenced by this accessor, not to be confused with the number of bytes or number of components.
     * 
     * Minimum: 1
     */
    @Nonnull
    public int count;

    /**
     * Specifies if the attribute is a scalar, vector, or matrix.
     */
    @Nonnull
    public Type type;

    /**
     * Maximum value of each component in this attribute.  
     * 
     * Array elements must be treated as having the same data type as accessor's `componentType`. 
     * Both min and max arrays have the same length.  The length is determined by the value of the type property; it can be 1, 2, 3, 4, 9, or 16.\n\n`normalized` property has no effect on array values: they always correspond to the actual values stored in the buffer. 
     * When accessor is sparse, this property must contain max values of accessor data with sparse substitution applied.
     */
    @Nullable
    public float[] max;

    /**
     * Minimum value of each component in this attribute.
     * 
     * Array elements must be treated as having the same data type as accessor's `componentType`. Both min and max arrays have the same length.  
     * The length is determined by the value of the type property; it can be 1, 2, 3, 4, 9, or 16.\n\n`normalized` property has no effect on array values: they always correspond to the actual values stored in the buffer. 
     * When accessor is sparse, this property must contain min values of accessor data with sparse substitution applied.
     */
    @Nullable
    public float[] min;

    public enum Type {
        SCALAR,
        VEC2,
        VEC3,
        VEC4,
        MAT2,
        MAT3,
        MAT4
    }
}