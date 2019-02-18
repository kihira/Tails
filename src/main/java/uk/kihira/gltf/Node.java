package uk.kihira.gltf;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.BufferUtils;
import uk.kihira.tails.common.IDisposable;

import javax.annotation.Nullable;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * NodeImpl can just directly refer to geometries as we don't need to store weights
 */
@OnlyIn(Dist.CLIENT)
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
        this.matrix.read(FloatBuffer.wrap(matrix));
        this.matrix.write(fb);
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
            matrix.mul(getRotateMatrix());
            matrix.scale(scale);
            matrix.write(fb);
            fb.rewind();
        }

        GlStateManager.pushMatrix();
        GlStateManager.multMatrixf(fb);

        if (mesh != null) {
            mesh.render();
        }

        if (children != null) {
            children.forEach(Node::render);
        }

        GlStateManager.popMatrix();
    }

    // todo cache rotation
    private Matrix4f getRotateMatrix() {
        return new Matrix4f(rotation);
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public void dispose() {
        mesh.dispose();
    }
}