package kihira.tails.client.gui;

import com.google.common.base.Strings;
import com.google.gson.JsonSyntaxException;
import kihira.foxlib.client.toast.ToastManager;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.LibraryEntryData;
import kihira.tails.common.PartsData;
import kihira.tails.common.Tails;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.io.IOException;
import java.util.UUID;

public class LibraryImportPanel extends Panel<GuiEditor> {

    GuiTextField inputField;

    public LibraryImportPanel(GuiEditor parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        GuiButton button;

        //Import Skin
        button = new GuiButtonExt(0, 3, 3, width - 6, 18, I18n.format("gui.library.import.skin"));
        button.enabled = TextureHelper.hasSkinData(mc.thePlayer);
        buttonList.add(button);

        buttonList.add(new GuiButtonExt(1, 3, 21, width - 6, 18, I18n.format("gui.library.import.string")));

        inputField = new GuiTextField(2, fontRendererObj, 3, 41, width - 6, 15);
        inputField.setMaxStringLength(5000);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            TextureHelper.buildPlayerPartsData(mc.thePlayer);
            ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2, TextFormatting.GREEN + I18n.format("gui.library.import.toast.skin"));
        }
        //Import from string
        else if (button.id == 1) {
            if (Strings.isNullOrEmpty(inputField.getText()) || inputField.getText().split(":", 3).length != 3) {
                ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2,
                        TextFormatting.RED + I18n.format("gui.library.import.toast.invalid"));
            }
            else {
                String[] strings = inputField.getText().split(":", 4);
                try {
                    LibraryEntryData entryData = new LibraryEntryData(UUID.fromString(strings[1]), strings[2], strings[0], Tails.gson.fromJson(strings[3], PartsData.class));
                    Tails.proxy.getLibraryManager().addEntry(entryData);
                    parent.libraryPanel.initList();

                    ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2,
                            TextFormatting.GREEN + I18n.format("gui.library.import.toast.success", strings[0]));

                } catch (IllegalArgumentException e) {
                    ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2,
                            TextFormatting.RED + I18n.format("gui.library.import.toast.invalid.uuid"));
                } catch (JsonSyntaxException e) {
                    ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2,
                            TextFormatting.RED + I18n.format("gui.library.import.toast.invalid.parts"));
                }
            }
        }
    }

    @Override
    public void keyTyped(char key, int keycode) {
        inputField.textboxKeyTyped(key, keycode);
        super.keyTyped(key, keycode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        inputField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        drawGradientRect(0, 0, width, height, 0xDE000000, 0xDE000000);
        inputField.drawTextBox();

        super.drawScreen(mouseX, mouseY, p_73863_3_);
    }
}
