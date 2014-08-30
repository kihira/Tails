package kihira.tails.client.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiYesNo;

public class EventGuiTick {
    public Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (mc.currentScreen instanceof GuiMainMenu) {
            mc.displayGuiScreen(new GuiYesNo(null, "We just downloaded foxlib, because you need it for the mod!", "Do you want to restart the game now? (no will crash the game)", 0));
        }
    }
}
