package uk.kihira.gltf;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * NodeImpl can just directly refer to geometries as we don't need to store weights
 */
@ParametersAreNonnullByDefault
public final class Node {
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

    public Node(@Nullable ArrayList<Node> children, float[] matrix) {
        this(children);
        this.isStatic = true;
        this.matrix.load(FloatBuffer.wrap(matrix));
        this.matrix.store(fb);
        fb.rewind();
    }

    public Node(@Nullable ArrayList<Node> children, float[] translation, float[] rotation, float[] scale) {
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
            Matrix4f.mul(matrix, rotate(rotation), matrix);
            matrix.scale(scale);
            matrix.store(fb);
            fb.rewind();
        }

        GlStateManager.pushMatrix();
        GlStateManager.multMatrix(fb);

        if (mesh != null) {
            mesh.render();
        }

        for (Node node : children) {
            node.render();
        }

        GlStateManager.popMatrix();
    }

    private Matrix4f rotate(Quaternion q) {
        Matrix4f rotMatrix = new Matrix4f();
        rotMatrix.m00 = q.x*q.x+q.y*q.y-q.z*q.z-q.w*q.w;
        rotMatrix.m01 = (2f*q.y*q.z)+(2f*q.x*q.w);
        rotMatrix.m02 = (2f*q.y*q.w)+(2f*q.x*q.z);
        rotMatrix.m03 = 0f;
        rotMatrix.m10 = (2f*q.y*q.z)-(2f*q.x*q.w);
        rotMatrix.m11 = q.x*q.x-q.y*q.y+q.z*q.z-q.w*q.w;
        rotMatrix.m12 = (2f*q.z*q.w)+(2f*q.x*q.y);
        rotMatrix.m13 = 0f;
        rotMatrix.m20 = (2f*q.y*q.w)+(2f*q.x*q.z);
        rotMatrix.m21 = (2f*q.z*q.w)-(2f*q.x*q.y);
        rotMatrix.m22 = q.x*q.x-q.y*q.y-q.z*q.z+q.w*q.w;
        rotMatrix.m23 = 0f;
        rotMatrix.m30 = 0f;
        rotMatrix.m31 = 0f;
        rotMatrix.m32 = 0f;
        rotMatrix.m33 = 1f;
        return rotMatrix;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}