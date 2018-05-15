package uk.kihira.gltf.spec;

import com.google.gson.annotations.SerializedName;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * A typed view into a bufferView.
 * 
 * A bufferView contains raw binary data. An accessor provides a typed view into
 * a bufferView or a subset of a bufferView similar to how WebGL's
 * `vertexAttribPointer()` defines an attribute in a buffer.
 */
public class Accessor {
    /**
     * The index of the bufferView. When not defined, accessor must be initialized
     * with zeros; `sparse` property or extensions could override zeros with actual
     * values.
     */
    @Nullable
    public Integer bufferView;

    /**
     * The offset relative to the start of the bufferView in bytes. This must be a
     * multiple of the size of the component datatype.
     * 
     * Minimum: 0 Default: 0
     */
    public int byteOffset = 0;

    /**
     * The datatype of components in the attribute.
     * 
     * All valid values correspond to WebGL enums. The corresponding typed arrays
     * are `Int8Array`, `Uint8Array`, `Int16Array`, `Uint16Array`, `Uint32Array`,
     * and `Float32Array`, respectively. 5125 (UNSIGNED_INT) is only allowed when
     * the accessor contains indices, i.e., the accessor is only referenced by
     * `primitive.indices`.
     *
     * Values: 5120 (BYTE), 5121 (UNSIGNED_BYTE), 5122 (SHORT), 5123
     * (UNSIGNED_SHORT), 5125 (UNSIGNED_INT), 5126 (FLOAT)
     */
    @Nonnull
    public ComponentType componentType;

    /**
     * Specifies whether integer data values should be normalized (`true`) to [0, 1]
     * (for unsigned types) or [-1, 1] (for signed types), or converted directly
     * (`false`) when they are accessed. This property is defined only for accessors
     * that contain vertex attributes or animation output data.
     */
    public boolean normalized = false;

    /**
     * The number of attributes referenced by this accessor, not to be confused with
     * the number of bytes or number of components.
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
     * Array elements must be treated as having the same data type as accessor's
     * `componentType`. Both min and max arrays have the same length. The length is
     * determined by the value of the type property; it can be 1, 2, 3, 4, 9, or
     * 16.\n\n`normalized` property has no effect on array values: they always
     * correspond to the actual values stored in the buffer. When accessor is
     * sparse, this property must contain max values of accessor data with sparse
     * substitution applied.
     */
    @Nullable
    public float[] max;

    /**
     * Minimum value of each component in this attribute.
     * 
     * Array elements must be treated as having the same data type as accessor's
     * `componentType`. Both min and max arrays have the same length. The length is
     * determined by the value of the type property; it can be 1, 2, 3, 4, 9, or
     * 16.\n\n`normalized` property has no effect on array values: they always
     * correspond to the actual values stored in the buffer. When accessor is
     * sparse, this property must contain min values of accessor data with sparse
     * substitution applied.
     */
    @Nullable
    public float[] min;

    /**
     * Sparse storage of attributes that deviate from their initialization value.
     */
    @Nullable
    public ArrayList<Sparse> sparse;

    public enum Type {
        SCALAR(1), 
        VEC2(2), 
        VEC3(3), 
        VEC4(4), 
        MAT2(4), 
        MAT3(9), 
        MAT4(16);

        public final int size;

        Type(final int size) {
            this.size = size;
        }
    }

    public enum ComponentType {
        @SerializedName("5120")
        BYTE(1, GL11.GL_BYTE),
        @SerializedName("5121")
        UNSIGNED_BYTE(1, GL11.GL_UNSIGNED_BYTE),
        @SerializedName("5122")
        SHORT(2, GL11.GL_SHORT),
        @SerializedName("5123")
        UNSIGNED_SHORT(2, GL11.GL_UNSIGNED_SHORT),
        @SerializedName("5125")
        UNSIGNED_INT(4, GL11.GL_UNSIGNED_INT),
        @SerializedName("5126")
        FLOAT(4, GL11.GL_FLOAT);

        /**
         * Size in bytes
         */
        public final int size;
        /**
         * GL Code
         */
        public final int gl;

        ComponentType(final int size, final int gl) {
            this.size = size;
            this.gl = gl;
        }
    }

    /**
     * Sparse storage of attributes that deviate from their initialization value.
     */
    private static class Sparse {
        /**
         * The number of attributes encoded in this sparse accessor.
         * 
         * Minimum: 1
         */
        public int count;

        /**
         * Index array of size `count` that points to those accessor attributes that deviate from their initialization value. 
         * Indices must strictly increase.
         */
        @Nonnull
        public ArrayList<Indicies> indicies;

        /**
         * Array of size `count` times number of components, storing the displaced accessor attributes pointed by `indices`.
         * Substituted values must have the same `componentType` and number of components as the base accessor.
         */
        @Nonnull
        public ArrayList<Values> values;

        /**
         * Indices of those attributes that deviate from their initialization value.
         */
        private static class Indicies {
            /**
             * The index of the bufferView with sparse indices. Referenced bufferView can't
             * have ARRAY_BUFFER or ELEMENT_ARRAY_BUFFER target.
             */
            public int bufferView;

            /**
             * The offset relative to the start of the bufferView in bytes. Must be aligned.
             * 
             * Minimum: 0 Default: 0
             */
            public int byteOffset;

            /**
             * The indices data type. Valid values correspond to WebGL enums: `5121`
             * (UNSIGNED_BYTE), `5123` (UNSIGNED_SHORT), `5125` (UNSIGNED_INT).
             */
            public int componentType;
        }

        /**
         * Array of size `accessor.sparse.count` times number of components storing the
         * displaced accessor attributes pointed by `accessor.sparse.indices`.
         */
        private static class Values {
            /**
             * The index of the bufferView with sparse values. Referenced bufferView can't
             * have ARRAY_BUFFER or ELEMENT_ARRAY_BUFFER target.
             */
            public int bufferView;

            /**
             * The offset relative to the start of the bufferView in bytes. Must be aligned.
             * 
             * Minimum: 0 Default: 0
             */
            public int byteOffset;
        }
    }
}