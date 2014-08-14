package kihira.tails.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.common.TailInfo;
import net.minecraft.entity.EntityLivingBase;

@SideOnly(Side.CLIENT)
public abstract class RenderTail {

    public abstract void render(EntityLivingBase player, TailInfo info);
    
    public abstract String[] getTextureNames();
}
