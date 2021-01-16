package uk.kihira.tails.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uk.kihira.gltf.Model;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.Tails;

import java.util.Optional;
import java.util.UUID;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

/**
 * Legacy renderer that uses the player render event.
 * This is used for compatibility with certain mods
 */
public class FallbackRenderHandler
{
    private static RenderPlayerEvent.Pre currentEvent = null;
    private static Outfit currentOutfit = null;
    private static ResourceLocation currentPlayerTexture = null;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerRenderTick(RenderPlayerEvent.Pre e) 
    {
        UUID uuid = e.getPlayer().getGameProfile().getId();
        if (Tails.proxy.hasActiveOutfit(uuid) && !e.getPlayer().isInvisible()) 
        {
            currentOutfit = Tails.proxy.getActiveOutfit(uuid);
            currentPlayerTexture = ((ClientPlayerEntity) e.getPlayer()).getLocationSkin();
            currentEvent = e;
        }
    }

    @SubscribeEvent()
    public void onPlayerRenderTickPost(RenderPlayerEvent.Post e)
    {
        //Reset to null after rendering the current tail
        currentOutfit = null;
        currentPlayerTexture = null;
        currentEvent = null;
    }

    public static class ModelRendererWrapper extends ModelRenderer 
    {
        private final MountPoint mountPoint;

        public ModelRendererWrapper(net.minecraft.client.renderer.model.Model model, MountPoint mountPoint) 
        {
            super(model);
            this.mountPoint = mountPoint;
            addBox(0, 0, 0, 0, 0, 0); //Adds in a blank box as it's required in certain cases such as rendering arrows in entities
        }

        @Override
        public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn) 
        {
            if (currentEvent != null && currentOutfit != null && currentPlayerTexture != null)
            {
                for (OutfitPart part : currentOutfit.parts)
                {
                    if (part.mountPoint != mountPoint) return;

                    Optional<Model> model = PartRegistry.getModel(part.basePart);
                    model.ifPresent(value -> value.render(matrixStackIn));

                    Minecraft.getInstance().getTextureManager().bindTexture(currentPlayerTexture);
                }
            }
        }
    }
}
