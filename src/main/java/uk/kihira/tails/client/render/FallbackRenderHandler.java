package uk.kihira.tails.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.kihira.gltf.Model;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.Tails;

import java.util.UUID;

/**
 * Legacy renderer that uses the player render event.
 * This is used for compatibility with certain mods
 */
@SideOnly(Side.CLIENT)
public class FallbackRenderHandler {

    private static RenderPlayerEvent.Pre currentEvent = null;
    private static Outfit currentOutfit = null;
    private static ResourceLocation currentPlayerTexture = null;

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

    @SideOnly(Side.CLIENT)
    public static class ModelRendererWrapper extends ModelRenderer {

        private final MountPoint mountPoint;

        public ModelRendererWrapper(ModelBase model, MountPoint mountPoint) {
            super(model);
            this.mountPoint = mountPoint;
            addBox(0, 0, 0, 0, 0, 0); //Adds in a blank box as it's required in certain cases such as rendering arrows in entities
        }

        @Override
        public void render(float scale) {
            if (currentEvent != null && currentOutfit != null && currentPlayerTexture != null) {
                for (OutfitPart part : currentOutfit.parts) {
                    if (part.mountPoint != mountPoint) return;

                    Model model = null;
                    // Model model = PartRegistry.getModel(part.basePart);
                    if (model != null) {
                        model.render();
                    }

                    Minecraft.getMinecraft().renderEngine.bindTexture(currentPlayerTexture);
                }
            }
        }
    }
}
