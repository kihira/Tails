package kihira.tails.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.TailInfo;
import net.minecraft.entity.EntityLivingBase;

@SideOnly(Side.CLIENT)
public abstract class RenderTail {

    protected String name;

    public RenderTail(String name) {
        this.name = name;
    }

    public void render(EntityLivingBase entity, TailInfo info) {
        if (info.needsTextureCompile || info.getTexture() == null) {
            info.setTexture(TextureHelper.generateTexture(info));
            info.needsTextureCompile = false;
        }
        this.doRender(entity, info);
    }

    protected abstract void doRender(EntityLivingBase player, TailInfo info);

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
