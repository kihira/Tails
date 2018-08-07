package uk.kihira.tails.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import uk.kihira.tails.common.Tails;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
public class Shader {
    private int program;
    private Map<String, Integer> uniforms;

    public Shader(String vertShader, String fragShader) {
        Minecraft mc = Minecraft.getMinecraft();
        ResourceLocation vertRes = new ResourceLocation(Tails.MOD_ID, "shader/" + vertShader + ".glsl");
        ResourceLocation fragRes = new ResourceLocation(Tails.MOD_ID, "shader/" + fragShader + ".glsl");

        uniforms = new HashMap<>();

        // Load vert and frag shader source from files
        ByteBuffer vertSrc, fragSrc;
        try (InputStream is = mc.getResourceManager().getResource(vertRes).getInputStream()) {
            byte[] bytes = IOUtils.toByteArray(is);
            vertSrc = ByteBuffer.allocateDirect(bytes.length);
            vertSrc.put(bytes);
            vertSrc.position(0);
        } catch (IOException e) {
            Tails.logger.error("Failed to load vertex shader " + vertShader, e);
            return;
        }
        try (InputStream is = mc.getResourceManager().getResource(fragRes).getInputStream()) {
            byte[] bytes = IOUtils.toByteArray(is);
            fragSrc = ByteBuffer.allocateDirect(bytes.length);
            fragSrc.put(bytes);
            fragSrc.position(0);
        } catch (IOException e) {
            Tails.logger.error("Failed to load fragment shader " + vertShader, e);
            return;
        }

        // Create the shaders and compile
        int vert, frag;

        vert = OpenGlHelper.glCreateShader(OpenGlHelper.GL_VERTEX_SHADER);
        OpenGlHelper.glShaderSource(vert, vertSrc);
        OpenGlHelper.glCompileShader(vert);
        checkShaderCompile(vert);

        frag = OpenGlHelper.glCreateShader(OpenGlHelper.GL_FRAGMENT_SHADER);
        OpenGlHelper.glShaderSource(frag, fragSrc);
        OpenGlHelper.glCompileShader(frag);
        checkShaderCompile(frag);

        program = OpenGlHelper.glCreateProgram();
        // bind attrib locations
        GL20.glBindAttribLocation(program, 0, "pos");
        GL20.glBindAttribLocation(program, 1, "normal");
        GL20.glBindAttribLocation(program, 2, "uv");
        // Attach shaders and links
        OpenGlHelper.glAttachShader(program, vert);
        OpenGlHelper.glAttachShader(program, frag);
        OpenGlHelper.glLinkProgram(program);

        int err = OpenGlHelper.glGetProgrami(program, GL20.GL_LINK_STATUS);
        if (err != GL11.GL_TRUE) {
            String msg = OpenGlHelper.glGetProgramInfoLog(program, 1024);
            Tails.logger.error("Failed to link program: " + msg);
        }

        // Cleanup shaders
        OpenGlHelper.glDeleteShader(vert);
        OpenGlHelper.glDeleteShader(frag);
    }

    public void use() {
        OpenGlHelper.glUseProgram(program);
    }

    /**
     * Registers a uniform that exists on the shader
     * TODO: check if this fails (loc is -1)
     *
     * @param name
     */
    public void registerUniform(String name) {
        int loc = GL20.glGetUniformLocation(program, name);
        uniforms.put(name, loc);
    }

    public int getUniform(String name) {
        return uniforms.get(name);
    }

    public void setTexture(ResourceLocation resourceLocation) {
        Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation);
    }

    /**
     * Checks whether the shader has compiled correctly
     *
     * @param shader The shader ID
     */
    private void checkShaderCompile(int shader) {
        int err = OpenGlHelper.glGetShaderi(shader, OpenGlHelper.GL_COMPILE_STATUS);
        if (err != GL11.GL_TRUE) {
            String msg = OpenGlHelper.glGetShaderInfoLog(shader, 1024);
            Tails.logger.error("Failed to compile shader: " + msg);
        }
    }
}
