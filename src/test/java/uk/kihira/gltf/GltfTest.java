package uk.kihira.gltf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import uk.kihira.gltf.animation.Animation;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static uk.kihira.gltf.GltfLoader.*;

public class GltfTest
{
    @Test
    void LoadGlb_BoxAnimatedTestFile_LoadsSuccessfully()
    {
        final Logger log = LogManager.getLogger();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("BoxAnimated.glb"))
        {
            assertNotNull(is, "Test resource failed to load");
            assertDoesNotThrow(() -> LoadGlb(new DataInputStream(is), log));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    void LoadGlb_BoxAnimatedTestFile_ValidAnimations()
    {
        final Logger log = LogManager.getLogger();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("BoxAnimated.glb"))
        {
            assertNotNull(is, "Test resource failed to load");

            // Validate animations
            Model model = LoadGlb(new DataInputStream(is), log);
            HashMap<String, Animation> animations = model.getAnimations();
            assertThat(animations).containsOnlyKeys("default");

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    void LoadGlb_MagicNumberIsCorrect_DoesNotThrow()
    {
        ByteBuffer buf = ByteBuffer.allocate(4);
        putUnsignedInt(buf, GLTF_MAGIC);
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(buf.array()));

        assertThrows(IllegalArgumentException.class, () -> LoadGlb(stream, LogManager.getLogger()), "File specified is not in the GLB format!");
    }

    @Test
    void LoadGlb_VersionIsCorrect_DoesNotThrow()
    {
        ByteBuffer buf = ByteBuffer.allocate(8);
        putUnsignedInt(buf, GLTF_MAGIC);
        putUnsignedInt(buf, GLTF_VERSION);
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(buf.array()));

        assertThrows(IllegalArgumentException.class, () -> LoadGlb(stream, LogManager.getLogger()), "GLB File is not version 2");
    }

    private static void putUnsignedInt(ByteBuffer buf, int value)
    {
        buf.putInt(Integer.reverseBytes(value));
    }
}
