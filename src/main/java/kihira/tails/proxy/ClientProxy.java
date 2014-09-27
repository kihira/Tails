/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.client.ClientEventHandler;
import kihira.tails.common.TailInfo;
import kihira.tails.common.Tails;
import kihira.tails.common.network.TailInfoMessage;
import kihira.tails.common.network.TailMapMessage;
import net.minecraftforge.common.MinecraftForge;

import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void addTailInfo(UUID uuid, TailInfo tailInfo) {
        if (hasTailInfo(uuid)) {
            this.tailMap.get(uuid).setTexture(null);
        }
        //Mark to generate texture
        if (tailInfo != null && tailInfo.hastail) tailInfo.needsTextureCompile = true;

        super.addTailInfo(uuid, tailInfo);
    }

    @Override
    public void removeTailInfo(UUID uuid) {
        if (hasTailInfo(uuid)) {
            this.tailMap.get(uuid).setTexture(null);
        }
        super.removeTailInfo(uuid);
    }

    @Override
    public void clearAllTailInfo() {
        //We surround this in a try/catch incase it was called from a thread that was not the client thread
        try {
            for (TailInfo tailInfo : this.tailMap.values()) {
                tailInfo.setTexture(null);
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
