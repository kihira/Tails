package uk.kihira.gltf;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * A set of primitives to be rendered.
 * A node can contain one mesh.
 * A node's transform places the mesh in the scene.
 */
class Mesh {
    /**
     * An array of primitives, each defining geometry to be rendered with a material.
     */
    @Nonnull
    public ArrayList<MeshPrimitive> primitives;
}