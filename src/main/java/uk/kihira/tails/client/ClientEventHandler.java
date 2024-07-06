package uk.kihira.tails.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import uk.kihira.tails.client.gui.GuiEditor;
import uk.kihira.tails.client.render.LayerPart;
import uk.kihira.tails.common.Config;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;

public class ClientEventHandler
{
    private static PartRenderer partRenderer;

    @EventBusSubscriber(modid = Tails.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
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
            if (Config.localOutfit != null) {
                Tails.proxy.setActiveOutfit(Minecraft.getInstance().getGameProfile().getId(), Config.localOutfit);
            }
        }

        @SubscribeEvent
        public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut e)
        {
            Tails.hasRemote = false;
            sentPartInfoToServer = false;
            clearAllPartInfo = true;
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
        public static void onRenderWorldLast(RenderLevelStageEvent event)
        {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL && partRenderer != null)
            {
                partRenderer.doRender(event.getPoseStack());
            }
        }
    }

    @EventBusSubscriber(modid = Tails.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
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
                // Default
                var renderPlayer = (PlayerRenderer) event.getSkin(PlayerSkin.Model.WIDE);
                var model = (PlayerModel<AbstractClientPlayer>)renderPlayer.getModel();
                renderPlayer.addLayer(new LayerPart(renderPlayer, model.head, partRenderer, MountPoint.HEAD));
                renderPlayer.addLayer(new LayerPart(renderPlayer, model.body, partRenderer, MountPoint.CHEST));
                renderPlayer.addLayer(new LayerPart(renderPlayer, model.leftArm, partRenderer, MountPoint.LEFT_ARM));
                renderPlayer.addLayer(new LayerPart(renderPlayer, model.rightArm, partRenderer, MountPoint.RIGHT_ARM));
                renderPlayer.addLayer(new LayerPart(renderPlayer, model.leftLeg, partRenderer, MountPoint.LEFT_LEG));
                renderPlayer.addLayer(new LayerPart(renderPlayer, model.rightLeg, partRenderer, MountPoint.RIGHT_LEG));

                // Slim
                renderPlayer = event.getSkin(PlayerSkin.Model.SLIM);
                model = (PlayerModel<AbstractClientPlayer>)renderPlayer.getModel();
                renderPlayer.addLayer(new LayerPart(renderPlayer, model.head, partRenderer, MountPoint.HEAD));
                renderPlayer.addLayer(new LayerPart(renderPlayer, model.body, partRenderer, MountPoint.CHEST));
                renderPlayer.addLayer(new LayerPart(renderPlayer, model.leftArm, partRenderer, MountPoint.LEFT_ARM));
                renderPlayer.addLayer(new LayerPart(renderPlayer, model.rightArm, partRenderer, MountPoint.RIGHT_ARM));
                renderPlayer.addLayer(new LayerPart(renderPlayer, model.leftLeg, partRenderer, MountPoint.LEFT_LEG));
                renderPlayer.addLayer(new LayerPart(renderPlayer, model.rightLeg, partRenderer, MountPoint.RIGHT_LEG));
            }
        }
    }
}
