/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client;

import kihira.tails.client.gui.GuiEditor;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.Tails;
import kihira.tails.common.network.PlayerDataMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("UnusedParameters")
@SideOnly(Side.CLIENT)
public class ClientEventHandler {
    private boolean sentPartInfoToServer = false;
    private boolean clearAllPartInfo = false;

    /*
        *** Tails Editor Button ***
     */
    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void onScreenInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiIngameMenu) {
            event.getButtonList().add(new GuiButton(1234, (event.getGui().width / 2) - 35, event.getGui().height - 25, 70, 20, I18n.format("gui.button.editor")));
        }
    }

    @SubscribeEvent
    public void onButtonClickPre(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (event.getGui() instanceof GuiIngameMenu) {
            if (event.getButton().id == 1234) {
                event.getGui().mc.displayGuiScreen(new GuiEditor());
                event.setCanceled(true);
            }
        }
    }

    /*
        *** Tails syncing ***
     */
    @SubscribeEvent
    public void onConnectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        //Add local player texture to map
        if (Tails.localPartsData != null) {
            Tails.proxy.addPartsData(Minecraft.getMinecraft().getSession().getProfile().getId(), Tails.localPartsData);
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        Tails.hasRemote = false;
        sentPartInfoToServer = false;
        clearAllPartInfo = true;

        Tails.instance.loadConfig();
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.START) {
            if (TextureHelper.needsBuild(e.player) && e.player instanceof AbstractClientPlayer) {
                TextureHelper.buildPlayerPartsData((AbstractClientPlayer) e.player);
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.START) {
            if (clearAllPartInfo) {
                Tails.proxy.clearAllPartsData();
                clearAllPartInfo = false;
            }
            //World can't be null if we want to send a packet it seems
            else if (!sentPartInfoToServer && Minecraft.getMinecraft().theWorld != null) {
                Tails.networkWrapper.sendToServer(new PlayerDataMessage(Minecraft.getMinecraft().thePlayer.getGameProfile().getId(), Tails.localPartsData, false));
                sentPartInfoToServer = true;
            }
        }
    }
}
