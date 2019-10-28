package uk.kihira.tails.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
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
public class PartRenderer {
    private final Shader shader;
    private final FloatBuffer modelViewMatrixWorld;
    private final FloatBuffer tintBuffer;
    private final ArrayDeque<FloatBuffer> bufferPool;
    private final HashMap<OutfitPart, FloatBuffer> renders;

    public PartRenderer() {
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
    private FloatBuffer getFloatBuffer() {
        if (bufferPool.size() == 0) {
            return BufferUtils.createFloatBuffer(16);
        } else return bufferPool.pop();
    }

    /**
     * Returns a {@link FloatBuffer} back to the pool
     */
    private void freeFloatBuffer(FloatBuffer buffer) {
        bufferPool.add(buffer);
    }

    /**
     * Queues up a part to be rendered
     */
    public void render(OutfitPart part) {
        GL11.glPushMatrix();
        GlStateManager.translate(part.mountOffset[0], part.mountOffset[1], part.mountOffset[2]);
        GlStateManager.rotate(part.rotation[0], 1f, 0f, 0f);
        GlStateManager.rotate(part.rotation[1], 0f, 1f, 0f);
        GlStateManager.rotate(part.rotation[2] + 180f, 0f, 0f, 1f); // todo need to find out why its being rotated 180 degrees so this fix is no longer required
        GlStateManager.scale(part.scale[0], part.scale[1], part.scale[2]);

        FloatBuffer fb = getFloatBuffer();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, fb);
        GL11.glPopMatrix();

        renders.put(part, fb);
    }

    /**
     * Renders the entire queue of parts
     */
    public void doRender() {
        if (renders.size() == 0) return;

        // Prepare OpenGL for rendering
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.color(1f, 1f, 1f);
        GlStateManager.getFloat(GL11.GL_MODELVIEW_MATRIX, modelViewMatrixWorld);
        shader.use();

        for (HashMap.Entry<OutfitPart, FloatBuffer> entry : renders.entrySet()) {
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
            OpenGlHelper.glUniform3(shader.getUniform("tints"), tintBuffer);

            // Load texture and model matrix
            // Minecraft.getMinecraft().getTextureManager().bindTexture(outfitPart.texture);
            GL11.glLoadMatrix(entry.getValue());
            model.render();

            if (Tails.DEBUG)
            {
                renderDebugGizmo();
            }

            freeFloatBuffer(entry.getValue());
            tintBuffer.clear();
        }
        renders.clear();

        unbindBuffersAndShader();

        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
        GL11.glLoadMatrix(modelViewMatrixWorld);
    }

    private void renderDebugGizmo()
    {
        unbindBuffersAndShader();

        final int scale = 1;
        OpenGlHelper.renderDirections(scale);
    }

    /**
     * Helper method to clear OpenGL state related to VBOs and Shader programs
     */
    private void unbindBuffersAndShader()
    {
        OpenGlHelper.glUseProgram(0);
        glBindVertexArray(0);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
    }

    /*
    Caching for vertex array binding
     */
    private static int vertexArray = 0;

    public static void glBindVertexArray(int vao) {
        if (vao != vertexArray) {
            GL30.glBindVertexArray(vao);
            vertexArray = vao;
        }
    }
}
