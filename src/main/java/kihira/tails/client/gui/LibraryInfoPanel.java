package kihira.tails.client.gui;

import kihira.foxlib.client.gui.GuiIconButton;
import kihira.foxlib.client.toast.ToastManager;
import kihira.tails.common.LibraryEntryData;
import kihira.tails.common.Tails;
import kihira.tails.common.network.LibraryEntriesMessage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class LibraryInfoPanel extends Panel<GuiEditor> {

    private LibraryListEntry entry;

    private GuiTextField textField;
    private GuiIconButton.GuiIconToggleButton favButton;

    public LibraryInfoPanel(GuiEditor parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        textField = new GuiTextField(-1, fontRendererObj, 6, 6, width - 12, 15);
        textField.setMaxStringLength(16);

        buttonList.add(favButton = new GuiIconButton.GuiIconToggleButton(0, 5, height - 20, GuiIconButton.Icons.STAR, StatCollector.translateToLocal("gui.button.favourite")));
        buttonList.add(new GuiIconButton(1, 21, height - 20, GuiIconButton.Icons.DELETE, StatCollector.translateToLocal("gui.button.delete")));
        buttonList.add(new GuiIconButton(2, 36, height - 20, GuiIconButton.Icons.UPLOAD, StatCollector.translateToLocal("gui.button.upload")));
        buttonList.add(new GuiIconButton(3, 53, height - 20, GuiIconButton.Icons.DOWNLOAD, StatCollector.translateToLocal("gui.button.savelocal")));
        buttonList.add(new GuiIconButton(4, 68, height - 20, GuiIconButton.Icons.EXPORT, StatCollector.translateToLocal("gui.button.share")));
        super.initGui();

        setEntry(null);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        zLevel = 0;
        drawGradientRect(0, 0, width, height, 0xCC000000, 0xCC000000);

        GL11.glColor4f(0F, 0F, 0F, 0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
        // TODO Port to 1.8.8
/*        renderer.startDrawingQuads();
        renderer.setColorOpaque_I(8421504);
        renderer.addVertexWithUV(2, height - 2, -10D, 0.0D, 1.0D);
        renderer.addVertexWithUV(width - 2, height - 2, -10D, 1.0D, 1.0D);
        renderer.addVertexWithUV(width - 2, 2, -10D, 1.0D, 0.0D);
        renderer.addVertexWithUV(2, 2, -10D, 0.0D, 0.0D);
        renderer.finishDrawing();*/
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        zLevel = 10;
        drawGradientRect(3, 3, width - 3, height - 3, 0xFF000000, 0xFF000000);

        if (entry != null) {
            textField.drawTextBox();

            fontRendererObj.setUnicodeFlag(true);
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.library.info.created") + ":", 5, height - 40, 0xAAAAAA);
            fontRendererObj.drawString(entry.data.creatorName, width - 5 - fontRendererObj.getStringWidth(entry.data.creatorName), height - 40, 0xAAAAAA);
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.library.info.createdate") + ":", 5, height - 32, 0xAAAAAA);
            String date = new SimpleDateFormat("dd/MM/YY").format(new Date(entry.data.creationDate));
            fontRendererObj.drawString(date, width - 5 - fontRendererObj.getStringWidth(date), height - 32, 0xAAAAAA);
            //fontRendererObj.drawSplitString(EnumChatFormatting.ITALIC + entry.data.comment, 5, 40, width, 0xFFFFFF);
            fontRendererObj.setUnicodeFlag(false);
        }

        super.drawScreen(mouseX, mouseY, p_73863_3_);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        //Favourite
        if (button.id == 0) {
            entry.data.favourite = ((GuiIconButton.GuiIconToggleButton) button).toggled;
        }
        //Delete
        else if (button.id == 1) {
            if (entry.data.remoteEntry) {
                //Only allow removing if player owns the entry
                if (!entry.data.creatorUUID.equals(mc.thePlayer.getUniqueID())) {
                    return;
                }
            }
            ((GuiIconButton) button).setHover(false);
            parent.libraryPanel.removeEntry(entry);
            setEntry(null);
        }
        //Upload
        else if (button.id == 2) {
            Tails.networkWrapper.sendToServer(new LibraryEntriesMessage(new ArrayList<LibraryEntryData>() {{ add(entry.data); }}, false));
            button.enabled = false;
        }
        //Download/save locally
        else if (button.id == 3) {
            entry.data.remoteEntry = false;
            button.enabled = false;
        }
        //Export to string
        else if (button.id == 4) {
            StringBuilder sb = new StringBuilder();
            LibraryEntryData libData = parent.libraryInfoPanel.getEntry().data;
            sb.append(libData.entryName).append(":");
            sb.append(libData.creatorUUID).append(":");
            sb.append(Tails.gson.toJson(libData.partsData));

            ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height / 2, parent.width / 2, StatCollector.translateToLocal("gui.library.info.toast.export"));
            GuiScreen.setClipboardString(sb.toString());
        }
        Tails.proxy.getLibraryManager().saveLibrary();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        textField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char key, int keycode) {
        textField.textboxKeyTyped(key, keycode);

        if (textField.isFocused() && entry != null) {
            entry.data.entryName = textField.getText();
            Tails.proxy.getLibraryManager().saveLibrary();
        }
        super.keyTyped(key, keycode);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(true);
        super.onGuiClosed();
    }

    public void setEntry(LibraryListEntry entry) {
        this.entry = entry;
        if (entry == null) {
            textField.setVisible(false);
            for (Object button : buttonList) {
                ((GuiButton) button).visible = false;
            }
        }
        else {
            favButton.toggled = entry.data.favourite;
            textField.setVisible(true);
            textField.setText(entry.data.entryName);
            for (Object obj : buttonList) {
                GuiButton button = (GuiButton) obj;
                button.visible = true;

                if (button.id == 1 && entry.data.remoteEntry && !entry.data.creatorUUID.equals(mc.thePlayer.getUniqueID())) {
                    button.visible = false;
                }
                //Download
                else if (button.id == 3 && !entry.data.remoteEntry) {
                    button.visible = false;
                }
                //Upload
                else if (button.id == 2 && (entry.data.remoteEntry || mc.isSingleplayer() || !Tails.hasRemote)) {
                    button.visible = false;
                }
            }
        }
    }

    public LibraryListEntry getEntry() {
        return this.entry;
    }
}
