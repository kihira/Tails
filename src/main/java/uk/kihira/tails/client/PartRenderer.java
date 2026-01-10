package uk.kihira.tails.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.Tails;
import uk.kihira.tails.client.render.LegacyLayerPart;

import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.BiFunction;

/**
 * The main class for handling rendering of parts
 */
public class PartRenderer
{
    private final Shader shader;
    private final FloatBuffer modelViewMatrixWorld;
    private final FloatBuffer tintBuffer;
    private final MappableRingBuffer ubo = new MappableRingBuffer(() -> "Part UBO", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_MAP_WRITE, new Std140SizeCalculator().putVec3().putVec3().putVec3().get());
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
/*        RenderSystem.assertOnRenderThread();
        if (renders.isEmpty())
        {
            return;
        }

        var renderPipeline = LegacyLayerPart.PART_PIPELINE;

        try (var mappedView = RenderSystem.getDevice()
                .createCommandEncoder()
                .mapBuffer(this.ubo.currentBuffer(), false, true))
        {
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

                Std140Builder.intoBuffer(mappedView.data())
                        .putVec3(outfitPart.tint[0][0], outfitPart.tint[0][1], outfitPart.tint[0][2])
                        .putVec3(outfitPart.tint[1][0], outfitPart.tint[1][1], outfitPart.tint[1][2])
                        .putVec3(outfitPart.tint[2][0], outfitPart.tint[2][1], outfitPart.tint[2][2]);

                var bufferSlice = RenderSystem.getDynamicUniforms()
                        .writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f());
                var rendertarget = Minecraft.getInstance().getMainRenderTarget();
                var rendersystem$autostorageindexbuffer = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
                var gpubuffer = rendersystem$autostorageindexbuffer.getBuffer(6 * this.quadCount);
                var colorTextureView = rendertarget.getColorTextureView();
                var depthTextureView = rendertarget.getDepthTextureView();

                try (RenderPass renderpass = RenderSystem.getDevice()
                        .createCommandEncoder()
                        .createRenderPass(() -> "Tails Parts", colorTextureView, OptionalInt.empty(), depthTextureView, OptionalDouble.empty())) {
                    renderpass.setPipeline(renderPipeline);
                    RenderSystem.bindDefaultUniforms(renderpass);
                    renderpass.setUniform("DynamicTransforms", bufferSlice);
                    renderpass.setIndexBuffer(gpubuffer, rendersystem$autostorageindexbuffer.type());
                    renderpass.setUniform("PartData", this.ubo.currentBuffer());
                    renderpass.drawIndexed(0, 0, 6 * this.quadCount, 1);
                }

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
        }*/
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

    public static final RenderPipeline PART_PIPELINE =
            RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(Tails.MOD_ID, "pipeline/tails_part"))
                    .withVertexShader(Identifier.fromNamespaceAndPath(Tails.MOD_ID, "shader/entity_threetint"))
                    .withFragmentShader(Identifier.fromNamespaceAndPath(Tails.MOD_ID, "shader/entity_threetint"))
                    .withSampler("Sampler0")
                    .withCull(false)
                    .withUniform("PartData", UniformType.UNIFORM_BUFFER)
                    .withVertexFormat(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS)
                    .build();

    public static final BiFunction<Identifier, Boolean, RenderType> PART_RENDER_TYPE = Util.memoize(
            (texture, outline) -> {
                RenderSetup rendersetup = RenderSetup.builder(PART_PIPELINE)
                        .withTexture("Sampler0", texture)
                        .useLightmap()
                        .useOverlay()
                        .affectsCrumbling()
                        .sortOnUpload()
                        .setOutline(outline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
                        .createRenderSetup();
                return RenderType.create("tails_part", rendersetup);
            }
    );
}
