package uk.kihira.tails.client;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Matrix4fStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.Tails;

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
        shader.registerUniform("ModelViewMat");
        shader.registerUniform("ProjMat");
    }

    /**
     * Gets a {@link FloatBuffer} from the pool. If there is none left, creates a new one
     *
     * @return
     */
    private FloatBuffer getFloatBuffer()
    {
        if (bufferPool.isEmpty()) {
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
    public void render(PoseStack poseStack, OutfitPart part)
    {
        poseStack.pushPose();
        //matrixStack.translate(part.mountOffset[0], part.mountOffset[1], part.mountOffset[2]);
        poseStack.rotateAround(Axis.XP.rotationDegrees(part.rotation[0]), part.mountOffset[0], part.mountOffset[1], part.mountOffset[2]);
        poseStack.rotateAround(Axis.YP.rotationDegrees(part.rotation[1]), part.mountOffset[0], part.mountOffset[1], part.mountOffset[2]);
        poseStack.rotateAround(Axis.YP.rotationDegrees(part.rotation[2] + 180f), part.mountOffset[0], part.mountOffset[1], part.mountOffset[2]); // todo need to find out why its being rotated 180 degrees so this fix is no longer required
        poseStack.scale(part.scale[0], part.scale[1], part.scale[2]);
        poseStack.scale(0.1f, 0.1f, 0.1f);


        FloatBuffer fb = getFloatBuffer();
        poseStack.last().pose().get(fb);
        poseStack.popPose();

        renders.put(part, fb);
    }

    /**
     * Renders the entire queue of parts
     */
    public void doRender(PoseStack poseStack)
    {
        RenderSystem.assertOnRenderThread();
        if (renders.isEmpty())
        {
            return;
        }

        // Prepare OpenGL for rendering
        //RenderSystem.enableStandardItemLighting();
        //GlStateManager._enableDepthTest();
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelViewMatrixWorld);
        shader.use();

        var modelViewMatrix = RenderSystem.getModelViewMatrix();

        for (HashMap.Entry<OutfitPart, FloatBuffer> entry : renders.entrySet())
        {
            OutfitPart outfitPart = entry.getKey();
            Part basePart = outfitPart.getPart();
            if (basePart == null) continue;
            var model = basePart.getModel();


            // Set tint colors
            tintBuffer.put(outfitPart.tint[0]);
            tintBuffer.put(outfitPart.tint[1]);
            tintBuffer.put(outfitPart.tint[2]);
            tintBuffer.flip();
            //GlStateManager._glUniform3(shader.getUniform("tints"), tintBuffer);

            FloatBuffer fb = MemoryUtil.memAllocFloat(16);
            RenderSystem.getModelViewMatrix().get(fb);
            //GlStateManager._glUniformMatrix4(shader.getUniform("ModelViewMat"), false, entry.getValue());

            fb.clear();
            //RenderSystem.getProjectionMatrix().get(fb);
            //GlStateManager._glUniformMatrix4(shader.getUniform("ProjMat"), false, fb);

            // Load texture and model matrix
            //Minecraft.getInstance().getTextureManager().bindForSetup(outfitPart.textureLoc);
            //GL11.glLoadMatrixf(entry.getValue());
            //RenderSystem.getModelViewMatrix().set(new Matrix4f(entry.getValue()));
            //RenderSystem.applyModelViewMatrix();

            var matrixStack = new Matrix4fStack(16);
            // TODO model.render(matrixStack);
            //poseStack.mulPoseMatrix(matrixStack);
            matrixStack.clear();

            if (Tails.DEBUG)
            {
                //renderDebugGizmo();
            }

            freeFloatBuffer(entry.getValue());
            tintBuffer.clear();
        }
        renders.clear();

        unbindBuffersAndShader();

        //GlStateManager._disableDepthTest();
        //RenderHelper.disableStandardItemLighting();
        //GL11.glLoadMatrixf(modelViewMatrixWorld);
        RenderSystem.getModelViewMatrix().set(modelViewMatrix);
        //RenderSystem.applyModelViewMatrix();
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
        GlStateManager._glUseProgram(0);
        glBindVertexArray(0);
        GlStateManager._glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
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
