package uk.kihira.gltf;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import uk.kihira.tails.common.IDisposable;

import javax.annotation.Nullable;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * NodeImpl can just directly refer to geometries as we don't need to store weights
 */
public final class Node implements IDisposable {
    private final Matrix4f matrix;
    private final FloatBuffer fb;
    private boolean isStatic; // This is set to true if there is no animation (ie a matrix is present in the gltf file)
    private Mesh mesh;
    private final ArrayList<Node> children;

    // These must be defined if we have an animation
    public Vector3f translation;
    public Quaternion rotation;
    public Vector3f scale;

    private Node(@Nullable ArrayList<Node> children) {
        this.fb = BufferUtils.createFloatBuffer(16);
        this.matrix = new Matrix4f();
        this.children = children;
    }

    Node(@Nullable ArrayList<Node> children, float[] matrix) {
        this(children);
        this.isStatic = true;
        this.matrix.load(FloatBuffer.wrap(matrix));
        this.matrix.store(fb);
        fb.rewind();
    }

    Node(@Nullable ArrayList<Node> children, float[] translation, float[] rotation, float[] scale) {
        this(children);
        this.translation = new Vector3f(translation[0], translation[1], translation[2]);
        this.rotation = new Quaternion(rotation[0], rotation[1], rotation[2], rotation[3]);
        this.scale = new Vector3f(scale[0], scale[1], scale[2]);
    }

    public void render() {
        // Generate matrix if this is not static
        if (!isStatic) {
            matrix.setIdentity();
            matrix.translate(translation);
            Matrix4f.mul(matrix, getRotateMatrix(), matrix);
            matrix.scale(scale);
            matrix.store(fb);
            fb.rewind();
        }

        GlStateManager.pushMatrix();
        GlStateManager.multMatrix(fb);

        if (mesh != null) {
            mesh.render();
        }

        if (children != null) {
            children.forEach(Node::render);
        }

        GlStateManager.popMatrix();
    }

    private Matrix4f getRotateMatrix() {
        Matrix4f rotMatrix = new Matrix4f();
        GlStateManager.quatToGlMatrix(fb, rotation);
        rotMatrix.load(fb);
        fb.clear();
        return rotMatrix;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public void dispose() {
        mesh.dispose();
    }
}