package uk.kihira.gltf.spec;

import javax.annotation.Nullable;

// TODO: when loading, should always load stuff into either TSR or matrix
public class Node {
    /**
     * The indices of this node's children.
     */
    @Nullable
    public int[] children;

    /**
     * A floating-point 4x4 transformation matrix stored in column-major order.
     */
    @Nullable
    public float[] matrix;

    /**
     * The index of the mesh in this node.
     */
    @Nullable
    public Integer mesh;

    /**
     * The node's unit quaternion rotation in the order (x, y, z, w), where w is the scalar.
     */
    @Nullable
    public float[] rotation;

    /**
     * The node's non-uniform scale, given as the scaling factors along the x, y, and z axes.
     */
    @Nullable
    public float[] scale;

    /**
     * The node's translation along the x, y, and z axes.
     */
    @Nullable
    public float[] translation;
}