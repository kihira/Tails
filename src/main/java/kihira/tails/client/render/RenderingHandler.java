package kihira.tails.client.render;

import kihira.tails.common.PartsData;
import kihira.tails.common.Tails;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

public class RenderingHandler {

    public static RenderPlayerEvent.Pre currentEvent = null;
    public static PartsData currentPartsData = null;
    public static ResourceLocation currentPlayerTexture = null;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerRenderTick(RenderPlayerEvent.Pre e) {
        UUID uuid = e.getEntityPlayer().getGameProfile().getId();
        if (Tails.proxy.hasPartsData(uuid) && !e.getEntityPlayer().isInvisible()) {
            currentPartsData = Tails.proxy.getPartsData(uuid);
            currentPlayerTexture = ((AbstractClientPlayer) e.getEntityPlayer()).getLocationSkin();
            currentEvent = e;
        }
    }

    @SubscribeEvent()
    public void onPlayerRenderTickPost(RenderPlayerEvent.Post e) {
        //Reset to null after rendering the current tail
        currentPartsData = null;
        currentPlayerTexture = null;
        currentEvent = null;
    }
}
