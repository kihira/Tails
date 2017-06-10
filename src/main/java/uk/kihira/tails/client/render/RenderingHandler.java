package uk.kihira.tails.client.render;

import uk.kihira.tails.common.Outfit;
import uk.kihira.tails.common.Tails;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

public class RenderingHandler {

    public static RenderPlayerEvent.Pre currentEvent = null;
    public static Outfit currentOutfit = null;
    public static ResourceLocation currentPlayerTexture = null;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerRenderTick(RenderPlayerEvent.Pre e) {
        UUID uuid = e.getEntityPlayer().getGameProfile().getId();
        if (Tails.proxy.hasActiveOutfit(uuid) && !e.getEntityPlayer().isInvisible()) {
            currentOutfit = Tails.proxy.getActiveOutfit(uuid);
            currentPlayerTexture = ((AbstractClientPlayer) e.getEntityPlayer()).getLocationSkin();
            currentEvent = e;
        }
    }

    @SubscribeEvent()
    public void onPlayerRenderTickPost(RenderPlayerEvent.Post e) {
        //Reset to null after rendering the current tail
        currentOutfit = null;
        currentPlayerTexture = null;
        currentEvent = null;
    }
}
