package kihira.tails.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;

@SideOnly(Side.CLIENT)
public abstract class RenderTail {

    public abstract void render(EntityPlayer player);
}
