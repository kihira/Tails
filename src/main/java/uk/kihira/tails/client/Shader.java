package uk.kihira.tails.client;

import com.mojang.blaze3d.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import uk.kihira.tails.Tails;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
public class Shader
{
    private final Map<String, Integer> uniforms;
    private int program;

    public Shader(String vertShader, String fragShader)
    {
        Minecraft mc = Minecraft.getInstance();
        Identifier vertRes = Identifier.fromNamespaceAndPath(Tails.MOD_ID, "shader/" + vertShader + ".glsl");
        Identifier fragRes = Identifier.fromNamespaceAndPath(Tails.MOD_ID, "shader/" + fragShader + ".glsl");

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
        GlStateManager.glShaderSource(vert, vertSrc);
        GlStateManager.glCompileShader(vert);
        checkShaderCompile(vert);

        frag = GlStateManager.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GlStateManager.glShaderSource(frag, fragSrc);
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

    public void setTexture(Identifier resourceLocation)
    {
        Minecraft.getInstance().getTextureManager().registerForNextReload(resourceLocation);
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
