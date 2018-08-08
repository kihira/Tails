package uk.kihira.tails.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import uk.kihira.gltf.Model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.HashMap;

/**
 * The main class for handling rendering of parts
 */
public class PartRenderer {
    private final Shader shader;
    private final FloatBuffer modelViewMatrixWorld;
    private final IntBuffer tintBuffer;
    private final ArrayDeque<FloatBuffer> bufferPool;
    private final HashMap<OutfitPart, FloatBuffer> renders;

    public PartRenderer() {
        modelViewMatrixWorld = BufferUtils.createFloatBuffer(16);
        tintBuffer = BufferUtils.createIntBuffer(9);
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
        FloatBuffer fb = getFloatBuffer();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, fb);
        renders.put(part, fb);
    }

    /**
     * Renders the entire queue of parts
     */
    public void doRender() {
        if (renders.size() == 0) return;

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

            // todo load texture
            GL11.glLoadMatrix(entry.getValue());
            model.render();

            freeFloatBuffer(entry.getValue());
            tintBuffer.clear();
        }
        renders.clear();

        // Unbind everything
        OpenGlHelper.glUseProgram(0);
        glBindVertexArray(0);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);

        GL11.glLoadMatrix(modelViewMatrixWorld);
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
