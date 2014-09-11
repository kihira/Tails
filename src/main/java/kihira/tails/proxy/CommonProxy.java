package kihira.tails.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import kihira.tails.common.ServerEventHandler;
import kihira.tails.common.TailInfo;
import kihira.tails.common.Tails;
import kihira.tails.common.network.TailInfoMessage;
import kihira.tails.common.network.TailMapMessage;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

public class CommonProxy {

    protected Hashtable<UUID, TailInfo> tailMap = new Hashtable<UUID, TailInfo>();

    public void registerMessages() {
        Tails.networkWrapper.registerMessage(TailInfoMessage.TailInfoMessageHandler.class, TailInfoMessage.class, 0, Side.SERVER);
        Tails.networkWrapper.registerMessage(TailMapMessage.TailMapMessageHandler.class, TailMapMessage.class, 1, Side.SERVER);
    }

    public void registerHandlers() {
        FMLCommonHandler.instance().bus().register(new ServerEventHandler());
    }

    public void addTailInfo(UUID uuid, TailInfo tailInfo) {
        //Tails.logger.info("Registered TailInfo " + tailInfo + " " + FMLCommonHandler.instance().getEffectiveSide());
        this.tailMap.put(uuid, tailInfo);
    }

    public void removeTailInfo(UUID uuid) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer() && uuid != null && this.tailMap.containsKey(uuid)) {
            //Tell client to remove textures
            Tails.networkWrapper.sendToAll(new TailInfoMessage(this.tailMap.get(uuid), true));
        }
        this.tailMap.remove(uuid);
    }

    public void clearAllTailInfo() {
        this.tailMap.clear();
    }

    public boolean hasTailInfo(UUID uuid) {
        return uuid != null && this.tailMap.containsKey(uuid);
    }

    public TailInfo getTailInfo(UUID uuid) {
        return this.tailMap.get(uuid);
    }

    public Map<UUID, TailInfo> getTailMap() {
        return this.tailMap;
    }
}