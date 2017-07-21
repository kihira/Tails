package uk.kihira.tails.api;

import uk.kihira.tails.client.OutfitPart;
import net.minecraft.entity.EntityLivingBase;

public interface IRenderHelper {
    void onPreRenderTail(EntityLivingBase entity, OutfitPart outfitPart);
}
