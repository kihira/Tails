package uk.kihira.tails.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import uk.kihira.gltf.Model;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.common.Tails;

import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.HashMap;

/**
 * The main class for handling rendering of parts
 */
public class PartRenderer
{
    private final Shader shader;
    private final FloatBuffer modelViewMatrixWorld;
    private final FloatBuffer tintBuffer;
    private final ArrayDeque<FloatBuffer> bufferPool;
    private final HashMap<OutfitPart, FloatBuffer> renders;

    public PartRenderer()
    {
        modelViewMatrixWorld = BufferUtils.createFloatBuffer(16);
        tintBuffer = BufferUtils.createFloatBuffer(9);
        bufferPool = new ArrayDeque<>();
        renders = new HashMap<>(16);
        shader = new Shader("threetint_vert", "threetint_frag");
        shader.registerUniform("tints");
    }

    /**
     * Gets a {@link FloatBuffer} from the pool. If there is none left, creates a new one
     *
     * @return
     */
    private FloatBuffer getFloatBuffer()
    {
        if (bufferPool.size() == 0) {
            return BufferUtils.createFloatBuffer(16);
        } else return bufferPool.pop();
    }

    /**
     * Returns a {@link FloatBuffer} back to the pool
     */
    private void freeFloatBuffer(FloatBuffer buffer)
    {
        bufferPool.add(buffer);
    }

    /**
     * Queues up a part to be rendered
     */
    public void render(MatrixStack matrixStack, OutfitPart part)
    {
        GL11.glPushMatrix();
        matrixStack.push();
        matrixStack.translate(part.mountOffset[0], part.mountOffset[1], part.mountOffset[2]);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(part.rotation[0]));
        matrixStack.rotate(Vector3f.YP.rotationDegrees(part.rotation[1]));
        matrixStack.rotate(Vector3f.YP.rotationDegrees(part.rotation[2] + 180f)); // todo need to find out why its being rotated 180 degrees so this fix is no longer required
        matrixStack.scale(part.scale[0], part.scale[1], part.scale[2]);

        FloatBuffer fb = getFloatBuffer();
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, fb);
        GL11.glPopMatrix();

        renders.put(part, fb);
    }

    /**
     * Renders the entire queue of parts
     */
    public void doRender(MatrixStack matrixStack)
    {
        if (renders.size() == 0) return;

        // Prepare OpenGL for rendering
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableDepthTest();
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelViewMatrixWorld);
        shader.use();

        for (HashMap.Entry<OutfitPart, FloatBuffer> entry : renders.entrySet())
        {
            OutfitPart outfitPart = entry.getKey();
            Part basePart = outfitPart.getPart();
            if (basePart == null) continue;
            Model model = basePart.getModel();
            if (model == null) continue;

            // Set tint colors
            tintBuffer.put(outfitPart.tint[0]);
            tintBuffer.put(outfitPart.tint[1]);
            tintBuffer.put(outfitPart.tint[2]);
            tintBuffer.flip();
            GlStateManager.uniform3f(shader.getUniform("tints"), tintBuffer);

            // Load texture and model matrix
            Minecraft.getInstance().getTextureManager().bindTexture(outfitPart.textureLoc);
            GL11.glLoadMatrixf(entry.getValue());
            model.render(matrixStack);

            if (Tails.DEBUG)
            {
                renderDebugGizmo();
            }

            freeFloatBuffer(entry.getValue());
            tintBuffer.clear();
        }
        renders.clear();

        unbindBuffersAndShader();

        GlStateManager.disableDepthTest();
        RenderHelper.disableStandardItemLighting();
        GL11.glLoadMatrixf(modelViewMatrixWorld);
    }

    private void renderDebugGizmo()
    {
        unbindBuffersAndShader();

        final int scale = 1;
        // TODO OpenGlHelper.renderDirections(scale);
    }

    /**
     * Helper method to clear OpenGL state related to VBOs and Shader programs
     */
    private void unbindBuffersAndShader()
    {
        GlStateManager.useProgram(0);
        glBindVertexArray(0);
        GlStateManager.bindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    /*
    Caching for vertex array binding
     */
    private static int vertexArray = 0;

    public static void glBindVertexArray(int vao)
    {
        if (vao != vertexArray) {
            GL30.glBindVertexArray(vao);
            vertexArray = vao;
        }
    }
}
