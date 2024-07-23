package uk.kihira.tails.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.TickEvent;
import uk.kihira.tails.client.gui.GuiEditor;
import uk.kihira.tails.client.render.LegacyLayerPart;
import uk.kihira.tails.common.Config;
import uk.kihira.tails.common.Tails;

public class ClientEventHandler
{
    private static PartRenderer partRenderer;
    public static boolean captureColourUnderMouse = false;
    public static int mouseColourRGBA;

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

        @SubscribeEvent
        public static void onRenderTickEnd(TickEvent.RenderTickEvent event)
        {
            if (event.phase == TickEvent.RenderTickEvent.Phase.END && captureColourUnderMouse)
            {
                var image = Screenshot.takeScreenshot(Minecraft.getInstance().getMainRenderTarget());
                mouseColourRGBA = image.getPixelRGBA(Mth.floor(Minecraft.getInstance().mouseHandler.xpos()), Mth.floor(Minecraft.getInstance().mouseHandler.ypos()));
                image.close();
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
                var model = renderPlayer.getModel();
                renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.head, MountPoint.HEAD));
                renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.body, MountPoint.CHEST));
                renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.leftArm, MountPoint.LEFT_ARM));
                renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.rightArm, MountPoint.RIGHT_ARM));
                renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.leftLeg, MountPoint.LEFT_LEG));
                renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.rightLeg, MountPoint.RIGHT_LEG));

                // Slim
                renderPlayer = event.getSkin(PlayerSkin.Model.SLIM);
                model = renderPlayer.getModel();
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
