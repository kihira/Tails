package kihira.tails.client.gui;

import kihira.foxlib.client.gui.GuiIconButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class LibraryInfoPanel extends Panel<GuiEditor> {

    private LibraryEntry entry;

    private GuiTextField textField;
    private GuiIconButton.GuiIconToggleButton favButton;

    public LibraryInfoPanel(GuiEditor parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        textField = new GuiTextField(fontRendererObj, 3, 3, width - 20, 15);
        textField.setMaxStringLength(16);
        textField.setFocused(true);
        textField.setCursorPositionEnd();

        buttonList.add(favButton = new GuiIconButton.GuiIconToggleButton(0, 5, height - 20, GuiIconButton.Icons.STAR, "Favourite"));
        buttonList.add(new GuiIconButton(1, 21, height - 20, GuiIconButton.Icons.DELETE, "Delete"));
        buttonList.add(new GuiIconButton(2, 36, height - 20, GuiIconButton.Icons.UPLOAD, "Upload to server"));
        buttonList.add(new GuiIconButton(3, 53, height - 20, GuiIconButton.Icons.DOWNLOAD, "Save locally"));
        super.initGui();

        setEntry(null);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        zLevel = -100;
        drawHorizontalLine(0, width, height, 0xFF000000);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(8421504);
        tessellator.addVertexWithUV(0, height, 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(width, height, 0.0D, 1.0D, 1.0D);
        tessellator.addVertexWithUV(width, 0, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(0, 0, 0.0D, 0.0D, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        drawGradientRect(1, 1, width - 1, height - 1, 0xFF000000, 0xFF000000);

        if (entry != null) {
            textField.drawTextBox();

            fontRendererObj.setUnicodeFlag(true);
            fontRendererObj.drawSplitString(EnumChatFormatting.ITALIC + entry.comment, 5, 40, width, 0xFFFFFF);
            fontRendererObj.setUnicodeFlag(false);
        }

        super.drawScreen(mouseX, mouseY, p_73863_3_);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        //Favourite
        if (button.id == 0) {
            entry.favourite = ((GuiIconButton.GuiIconToggleButton) button).toggled;
        }
        //Delete
        if (button.id == 1) {
            ((GuiIconButton) button).setHover(false);
            parent.libraryPanel.removeEntry(entry);
            setEntry(null);
        }
        parent.libraryPanel.saveLibrary();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        textField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char key, int keycode) {
        textField.textboxKeyTyped(key, keycode);

        if (textField.isFocused() && entry != null) {
            entry.entryName = textField.getText();
            parent.libraryPanel.saveLibrary();
        }
        super.keyTyped(key, keycode);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(true);
        super.onGuiClosed();
    }

    public void setEntry(LibraryEntry entry) {
        this.entry = entry;
        if (entry == null) {
            textField.setVisible(false);
            for (Object button : buttonList) {
                ((GuiButton) button).visible = false;
            }
        }
        else {
            favButton.toggled = entry.favourite;
            textField.setVisible(true);
            textField.setText(entry.entryName);
            for (Object obj : buttonList) {
                GuiButton button = (GuiButton) obj;
                button.visible = true;

                //Download
                if (button.id == 3 && !(entry instanceof LibraryEntry.RemoteLibraryEntry)) {
                    button.visible = false;
                }
                //Upload
                else if (button.id == 2 && entry instanceof LibraryEntry.RemoteLibraryEntry) {
                    button.visible = false;
                }
            }
        }
    }
}
