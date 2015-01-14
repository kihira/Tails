package kihira.tails.client.gui;

import cpw.mods.fml.client.config.GuiButtonExt;
import kihira.foxlib.client.toast.ToastManager;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.LibraryEntryData;
import kihira.tails.common.Tails;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class LibrarySharePanel extends Panel<GuiEditor> {

    GuiTextField shareTextField;

    public LibrarySharePanel(GuiEditor parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        shareTextField = new GuiTextField(fontRendererObj, 3, height - 38, width - 6, 15);
        GuiButton button;

        //Import Skin
        button = new GuiButtonExt(0, 3, 3, width - 6, 18, "Import from Skin");
        button.enabled = TextureHelper.hasSkinData(mc.thePlayer);
        buttonList.add(button);

        buttonList.add(new GuiButtonExt(1, 3, height - 58, width - 6, 18, "Import from String"));
        buttonList.add(new GuiButtonExt(2, 3, height - 21, width - 6, 18, "Export to String"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            TextureHelper.buildPlayerPartsData(mc.thePlayer);
            ToastManager.INSTANCE.createCenteredToast(parent.width, parent.height - 50, 200, "Loaded data from your skin!");
            TextureHelper.hasSkinData(mc.thePlayer);
        }
        //Import from string
        else if (button.id == 1) {

        }
        //Export to String
        else if (button.id == 2) {
            StringBuilder sb = new StringBuilder();
            LibraryEntryData libData = parent.libraryInfoPanel.getEntry().data;
            sb.append(libData.entryName).append(":");
            sb.append(libData.creatorUUID).append(":");
            sb.append(Tails.gson.toJson(libData.partsData));

            shareTextField.setText(sb.toString());
            ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height / 2, parent.width / 2, "Copied library data to clipboard!");
            GuiScreen.setClipboardString(sb.toString());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        drawGradientRect(0, 0, width, height, 0xCC000000, 0xCC000000);
        shareTextField.drawTextBox();

        super.drawScreen(mouseX, mouseY, p_73863_3_);
    }
}
