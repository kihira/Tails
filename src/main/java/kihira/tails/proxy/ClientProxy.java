package kihira.tails.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import kihira.tails.client.ClientEventHandler;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.TailInfo;
import kihira.tails.common.Tails;
import kihira.tails.common.network.TailInfoMessage;
import kihira.tails.common.network.TailMapMessage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import java.util.UUID;

public class ClientProxy extends CommonProxy {

    @Override
    public void addTailInfo(UUID uuid, TailInfo tailInfo) {
        //Have I ever said how much I hate this proxy BS? Fucking ClientProxy is registered on the server thread if integrated
        //This doesn't work properly with netty threads, try with getSide instead?
        if (FMLCommonHandler.instance().getSide().isClient()) {
            //Release texture if a new TailInfo is being created
            if (this.tailMap.containsKey(uuid)) {
                Minecraft.getMinecraft().renderEngine.deleteTexture(tailInfo.texture);
            }
            //Generate texture
            if (tailInfo != null && tailInfo.hastail) tailInfo.texture = TextureHelper.generateTexture(tailInfo);
        }

        super.addTailInfo(uuid, tailInfo);
    }

    @Override
    public void removeTailInfo(UUID uuid) {
        if (this.tailMap.containsKey(uuid)) {
            Minecraft.getMinecraft().renderEngine.deleteTexture(this.tailMap.get(uuid).texture);
        }
        super.removeTailInfo(uuid);
    }

    @Override
    public void clearAllTailInfo() {
        //We surround this in a try/catch incase it was called from a thread that was not the client thread
        try {
            for (TailInfo tailInfo : this.tailMap.values()) {
                Minecraft.getMinecraft().renderEngine.deleteTexture(tailInfo.texture);
            }
        } catch (Exception ignored) {}
        super.clearAllTailInfo();
    }

    @Override
    public void registerMessages() {
        Tails.networkWrapper.registerMessage(TailInfoMessage.TailInfoMessageHandler.class, TailInfoMessage.class, 0, Side.CLIENT);
        Tails.networkWrapper.registerMessage(TailMapMessage.TailMapMessageHandler.class, TailMapMessage.class, 1, Side.CLIENT);
        super.registerMessages();
    }

    @Override
    public void registerHandlers() {
        ClientEventHandler eventHandler = new ClientEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLCommonHandler.instance().bus().register(eventHandler);

        super.registerHandlers();
    }
}
