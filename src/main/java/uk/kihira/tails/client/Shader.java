package uk.kihira.tails.client;

import com.google.common.base.Strings;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import uk.kihira.tails.common.Tails;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class Shader
{
    private final Map<String, Integer> uniforms;
    private int program;

    public Shader(String vertShader, String fragShader)
    {
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation vertRes = new ResourceLocation(Tails.MOD_ID, "shader/" + vertShader + ".glsl");
        ResourceLocation fragRes = new ResourceLocation(Tails.MOD_ID, "shader/" + fragShader + ".glsl");

        uniforms = new HashMap<>();

        // Load vert and frag shader source from files
        String vertSrc, fragSrc;
        try (InputStream is = mc.getResourceManager().getResource(vertRes).getInputStream())
        {
            vertSrc = TextureUtil.readResourceAsString(is);
        }
        catch (IOException e)
        {
            Tails.LOGGER.error("Failed to load vertex shader " + vertShader, e);
            return;
        }
        try (InputStream is = mc.getResourceManager().getResource(fragRes).getInputStream())
        {
            fragSrc = TextureUtil.readResourceAsString(is);
        }
        catch (IOException e)
        {
            Tails.LOGGER.error("Failed to load fragment shader " + vertShader, e);
            return;
        }

        assert !Strings.isNullOrEmpty(vertSrc);
        assert !Strings.isNullOrEmpty(fragSrc);

        // Create the shaders and compile
        int vert, frag;

        vert = GlStateManager.createShader(GL20.GL_VERTEX_SHADER);
        GlStateManager.shaderSource(vert, vertSrc);
        GlStateManager.compileShader(vert);
        checkShaderCompile(vert);

        frag = GlStateManager.createShader(GL20.GL_FRAGMENT_SHADER);
        GlStateManager.shaderSource(frag, fragSrc);
        GlStateManager.compileShader(frag);
        checkShaderCompile(frag);

        program = GlStateManager.createProgram();
        // bind attrib locations
        GL20.glBindAttribLocation(program, 0, "pos");
        GL20.glBindAttribLocation(program, 1, "normal");
        GL20.glBindAttribLocation(program, 2, "uv");
        // Attach shaders and links
        GlStateManager.attachShader(program, vert);
        GlStateManager.attachShader(program, frag);
        GlStateManager.linkProgram(program);

        int err = GlStateManager.getProgram(program, GL20.GL_LINK_STATUS);
        if (err != GL11.GL_TRUE)
        {
            String msg = GlStateManager.getProgramInfoLog(program, 1024);
            Tails.LOGGER.error("Failed to link program: " + msg);
        }

        // Cleanup shaders
        GlStateManager.deleteShader(vert);
        GlStateManager.deleteShader(frag);
    }

    public void use()
    {
        GlStateManager.useProgram(program);
    }

    /**
     * Registers a uniform that exists on the shader
     * TODO: check if this fails (loc is -1)
     *
     * @param name
     */
    public void registerUniform(String name)
    {
        int loc = GL20.glGetUniformLocation(program, name);
        uniforms.put(name, loc);
    }

    public int getUniform(String name)
    {
        return uniforms.get(name);
    }

    public void setTexture(ResourceLocation resourceLocation)
    {
        Minecraft.getInstance().textureManager.bindTexture(resourceLocation);
    }

    /**
     * Checks whether the shader has compiled correctly
     *
     * @param shader The shader ID
     */
    private void checkShaderCompile(int shader)
    {
        int err = GlStateManager.getShader(shader, GL20.GL_COMPILE_STATUS);
        if (err != GL11.GL_TRUE) {
            String msg = GlStateManager.getShaderInfoLog(shader, 1024);
            Tails.LOGGER.error("Failed to compile shader: " + msg);
        }
    }
}
