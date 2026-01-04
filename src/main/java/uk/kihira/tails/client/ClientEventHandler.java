package uk.kihira.tails.client;

import com.google.common.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import uk.kihira.tails.client.gui.GuiEditor;
import uk.kihira.tails.client.render.LayerPart;
import uk.kihira.tails.client.render.LegacyLayerPart;
import uk.kihira.tails.common.Config;
import uk.kihira.tails.Tails;

import java.util.function.BiConsumer;

public class ClientEventHandler
{
    private static PartRenderer partRenderer;
    public static boolean captureColourUnderMouse = false;
    public static int mouseColourRGBA;

    @EventBusSubscriber(modid = Tails.MOD_ID, value = Dist.CLIENT)
    public static class Forge
    {
        private static boolean sentPartInfoToServer = false;
        private static boolean clearAllPartInfo = false;

        /*
         *** Tails Editor Button ***
         */
        @SubscribeEvent
        public static void onScreenInitPost(ScreenEvent.Init.Post event)
        {
            var screen = event.getScreen();
            if (screen instanceof PauseScreen)
            {
                var tailsButton = Button
                        .builder(Component.translatable("tails.gui.button.editor"), (button) -> screen.getMinecraft().setScreen(new GuiEditor()))
                        .bounds((screen.width / 2) - 50, screen.height - 25, 100, 20)
                        .build();
                event.addListener(tailsButton);
            }
        }

        /*
         *** Tails syncing ***
         */
        @SubscribeEvent
        public static void onConnectToServer(ClientPlayerNetworkEvent.LoggingIn event)
        {
            //Add local player texture to map
            if (Config.CONFIG.getLocalOutfit() != null) {
                Tails.proxy.setActiveOutfit(Minecraft.getInstance().getGameProfile().id(), Config.CONFIG.getLocalOutfit());
            }
        }

        @SubscribeEvent
        public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut e)
        {
            Tails.hasRemote = false;
            sentPartInfoToServer = false;
            clearAllPartInfo = true;
        }

        @SubscribeEvent
        public static void onExtractLevelRenderState(ExtractLevelRenderStateEvent event)
        {
            //event.getRenderState().setRenderData(LayerPart.OUTFIT_KEY, Tails.proxy.getActiveOutfit(event.getRenderState()));
        }

        @SubscribeEvent
        public static void onRegisterRenderStateModifiers(RegisterRenderStateModifiersEvent event)
        {
            // This should work but type checking is failing
/*            TypeToken<AvatarRenderer<AbstractClientPlayer>> rendererType = new TypeToken<>() {};
            BiConsumer<Avatar, AvatarRenderState> modifier = (player, renderState) -> {
                var uuid = player.getUUID();
                if (Tails.proxy.hasActiveOutfit(uuid))
                {
                    renderState.setRenderData(LayerPart.OUTFIT_KEY, Tails.proxy.getActiveOutfit(uuid));
                }
            };
            event.registerEntityModifier(rendererType, modifier);*/

            TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>> rendererType = new TypeToken<>() {};
            BiConsumer<LivingEntity, LivingEntityRenderState> modifier = (entity, renderState) -> {
                var uuid = entity.getUUID();
                if (entity instanceof AbstractClientPlayer && Tails.proxy.hasActiveOutfit(uuid))
                {
                    renderState.setRenderData(LayerPart.OUTFIT_KEY, Tails.proxy.getActiveOutfit(uuid));
                }
            };

            event.registerEntityModifier(rendererType, modifier);
        }

/*        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent e)
        {
            if (e.phase == TickEvent.Phase.START)
            {
                if (clearAllPartInfo)
                {
                    Tails.proxy.clearAllPartsData();
                    clearAllPartInfo = false;
                }
                //World can't be null if we want to send a packet it seems
                else if (!sentPartInfoToServer)
                {
                    //PacketDistributor.SERVER.noArg().send(new PlayerDataMessage(Minecraft.getInstance().getGameProfile().getId(), Config.localOutfit, false));
                    sentPartInfoToServer = true;
                }
            }
        }*/

        @SubscribeEvent
        public static void onRenderWorldLast(RenderLevelStageEvent.AfterLevel event)
        {
            if (partRenderer != null)
            {
                partRenderer.doRender(event.getPoseStack());
            }
        }

        @SubscribeEvent
        public static void onRenderTickEnd(RenderFrameEvent.Post event)
        {
            if (captureColourUnderMouse)
            {
                Screenshot.takeScreenshot(Minecraft.getInstance().getMainRenderTarget(), image ->
                {
                    // TODO get pixel returns ARGB
                    mouseColourRGBA = image.getPixel(Mth.floor(Minecraft.getInstance().mouseHandler.xpos()), Mth.floor(Minecraft.getInstance().mouseHandler.ypos()));
                    image.close();
                });
            }
        }
    }

    @EventBusSubscriber(modid = Tails.MOD_ID, value = Dist.CLIENT)
    public static class Mod
    {
        /**
         * Sets up the various part renders on the player model.
         * Renderers used depends upon legacy setting
         */
        @SubscribeEvent
        public static void addLayersEvent(EntityRenderersEvent.AddLayers event)
        {
            partRenderer = new PartRenderer();
            {
                for (var modelType : event.getSkins())
                {
                    var renderPlayer = event.getPlayerRenderer(modelType);
                    var model = renderPlayer.getModel();
                    renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.head, MountPoint.HEAD));
                    renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.body, MountPoint.CHEST));
                    renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.leftArm, MountPoint.LEFT_ARM));
                    renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.rightArm, MountPoint.RIGHT_ARM));
                    renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.leftLeg, MountPoint.LEFT_LEG));
                    renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.rightLeg, MountPoint.RIGHT_LEG));
                }
            }
        }
    }
}
