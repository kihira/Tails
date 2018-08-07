package uk.kihira.tails.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import uk.kihira.gltf.Model;
import uk.kihira.tails.common.Tails;

import java.nio.FloatBuffer;
import java.util.HashMap;

/**
 * The main class for handling rendering of parts
 */
public class PartRenderer {
    private Shader shader;
    private FloatBuffer modelViewMatrixWorld;

    private HashMap<OutfitPart, FloatBuffer> renders;

    public PartRenderer() {
        renders = new HashMap<>(16);
        modelViewMatrixWorld = BufferUtils.createFloatBuffer(16);
        shader = new Shader("threetint_vert", "threetint_frag");
    }

    /**
     * Queues up a part to be rendered
     */
    public void render(OutfitPart part) {

        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, fb);
        renders.put(part, fb);
    }

    /**
     * Renders the entire queue of parts
     */
    public void doRender() {
        GlStateManager.getFloat(GL11.GL_MODELVIEW_MATRIX, modelViewMatrixWorld);
        shader.use();

        for (HashMap.Entry<OutfitPart, FloatBuffer> entry : renders.entrySet()) {
            Part part = entry.getKey().getPart();
            if (part == null) continue;
            Model model = part.getModel();
            if (model == null) continue;

            // todo load texture
            GL11.glLoadMatrix(entry.getValue());
            model.render();
        }
        renders.clear();

        GL11.glLoadMatrix(modelViewMatrixWorld);
        OpenGlHelper.glUseProgram(0);

        checkError();
    }

    public static void checkError() {
        int i = GlStateManager.glGetError();
        if (i != 0) {
            String s = GLU.gluErrorString(i);
            Tails.logger.error("########## GL ERROR ##########");
            Tails.logger.error("{}: {}", i, s);
        }
    }
}
