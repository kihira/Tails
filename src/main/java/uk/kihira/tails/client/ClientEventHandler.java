package uk.kihira.tails.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import uk.kihira.tails.client.gui.GuiEditor;
import uk.kihira.tails.common.OutfitManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    private static final int TAILS_BUTTON_ID = 1234;

    private boolean sentPartInfoToServer = false;
    private boolean clearAllPartInfo = false;

    /*
     *** Tails Editor Button ***
     */
    @SubscribeEvent
    public void onScreenInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiIngameMenu) {
            event.getButtonList().add(new GuiButton(TAILS_BUTTON_ID, (event.getGui().width / 2) - 35, event.getGui().height - 25, 70, 20, I18n.format("gui.button.editor")) {
                @Override
                public void onClick(double mouseX, double mouseY) {
                    event.getGui().mc.displayGuiScreen(new GuiEditor());
                }
            });
        }
    }

    /*
     *** Tails syncing ***
     */
    @SubscribeEvent
    public void onConnectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        //Add local player texture to map
        if (Tails.localOutfit != null) {
            OutfitManager.INSTANCE.setActiveOutfit(Minecraft.getInstance().getSession().getProfile().getId(), Tails.localOutfit);
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        Tails.hasRemote = false;
        sentPartInfoToServer = false;
        clearAllPartInfo = true;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.START) {
            if (clearAllPartInfo) {
                OutfitManager.INSTANCE.clearAllPartsData();
                clearAllPartInfo = false;
            }
            //World can't be null if we want to send a packet it seems
            else if (!sentPartInfoToServer && Minecraft.getInstance().world != null) {
                Tails.networkWrapper.sendToServer(new PlayerDataMessage(Minecraft.getInstance().getSession().getProfile().getId(), Tails.localOutfit, false));
                sentPartInfoToServer = true;
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        ((ClientProxy) Tails.proxy).partRenderer.doRender();
    }
}
