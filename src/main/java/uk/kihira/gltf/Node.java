package uk.kihira.gltf;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.BufferUtils;
import uk.kihira.tails.common.IDisposable;

import javax.annotation.Nullable;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * NodeImpl can just directly refer to geometries as we don't need to store weights
 */
public final class Node implements IDisposable
{
    private Matrix4f matrix;
    private boolean isStatic; // This is set to true if there is no animation (ie a matrix is present in the gltf file)
    private Mesh mesh;
    private final ArrayList<Node> children;

    // These must be defined if we have an animation
    public Vector3f translation;
    public Quaternion rotation;
    public Vector3f scale;

    private Node(@Nullable ArrayList<Node> children)
    {
        this.matrix = new Matrix4f();
        this.children = children;
    }

    Node(@Nullable ArrayList<Node> children, float[] matrix)
    {
        this(children);
        this.isStatic = true;
        this.matrix = new Matrix4f(matrix);
    }

    Node(@Nullable ArrayList<Node> children, float[] translation, float[] rotation, float[] scale)
    {
        this(children);
        this.translation = new Vector3f(translation[0], translation[1], translation[2]);
        this.rotation = new Quaternion(rotation[0], rotation[1], rotation[2], rotation[3]);
        this.scale = new Vector3f(scale[0], scale[1], scale[2]);
    }

    public void render(MatrixStack matrixStack)
    {
        // Generate matrix if this is not static
        if (!this.isStatic)
        {
            this.matrix.setIdentity();
            this.matrix.translate(this.translation);
            this.matrix.mul(this.rotation);
            // todo this.matrix.scale(this.scale);
        }

        matrixStack.push();
        matrixStack.getLast().getMatrix().mul(matrix);

        if (mesh != null)
        {
            mesh.render();
        }

        if (children != null)
        {
            for (Node child: children)
            {
                child.render(matrixStack);
            }
        }

        matrixStack.pop();
    }

    public void setMesh(Mesh mesh)
    {
        this.mesh = mesh;
    }

    @Override
    public void dispose()
    {
        mesh.dispose();
    }
}