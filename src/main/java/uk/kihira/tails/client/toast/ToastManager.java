package uk.kihira.tails.client.toast;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ToastManager
{
    public static final ToastManager INSTANCE = new ToastManager();

    private final ArrayList<Toast> toasts = new ArrayList<>();

    private ToastManager() 
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void createToast(int x, int y, String text)
    {
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        int stringWidth = fontRenderer.getStringWidth(text);
        toasts.add(new Toast(x, y, stringWidth + 10,  stringWidth * 3, text));
    }

    public void createCenteredToast(int x, int y, int maxWidth, String text)
    {
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        int stringWidth = fontRenderer.getStringWidth(text);
        if (stringWidth > maxWidth) {
            List<String> strings = fontRenderer.listFormattedStringToWidth(text, maxWidth);
            toasts.add(new Toast(x - (maxWidth / 2) - 5, y, maxWidth + 10, text.length() * 3, strings.toArray(new String[strings.size()])));
        }
        else {
            toasts.add(new Toast(x - (stringWidth / 2) - 5, y, stringWidth + 10, text.length() * 3, text));
        }
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event)
    {
        for (Toast toast : toasts) 
        {
            if (toast.mouseOver) 
            {
                toast.onMouseEvent(event);
            }
        }
    }

    @SubscribeEvent
    public void onClientTickPost(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            Iterator<Toast> toasts = this.toasts.iterator();
            while (toasts.hasNext())
            {
                Toast toast = toasts.next();
                if (toast.time-- <= 0)
                { 
                    toasts.remove();
                }
            }
        }
    }

    @SubscribeEvent
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event)
    {
        IProfiler profiler = Minecraft.getInstance().getProfiler();
        profiler.startSection("toastNotification");

        for (Toast toast : toasts)
        {
            toast.drawToast(event.getMatrixStack(), event.getMouseX(), event.getMouseY());
        }

        profiler.endSection();
    }
}
