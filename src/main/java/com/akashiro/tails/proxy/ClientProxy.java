package com.akashiro.tails.proxy;

import com.akashiro.tails.Tails;
import com.akashiro.tails.client.PartRegistry;
import com.akashiro.tails.client.render.LayerPart;
import com.akashiro.tails.client.render.PlayerRenderHelper;
import com.akashiro.tails.client.render.RenderPart;
import com.akashiro.tails.common.data.PartsData;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(
                new com.akashiro.tails.client.ClientEventHandler());
        RenderPart.registerRenderHelper(Player.class, new PlayerRenderHelper());
    }

    @Override
    public void registerRenderers() {
        PartRegistry.register();
    }

    public void onAddLayers(EntityRenderersEvent.AddLayers event) {
        addLayers(event.getSkin("default"));
        addLayers(event.getSkin("slim"));
    }

    private void addLayers(PlayerRenderer playerRenderer) {
        if (playerRenderer == null) return;

        var model = playerRenderer.getModel();
        playerRenderer.addLayer(new LayerPart(
                playerRenderer, PartsData.PartType.TAIL, model.body));
        playerRenderer.addLayer(new LayerPart(
                playerRenderer, PartsData.PartType.WINGS, model.body));
        playerRenderer.addLayer(new LayerPart(
                playerRenderer, PartsData.PartType.EARS, model.head));
        playerRenderer.addLayer(new LayerPart(
                playerRenderer, PartsData.PartType.MUZZLE, model.head));
    }

    @SubscribeEvent
    public void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (!Tails.hasRemote
                && Tails.localPartsData != null
                && event.getPlayer() != null) {
            addPartsData(event.getPlayer().getUUID(), Tails.localPartsData);
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        clearAllPartsData();
        Tails.hasRemote = false;
        Tails.loadConfig();
    }
}
