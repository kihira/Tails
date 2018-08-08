package uk.kihira.tails.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.kihira.tails.client.gui.GuiEditor;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;
import uk.kihira.tails.proxy.ClientProxy;

@SideOnly(Side.CLIENT)
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
            event.getButtonList().add(new GuiButton(TAILS_BUTTON_ID, (event.getGui().width / 2) - 35, event.getGui().height - 25, 70, 20, I18n.format("gui.button.editor")));
        }
    }

    @SubscribeEvent
    public void onButtonClickPre(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (event.getGui() instanceof GuiIngameMenu) {
            if (event.getButton().id == TAILS_BUTTON_ID) {
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
        if (Tails.localOutfit != null) {
            Tails.proxy.setActiveOutfit(Minecraft.getMinecraft().getSession().getProfile().getId(), Tails.localOutfit);
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
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.START) {
            if (clearAllPartInfo) {
                Tails.proxy.clearAllPartsData();
                clearAllPartInfo = false;
            }
            //World can't be null if we want to send a packet it seems
            else if (!sentPartInfoToServer && Minecraft.getMinecraft().world != null) {
                Tails.networkWrapper.sendToServer(new PlayerDataMessage(Minecraft.getMinecraft().getSession().getProfile().getId(), Tails.localOutfit, false));
                sentPartInfoToServer = true;
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        ((ClientProxy) Tails.proxy).partRenderer.doRender();
    }
}
