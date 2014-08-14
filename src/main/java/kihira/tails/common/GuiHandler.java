package kihira.tails.common;

import cpw.mods.fml.common.network.IGuiHandler;
import kihira.tails.client.gui.GuiEditTail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0) return new GuiEditTail();
        return null;
    }
}
