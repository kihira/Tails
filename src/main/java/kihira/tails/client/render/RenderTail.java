package kihira.tails.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.common.TailInfo;
import net.minecraft.entity.EntityLivingBase;

@SideOnly(Side.CLIENT)
public abstract class RenderTail {

    protected String name;

    public RenderTail(String name) {
        this.name = name;
    }

    public abstract void render(EntityLivingBase player, TailInfo info);

    /**
     * Gets the available textures for this tail
     * @return
     */
    public abstract String[] getTextureNames();

    /**
     * Gets the available subtypes for this tail
     * @return
     */
    public abstract int getAvailableSubTypes();

    public String getUnlocalisedName(int subType) {
        return "tail."+this.name+"."+subType+".name";
    }
}
