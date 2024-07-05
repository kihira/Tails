package uk.kihira.tails.client.toast;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    public void onClientTickPost(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            this.toasts.removeIf(toast -> toast.time-- <= 0);
        }
    }

    @SubscribeEvent
    public void onDrawScreenPost(ScreenEvent.Render.Post event)
    {
        ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
        profiler.push("toastNotification");

        for (Toast toast : toasts)
        {
            toast.drawToast(event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
        }

        profiler.pop();
    }
}
