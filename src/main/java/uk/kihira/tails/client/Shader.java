package uk.kihira.tails.client;

import com.google.common.base.Strings;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import uk.kihira.tails.common.Tails;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        try (var inputStream = mc.getResourceManager().getResource(vertRes).get().open())
        {
            vertSrc = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            Tails.LOGGER.error("Failed to load vertex shader " + vertShader, e);
            return;
        }
        try (var inputStream = mc.getResourceManager().getResource(fragRes).get().open())
        {
            fragSrc = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            Tails.LOGGER.error("Failed to load fragment shader " + vertShader, e);
            return;
        }

        // Create the shaders and compile
        int vert, frag;

        vert = GlStateManager.glCreateShader(GL20.GL_VERTEX_SHADER);
        GlStateManager.glShaderSource(vert, Collections.singletonList(vertSrc));
        GlStateManager.glCompileShader(vert);
        checkShaderCompile(vert);

        frag = GlStateManager.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GlStateManager.glShaderSource(frag, Collections.singletonList(fragSrc));
        GlStateManager.glCompileShader(frag);
        checkShaderCompile(frag);

        program = GlStateManager.glCreateProgram();
        // bind attrib locations
        GL20.glBindAttribLocation(program, 0, "pos");
        GL20.glBindAttribLocation(program, 1, "normal");
        GL20.glBindAttribLocation(program, 2, "uv");
        // Attach shaders and links
        GlStateManager.glAttachShader(program, vert);
        GlStateManager.glAttachShader(program, frag);
        GlStateManager.glLinkProgram(program);

        int err = GlStateManager.glGetProgrami(program, GL20.GL_LINK_STATUS);
        if (err != GL11.GL_TRUE)
        {
            String msg = GlStateManager.glGetProgramInfoLog(program, 1024);
            Tails.LOGGER.error("Failed to link program: " + msg);
        }

        // Cleanup shaders
        GlStateManager.glDeleteShader(vert);
        GlStateManager.glDeleteShader(frag);
    }

    public void use()
    {
        GlStateManager._glUseProgram(program);
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
        Minecraft.getInstance().textureManager.bindForSetup(resourceLocation);
    }

    /**
     * Checks whether the shader has compiled correctly
     *
     * @param shader The shader ID
     */
    private void checkShaderCompile(int shader)
    {
        int err = GlStateManager.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
        if (err != GL11.GL_TRUE)
        {
            String msg = GlStateManager.glGetShaderInfoLog(shader, 1024);
            Tails.LOGGER.error("Failed to compile shader: {}", msg);
        }
    }
}
