package uk.kihira.tails.api;

import uk.kihira.tails.client.outfit.OutfitPart;
import net.minecraft.entity.LivingEntity;

public interface IRenderHelper {
    void onPreRenderPart(LivingEntity entity, OutfitPart outfitPart);
}
