package uk.kihira.tails.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.common.MinecraftForge;
import uk.kihira.tails.client.ClientEventHandler;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.PartRenderer;
import uk.kihira.tails.client.render.FallbackRenderHandler;
import uk.kihira.tails.client.render.LayerPart;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.*;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public PartRenderer partRenderer;

    @Override
    public void preInit() {
        registerMessages();
        registerHandlers();
        libraryManager = new LibraryManager.ClientLibraryManager();
    }

    @Override
    public void postInit() {
        setupRenderers();
    }

    @Override
    protected void registerMessages() {
        Tails.networkWrapper.registerMessage(PlayerDataMessage.Handler.class, PlayerDataMessage.class, 0, Side.CLIENT);
        Tails.networkWrapper.registerMessage(PlayerDataMapMessage.Handler.class, PlayerDataMapMessage.class, 1, Side.CLIENT);
        Tails.networkWrapper.registerMessage(LibraryEntriesMessage.Handler.class, LibraryEntriesMessage.class, 2, Side.CLIENT);
        Tails.networkWrapper.registerMessage(LibraryRequestMessage.Handler.class, LibraryRequestMessage.class, 3, Side.CLIENT);
        Tails.networkWrapper.registerMessage(ServerCapabilitiesMessage.Handler.class, ServerCapabilitiesMessage.class, 4, Side.CLIENT);
        super.registerMessages();
    }

    @Override
    protected void registerHandlers() {
        ClientEventHandler eventHandler = new ClientEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);

        //super.registerHandlers();
    }

    /**
     * Sets up the various part renders on the player model.
     * Renderers used depends upon legacy setting
     */
    private void setupRenderers() {
        boolean legacyRenderer = Tails.configuration.getBoolean(Configuration.CATEGORY_CLIENT, "ForceLegacyRendering", false, "Forces the legacy renderer which may have better compatibility with other mods");
        if (legacyRenderer) Tails.logger.info("Legacy Renderer has been forced enabled");
        else if (Loader.isModLoaded("SmartMoving")) {
            Tails.logger.info("Legacy Renderer enabled automatically for mod compatibility");
            legacyRenderer = true;
        }

        partRenderer = new PartRenderer();

        if (legacyRenderer) {
            MinecraftForge.EVENT_BUS.register(new FallbackRenderHandler());

            Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
            // Default
            ModelPlayer model = skinMap.get("default").getMainModel();
            model.bipedHead.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.HEAD));
            model.bipedBody.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.CHEST));
            model.bipedLeftArm.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.LEFT_ARM));
            model.bipedRightArm.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.RIGHT_ARM));
            model.bipedLeftLeg.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.LEFT_LEG));
            model.bipedRightLeg.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.RIGHT_LEG));
            // Slim
            model = skinMap.get("slim").getMainModel();
            model.bipedHead.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.HEAD));
            model.bipedBody.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.CHEST));
            model.bipedLeftArm.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.LEFT_ARM));
            model.bipedRightArm.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.RIGHT_ARM));
            model.bipedLeftLeg.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.LEFT_LEG));
            model.bipedRightLeg.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.RIGHT_LEG));
        } else {
            Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
            // Default
            RenderPlayer renderPlayer = skinMap.get("default");
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedHead, partRenderer, MountPoint.HEAD));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedBody, partRenderer, MountPoint.CHEST));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedLeftArm, partRenderer, MountPoint.LEFT_ARM));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedRightArm, partRenderer, MountPoint.RIGHT_ARM));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedLeftLeg, partRenderer, MountPoint.LEFT_LEG));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedRightLeg, partRenderer, MountPoint.RIGHT_LEG));

            // Slim
            renderPlayer = skinMap.get("slim");
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedHead, partRenderer, MountPoint.HEAD));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedBody, partRenderer, MountPoint.CHEST));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedLeftArm, partRenderer, MountPoint.LEFT_ARM));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedRightArm, partRenderer, MountPoint.RIGHT_ARM));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedLeftLeg, partRenderer, MountPoint.LEFT_LEG));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedRightLeg, partRenderer, MountPoint.RIGHT_LEG));
        }
    }
}
