package uk.kihira.tails.client;

import com.google.common.reflect.TypeToken;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import uk.kihira.tails.client.gui.OutfitEditScreen;
import uk.kihira.tails.client.model.DragonTailModel;
import uk.kihira.tails.client.model.FoxTailModel;
import uk.kihira.tails.client.model.RacoonTailModel;
import uk.kihira.tails.client.model.SharkTailModel;
import uk.kihira.tails.client.model.head.FoxEarsModel;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.outfit.Tint;
import uk.kihira.tails.client.render.LayerPart;
import uk.kihira.tails.client.render.LegacyLayerPart;
import uk.kihira.tails.common.Config;
import uk.kihira.tails.Tails;

import java.util.UUID;
import java.util.function.BiConsumer;

import static uk.kihira.tails.client.PartRegistry.registerPartWithModel;

public class ClientEventHandler
{
    private static PartRenderer partRenderer;

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
                        .builder(Component.translatable("tails.gui.button.editor"), (button) -> screen.getMinecraft().setScreen(new OutfitEditScreen()))
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
            if (Config.CONFIG.getLocalOutfit() != null)
            {
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
                // Exclude entities that already have an outfit as this is likely coming from the part list window
                if (entity instanceof AbstractClientPlayer && Tails.proxy.hasActiveOutfit(uuid) && renderState.getRenderData(LayerPart.OUTFIT_KEY) == null)
                {
                    renderState.setRenderData(LayerPart.OUTFIT_KEY, Tails.proxy.getActiveOutfit(uuid));
                }
            };

            event.registerEntityModifier(rendererType, modifier);
        }

        @SubscribeEvent
        public static void onRenderWorldLast(RenderLevelStageEvent.AfterLevel event)
        {
            if (partRenderer != null)
            {
                partRenderer.doRender(event.getPoseStack());
            }
        }
    }

    @EventBusSubscriber(modid = Tails.MOD_ID, value = Dist.CLIENT)
    public static class Mod
    {
        @SubscribeEvent
        public static void registerRenderPipelines(final RegisterRenderPipelinesEvent event)
        {
            //event.registerPipeline(PartRenderer.PART_PIPELINE);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
        {
            // TODO do we want to register a layer for each model? Maybe instead we do it per mount point with an empty model then have it include the part model?
            event.registerLayerDefinition(FOX_TAIL_LAYER_LOCATION, FoxTailModel::createBodyLayer);
            event.registerLayerDefinition(SHARK_TAIL_LAYER_LOCATION, SharkTailModel::createModelLayer);
            event.registerLayerDefinition(DRAGON_TAIL_LAYER_LOCATION, DragonTailModel::createModelLayer);
            event.registerLayerDefinition(RACOON_TAIL_LAYER_LOCATION, RacoonTailModel::createModelLayer);
            event.registerLayerDefinition(FOX_EARS_LAYER_LOCATION, FoxEarsModel::createModelLayer);
        }

        /**
         * Sets up the various part renders on the player model.
         * Renderers used depends upon legacy setting
         */
        @SubscribeEvent
        public static void addLayersEvent(EntityRenderersEvent.AddLayers event)
        {
            registerPartWithModel(
                    new Part(
                            UUID.fromString("b783d4b9-dd0e-41bb-8aa3-87efac967c19"),
                            "Fluffy Tail",
                            "Kihira",
                            MountPoint.CHEST,
                            new float[] {0, 1, 0},
                            new float[] {0, 0, 0},
                            new float[] {1, 1, 1},
                            new Tint[]{new Tint(1, 0, 0), new Tint(0, 1, 0), new Tint(0, 0, 1)},
                            new PartTexture[]{ new PartTexture(UUID.fromString("b783d4b9-dd0e-41bb-8aa3-87efac967c19"), "Default", "Kihira") }),
                    new FoxTailModel(event.getEntityModels().bakeLayer(FOX_TAIL_LAYER_LOCATION)));
            registerPartWithModel(
                    new Part(
                            UUID.fromString("c371a5c1-7f08-4b3a-974b-abf8bc639cd1"),
                            "Shark Tail",
                            "Kihira",
                            MountPoint.CHEST,
                            new float[] {0, 1, 0},
                            new float[] {0, 0, 0},
                            new float[] {1, 1, 1},
                            new Tint[]{new Tint(1, 0, 0), new Tint(0, 1, 0), new Tint(0, 0, 1)},
                            new PartTexture[]{ new PartTexture(UUID.fromString("c371a5c1-7f08-4b3a-974b-abf8bc639cd1"), "Default", "Kihira") }),
                    new SharkTailModel(event.getEntityModels().bakeLayer(SHARK_TAIL_LAYER_LOCATION)));
            registerPartWithModel(
                    new Part(
                            UUID.fromString("42db3167-aa40-4d9d-bf22-68944ef65cda"),
                            "Shark Tail",
                            "Kihira",
                            MountPoint.CHEST,
                            new float[] {0, 1, 0},
                            new float[] {0, 0, 0},
                            new float[] {1, 1, 1},
                            new Tint[]{new Tint(1, 0, 0), new Tint(0, 1, 0), new Tint(0, 0, 1)},
                            // new PartTexture[]{ new PartTexture(UUID.fromString("42db3167-aa40-4d9d-bf22-68944ef65cda"), "Striped", "Kihira") }),
                            new PartTexture[]{ new PartTexture(UUID.fromString("42db3167-aa40-4d9d-bf22-68944ef65cda"), "Default", "Kihira") }),
                    new DragonTailModel(event.getEntityModels().bakeLayer(DRAGON_TAIL_LAYER_LOCATION)));
            registerPartWithModel(
                    new Part(
                            UUID.fromString("b119ff1e-d8ed-4454-a77f-141916e77ebe"),
                            "Racoon Tail",
                            "Kihira",
                            MountPoint.CHEST,
                            new float[] {0, 1, 0},
                            new float[] {0, 0, 0},
                            new float[] {1, 1, 1},
                            new Tint[]{new Tint(1, 0, 0), new Tint(0, 1, 0), new Tint(0, 0, 1)},
                            new PartTexture[]{ new PartTexture(UUID.fromString("b119ff1e-d8ed-4454-a77f-141916e77ebe"), "Default", "Kihira") }),
                    new RacoonTailModel(event.getEntityModels().bakeLayer(RACOON_TAIL_LAYER_LOCATION)));
            registerPartWithModel(
                    new Part(
                            UUID.fromString("7ee6ceb1-bcbf-44f3-98a5-969094f7f1d6"),
                            "Fox Ears",
                            "Kihira",
                            MountPoint.HEAD,
                            new float[] {0, 1, 0},
                            new float[] {0, 0, 0},
                            new float[] {1, 1, 1},
                            new Tint[]{new Tint(1, 0, 0), new Tint(0, 1, 0), new Tint(0, 0, 1)},
                            new PartTexture[]{ new PartTexture(UUID.fromString("7ee6ceb1-bcbf-44f3-98a5-969094f7f1d6"), "Default", "Kihira") }),
                    new FoxEarsModel(event.getEntityModels().bakeLayer(FOX_EARS_LAYER_LOCATION)));

            partRenderer = new PartRenderer();
            {
                for (var modelType : event.getSkins())
                {
                    var renderPlayer = event.getPlayerRenderer(modelType);
                    var model = renderPlayer.getModel();
                    renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.head, event.getContext().getModelSet(), MountPoint.HEAD));
                    renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.body, event.getContext().getModelSet(), MountPoint.CHEST));
                    //renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.leftArm, event.getContext().getModelSet(), MountPoint.LEFT_ARM));
                    //renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.rightArm, event.getContext().getModelSet(), MountPoint.RIGHT_ARM));
                    //renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.leftLeg, event.getContext().getModelSet(), MountPoint.LEFT_LEG));
                    //renderPlayer.addLayer(new LegacyLayerPart(renderPlayer, model.rightLeg, event.getContext().getModelSet(), MountPoint.RIGHT_LEG));
                }
            }

            // todo temp
            var outfit = new Outfit();
            //outfit.parts.add(new OutfitPart(PartRegistry.getPart(UUID.fromString("b783d4b9-dd0e-41bb-8aa3-87efac967c19")).get())); // fox
            //outfit.parts.add(new OutfitPart(PartRegistry.getPart(UUID.fromString("c371a5c1-7f08-4b3a-974b-abf8bc639cd1")).get())); // shark
            //outfit.parts.add(new OutfitPart(PartRegistry.getPart(UUID.fromString("42db3167-aa40-4d9d-bf22-68944ef65cda")).get())); // dragon
            outfit.parts.add(new OutfitPart(PartRegistry.getPart(UUID.fromString("b119ff1e-d8ed-4454-a77f-141916e77ebe")).get())); // racoon
            outfit.parts.add(new OutfitPart(PartRegistry.getPart(UUID.fromString("7ee6ceb1-bcbf-44f3-98a5-969094f7f1d6")).get())); // fox ears
            Config.CONFIG.setLocalOutfit(outfit);
        }
    }

    private static final ModelLayerLocation FOX_TAIL_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Tails.MOD_ID, "fox_tail"), "chest");
    private static final ModelLayerLocation SHARK_TAIL_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Tails.MOD_ID, "shark_tail"), "chest");
    private static final ModelLayerLocation DRAGON_TAIL_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Tails.MOD_ID, "dragon_tail"), "chest");
    private static final ModelLayerLocation RACOON_TAIL_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Tails.MOD_ID, "racoon_tail"), "chest");
    private static final ModelLayerLocation FOX_EARS_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Tails.MOD_ID, "fox_ears"), "head");
}
