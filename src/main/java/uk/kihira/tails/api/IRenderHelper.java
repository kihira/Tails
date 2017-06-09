package uk.kihira.tails.api;

import uk.kihira.tails.client.OutfitPart;
import uk.kihira.tails.client.render.LegacyPartRenderer;
import uk.kihira.tails.common.PartInfo;
import net.minecraft.entity.EntityLivingBase;

public interface IRenderHelper {
    void onPreRenderTail(EntityLivingBase entity, OutfitPart outfitPart);
}
