package uk.kihira.tails.api;

import net.minecraft.world.entity.LivingEntity;
import uk.kihira.tails.client.outfit.OutfitPart;

public interface IRenderHelper {
    void onPreRenderPart(LivingEntity entity, OutfitPart outfitPart);
}
