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

import static uk.kihira.tails.client.PartRenderer.checkError;

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

        vert = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vert, vertSrc);
        GL20.glCompileShader(vert);
        checkShaderCompile(vert);

        frag = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(frag, fragSrc);
        GL20.glCompileShader(frag);
        checkShaderCompile(frag);

        // Create program and link shaders
        program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vert);
        GL20.glAttachShader(program, frag);
        GL20.glLinkProgram(program);

        int err = GL20.glGetProgrami(program, GL20.GL_LINK_STATUS);
        if (err != GL11.GL_TRUE) {
            String msg = GL20.glGetProgramInfoLog(program, 1024);
            Tails.logger.error("Failed to link program: " + msg);
        }

        // Cleanup shaders
        GL20.glDeleteShader(vert);
        GL20.glDeleteShader(frag);
    }

    public void use() {
        checkError();
        OpenGlHelper.glUseProgram(program);
        checkError();
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
        int err = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
        if (err != GL11.GL_TRUE) {
            String msg = GL20.glGetShaderInfoLog(shader, 1024);
            Tails.logger.error("Failed to compile shader: " + msg);
        }
    }
}
