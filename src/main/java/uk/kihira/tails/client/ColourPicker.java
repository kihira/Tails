package uk.kihira.tails.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;
import uk.kihira.tails.Tails;
import uk.kihira.tails.client.gui.TintPanel;

import java.util.function.Consumer;

@EventBusSubscriber(modid = Tails.MOD_ID, value = Dist.CLIENT)
public class ColourPicker
{
    public static void startPickingColour(Consumer<Integer> colourPickedCallback)
    {
        ColourPicker.colourPickedCallback = colourPickedCallback;
        pickingColour = true;
    }

    public static void finishPickingColour(boolean successful)
    {
        if (successful)
        {
            colourPickedCallback.accept(activeColourSelected);
        }
        pickingColour = false;
    }

    public static boolean isPickingColour()
    {
        return pickingColour;
    }

    @SubscribeEvent
    public static void onRenderTickEnd(RenderFrameEvent.Post event)
    {
        if (pickingColour)
        {
            Screenshot.takeScreenshot(Minecraft.getInstance().getMainRenderTarget(), image ->
            {
                var x = Mth.floor(Minecraft.getInstance().mouseHandler.xpos());
                var y = Mth.floor(Minecraft.getInstance().mouseHandler.ypos());
                if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight())
                {
                    return;
                }

                activeColourSelected = image.getPixel(x, y);
                image.close();
            });
        }
    }

    @SubscribeEvent
    public static void renderScreenPost(ScreenEvent.Render.Post event)
    {
        if (pickingColour)
        {
            int mouseX = (int) Minecraft.getInstance().mouseHandler.getScaledXPos(Minecraft.getInstance().getWindow());
            int mouseY = (int) Minecraft.getInstance().mouseHandler.getScaledYPos(Minecraft.getInstance().getWindow());
            final int x = mouseX + COLOUR_PREVIEW_OFFSET;
            final int y = mouseY + COLOUR_PREVIEW_OFFSET;
            event.getGuiGraphics().fill(x - 1, y - 1, x + COLOUR_PREVIEW_SIZE + 1, y + COLOUR_PREVIEW_SIZE + 1, Colour.BLACK);
            event.getGuiGraphics().fill(x, y, x + COLOUR_PREVIEW_SIZE, y + COLOUR_PREVIEW_SIZE, activeColourSelected);
        }
    }

    @SubscribeEvent
    public static void mouseButtonPressedPre(ScreenEvent.MouseButtonPressed.Pre event)
    {
        // Intercept left mouse button click to capture colour under mouse
        if (pickingColour && event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT)
        {
            finishPickingColour(true);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void keyPressedPre(ScreenEvent.KeyPressed.Pre event)
    {
        // Intercept left mouse button click to capture colour under mouse
        if (pickingColour && event.getKeyCode() == GLFW.GLFW_KEY_ESCAPE)
        {
            finishPickingColour(true);
            event.setCanceled(true);
        }
    }

    private static int activeColourSelected;
    private static boolean pickingColour;
    private static Consumer<Integer> colourPickedCallback;

    public static final int COLOUR_PREVIEW_SIZE = 10;
    public static final int COLOUR_PREVIEW_OFFSET = 3;
}
