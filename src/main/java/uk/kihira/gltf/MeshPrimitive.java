package uk.kihira.gltf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

class MeshPrimitive {
    /**
     * A dictionary object, where each key corresponds to mesh attribute semantic and each value is the index of the accessor containing attribute's data.
     */
    @Nonnull
    public HashMap<String, Integer> attributes;

    /**
     * The index of the accessor that contains mesh indices.
     * 
     * When this is not defined, the primitives should be rendered without indices using `drawArrays()`.
     * When defined, the accessor must contain indices: the `bufferView` referenced by the accessor should have a `target` equal to 34963 (ELEMENT_ARRAY_BUFFER); 
     * `componentType` must be 5121 (UNSIGNED_BYTE), 5123 (UNSIGNED_SHORT) or 5125 (UNSIGNED_INT), the latter may require enabling additional hardware support; 
     * `type` must be `"SCALAR"`. For triangle primitives, the front face has a counter-clockwise (CCW) winding order.
     */
    @Nullable
    public Integer indicies;

    /**
     * The index of the material to apply to this primitive when rendering.
     */
    @Nullable
    public Integer material;

    /**
     * The type of primitives to render. All valid values correspond to WebGL enums.
     * 
     * Default: 4
     * Values: 0 (POINTS), 1 (LINES), 2 (LINE_LOOP), 3 (LINE_STRIP), 4 (TRIANGLES), 5 (TRIANGLE_STRIP), 6 (TRIANGLE_FAN)
     */
    public int mode = 4;
}