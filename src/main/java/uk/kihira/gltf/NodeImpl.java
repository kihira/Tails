package uk.kihira.gltf;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import uk.kihira.gltf.spec.Node;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * NodeImpl can just directly refer to geometries as we don't need to store weights
 */
public class NodeImpl {
    private Matrix4f matrix;
    private FloatBuffer fb;
    private boolean isStatic; // This is set to true if there is no animation (ie a matrix is present in the gltf file)
    private ArrayList<NodeImpl> children;
    private ArrayList<Geometry> geometries;

    // These must be defined if we have an animation
    public Vector3f translation;
    public Quaternion rotation;
    public Vector3f scale;

    public NodeImpl(Node node) {
        this.fb = BufferUtils.createFloatBuffer(16);

        if (node.matrix != null) {
            matrix = new Matrix4f(node.matrix);
            matrix.store(fb);
            this.isStatic = true;
        } 
        else {
            matrix = new Matrix4f();
            matrix.setIdentity();

            translation = new Vector3f(node.translation[0], node.translation[1], node.translation[2]);
            rotation = new Quaternion(node.rotation[0], node.rotation[1], node.rotation[2], node.rotation[3]);
            scale = new Vector3f(node.scale[0], node.scale[1], node.scale[2]);
        }
    }

    public void render() {
        // Generate matrix if this is not static
        if (!isStatic) {
            matrix.translate(translation);
            Matrix4f.mul(matrix, rotate(rotation), matrix);
            matrix.scale(scale);
            matrix.store(fb);
        }

        GlStateManager.pushMatrix();
        GlStateManager.multMatrix(fb);

        for (Geometry geometry : geometries) {
            geometry.render();
        }

        for (NodeImpl node : children) {
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
}