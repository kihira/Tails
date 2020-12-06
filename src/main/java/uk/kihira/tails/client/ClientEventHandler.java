package uk.kihira.tails.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uk.kihira.tails.client.gui.GuiEditor;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;
import uk.kihira.tails.proxy.ClientProxy;

public class ClientEventHandler 
{
    private static final int TAILS_BUTTON_ID = 1234;

    private boolean sentPartInfoToServer = false;
    private boolean clearAllPartInfo = false;

    /*
     *** Tails Editor Button ***
     */
    @SubscribeEvent
    public void onScreenInitPost(GuiScreenEvent.InitGuiEvent.Post event) 
    {
        if (event.getGui() instanceof IngameMenuScreen) 
        {
            Button tailsButton = new Button((event.getGui().width / 2) - 35, event.getGui().height - 25, 70, 20, new TranslationTextComponent("tails.gui.button.editor"), (button) -> 
            {
                event.getGui().getMinecraft().displayGuiScreen(new GuiEditor());
                event.setCanceled(true);
            });
            event.addWidget(tailsButton);
        }
    }

    /*
     *** Tails syncing ***
     */
    @SubscribeEvent
    public void onConnectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        //Add local player texture to map
        if (Tails.localOutfit != null) {
            Tails.proxy.setActiveOutfit(Minecraft.getInstance().getSession().getProfile().getId(), Tails.localOutfit);
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) 
    {
        Tails.hasRemote = false;
        sentPartInfoToServer = false;
        clearAllPartInfo = true;

        Tails.instance.loadConfig();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) 
    {
        if (e.phase == TickEvent.Phase.START)
        {
            if (clearAllPartInfo) 
            {
                Tails.proxy.clearAllPartsData();
                clearAllPartInfo = false;
            }
            //World can't be null if we want to send a packet it seems
            else if (!sentPartInfoToServer && Minecraft.getInstance().world != null) 
            {
                Tails.networkWrapper.sendToServer(new PlayerDataMessage(Minecraft.getInstance().getSession().getProfile().getId(), Tails.localOutfit, false));
                sentPartInfoToServer = true;
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) 
    {
        ((ClientProxy) Tails.proxy).partRenderer.doRender();
    }
}
