package uk.kihira.tails.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraftforge.common.MinecraftForge;
import uk.kihira.tails.client.ClientEventHandler;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.PartRenderer;
import uk.kihira.tails.client.render.FallbackRenderHandler;
import uk.kihira.tails.client.render.LayerPart;
import uk.kihira.tails.common.Config;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.*;

import java.util.Map;

public class ClientProxy extends CommonProxy 
{
    public PartRenderer partRenderer;

    @Override
    public void preInit() 
    {
        registerMessages();
        registerHandlers();
        this.libraryManager = new LibraryManager.ClientLibraryManager();
    }

    @Override
    public void postInit() 
    {
        setupRenderers();
    }

    @Override
    protected void registerHandlers()
    {
        ClientEventHandler eventHandler = new ClientEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);

        //super.registerHandlers();
    }

    /**
     * Sets up the various part renders on the player model.
     * Renderers used depends upon legacy setting
     */
    private void setupRenderers()
    {
        boolean legacyRenderer = Config.forceLegacyRendering.get();
        if (legacyRenderer)
        {
            Tails.LOGGER.info("Legacy Renderer has been forced enabled");
        }
        // todo Fix smart moving compat
//        else if (Loader.isModLoaded("SmartMoving"))
//        {
//            Tails.LOGGER.info("Legacy Renderer enabled automatically for mod compatibility");
//            legacyRenderer = true;
//        }
        partRenderer = new PartRenderer();

        Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();

        if (legacyRenderer) 
        {
            MinecraftForge.EVENT_BUS.register(new FallbackRenderHandler());

            // Default
            PlayerModel<AbstractClientPlayerEntity> model = skinMap.get("default").getEntityModel();
            model.bipedHead.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.HEAD));
            model.bipedBody.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.CHEST));
            model.bipedLeftArm.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.LEFT_ARM));
            model.bipedRightArm.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.RIGHT_ARM));
            model.bipedLeftLeg.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.LEFT_LEG));
            model.bipedRightLeg.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.RIGHT_LEG));
            // Slim
            model = skinMap.get("slim").getEntityModel();
            model.bipedHead.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.HEAD));
            model.bipedBody.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.CHEST));
            model.bipedLeftArm.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.LEFT_ARM));
            model.bipedRightArm.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.RIGHT_ARM));
            model.bipedLeftLeg.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.LEFT_LEG));
            model.bipedRightLeg.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.RIGHT_LEG));
        } else 
        {
            // Default
            PlayerRenderer renderPlayer = skinMap.get("default");
            renderPlayer.addLayer(new LayerPart(renderPlayer, renderPlayer.getEntityModel().bipedHead, partRenderer, MountPoint.HEAD));
            renderPlayer.addLayer(new LayerPart(renderPlayer,renderPlayer.getEntityModel().bipedBody, partRenderer, MountPoint.CHEST));
            renderPlayer.addLayer(new LayerPart(renderPlayer,renderPlayer.getEntityModel().bipedLeftArm, partRenderer, MountPoint.LEFT_ARM));
            renderPlayer.addLayer(new LayerPart(renderPlayer,renderPlayer.getEntityModel().bipedRightArm, partRenderer, MountPoint.RIGHT_ARM));
            renderPlayer.addLayer(new LayerPart(renderPlayer,renderPlayer.getEntityModel().bipedLeftLeg, partRenderer, MountPoint.LEFT_LEG));
            renderPlayer.addLayer(new LayerPart(renderPlayer,renderPlayer.getEntityModel().bipedRightLeg, partRenderer, MountPoint.RIGHT_LEG));

            // Slim
            renderPlayer = skinMap.get("slim");
            renderPlayer.addLayer(new LayerPart(renderPlayer,renderPlayer.getEntityModel().bipedHead, partRenderer, MountPoint.HEAD));
            renderPlayer.addLayer(new LayerPart(renderPlayer,renderPlayer.getEntityModel().bipedBody, partRenderer, MountPoint.CHEST));
            renderPlayer.addLayer(new LayerPart(renderPlayer,renderPlayer.getEntityModel().bipedLeftArm, partRenderer, MountPoint.LEFT_ARM));
            renderPlayer.addLayer(new LayerPart(renderPlayer,renderPlayer.getEntityModel().bipedRightArm, partRenderer, MountPoint.RIGHT_ARM));
            renderPlayer.addLayer(new LayerPart(renderPlayer,renderPlayer.getEntityModel().bipedLeftLeg, partRenderer, MountPoint.LEFT_LEG));
            renderPlayer.addLayer(new LayerPart(renderPlayer,renderPlayer.getEntityModel().bipedRightLeg, partRenderer, MountPoint.RIGHT_LEG));
        }
    }
}
