package uk.kihira.tails.proxy;

import uk.kihira.tails.client.ClientEventHandler;
import uk.kihira.tails.client.model.ModelRendererWrapper;
import uk.kihira.tails.client.render.LayerPart;
import uk.kihira.tails.client.render.RenderingHandler;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.Outfit;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        registerMessages();
        registerHandlers();
        libraryManager = new LibraryManager.ClientLibraryManager();
    }

    @Override
    public void setActiveOutfit(UUID uuid, Outfit outfit) {
        if (hasActiveOutfit(uuid)) {
            this.activeOutfits.get(uuid).clearTextures();
        }

        super.setActiveOutfit(uuid, outfit);
    }

    @Override
    public void removeActiveOutfit(UUID uuid) {
        if (hasActiveOutfit(uuid)) {
            this.activeOutfits.get(uuid).clearTextures();
        }
        super.removeActiveOutfit(uuid);
    }

    @Override
    public void clearAllPartsData() {
        for (Outfit outfit : this.activeOutfits.values()) {
            outfit.clearTextures();
        }
        super.clearAllPartsData();
    }

    @Override
    public void registerMessages() {
        Tails.networkWrapper.registerMessage(PlayerDataMessage.Handler.class, PlayerDataMessage.class, 0, Side.CLIENT);
        Tails.networkWrapper.registerMessage(PlayerDataMapMessage.Handler.class, PlayerDataMapMessage.class, 1, Side.CLIENT);
        Tails.networkWrapper.registerMessage(LibraryEntriesMessage.Handler.class, LibraryEntriesMessage.class, 2, Side.CLIENT);
        Tails.networkWrapper.registerMessage(LibraryRequestMessage.Handler.class, LibraryRequestMessage.class, 3, Side.CLIENT);
        Tails.networkWrapper.registerMessage(ServerCapabilitiesMessage.Handler.class, ServerCapabilitiesMessage.class, 4, Side.CLIENT);
        super.registerMessages();
    }

    @Override
    public void registerHandlers() {
        ClientEventHandler eventHandler = new ClientEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);

        //super.registerHandlers();
    }

    @Override
    public void registerRenderers(boolean legacyRenderer) {
        if (legacyRenderer) {
            MinecraftForge.EVENT_BUS.register(new RenderingHandler());

            Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
            // Default
            ModelPlayer model = skinMap.get("default").getMainModel();
            model.bipedBody.addChild(new ModelRendererWrapper(model, PartsData.PartType.TAIL));
            model.bipedBody.addChild(new ModelRendererWrapper(model, PartsData.PartType.WINGS));
            model.bipedHead.addChild(new ModelRendererWrapper(model, PartsData.PartType.EARS));
            model.bipedHead.addChild(new ModelRendererWrapper(model, PartsData.PartType.MUZZLE));
            // Slim
            model = skinMap.get("slim").getMainModel();
            model.bipedBody.addChild(new ModelRendererWrapper(model, PartsData.PartType.TAIL));
            model.bipedBody.addChild(new ModelRendererWrapper(model, PartsData.PartType.WINGS));
            model.bipedHead.addChild(new ModelRendererWrapper(model, PartsData.PartType.EARS));
            model.bipedHead.addChild(new ModelRendererWrapper(model, PartsData.PartType.MUZZLE));
        }
        else {
            Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
            // Default
            RenderPlayer renderPlayer = skinMap.get("default");
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedBody, PartsData.PartType.TAIL));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedBody, PartsData.PartType.WINGS));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedHead, PartsData.PartType.EARS));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedHead, PartsData.PartType.MUZZLE));
            // Slim
            renderPlayer = skinMap.get("slim");
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedBody, PartsData.PartType.TAIL));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedBody, PartsData.PartType.WINGS));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedHead, PartsData.PartType.EARS));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedHead, PartsData.PartType.MUZZLE));
        }
    }
}
