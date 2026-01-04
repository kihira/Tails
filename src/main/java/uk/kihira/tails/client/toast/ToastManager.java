package uk.kihira.tails.client.toast;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;

public final class ToastManager
{
    public static final ToastManager INSTANCE = new ToastManager();

    private final ArrayList<Toast> toasts = new ArrayList<>();

    private ToastManager() 
    {
        NeoForge.EVENT_BUS.register(this);
    }

    public void createToast(int x, int y, String text)
    {
        Font fontRenderer = Minecraft.getInstance().font;
        int stringWidth = fontRenderer.width(text);
        this.toasts.add(new Toast(x, y, stringWidth + 10,  stringWidth * 3, text));
    }

    public void createCenteredToast(int x, int y, int maxWidth, String text)
    {
        Font fontRenderer = Minecraft.getInstance().font;
        int stringWidth = fontRenderer.width(text);
        if (stringWidth > maxWidth)
        {
            // TODO List<IReorderingProcessor> strings = fontRenderer.trimStringToWidth(new StringTextComponent(text), maxWidth);
            // TODO toasts.add(new Toast(x - (maxWidth / 2) - 5, y, maxWidth + 10, text.length() * 3, strings.toArray(new IReorderingProcessor[0])));
        }
        else
        {
            this.toasts.add(new Toast(x - (stringWidth / 2) - 5, y, stringWidth + 10, text.length() * 3, text));
        }
    }

    @SubscribeEvent
    public void onClientTickPost(ClientTickEvent.Post event)
    {
        this.toasts.removeIf(toast -> toast.time-- <= 0);
    }

    @SubscribeEvent
    public void onDrawScreenPost(ScreenEvent.Render.Post event)
    {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("toastNotification");

        for (Toast toast : toasts)
        {
            toast.drawToast(event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
        }

        profiler.pop();
    }
}
