package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiTextField;
import uk.kihira.tails.client.OutfitPart;

import java.io.IOException;

/**
 * A panel for editing the currently selected part
 */
public class PartsEditorPanel extends Panel<GuiEditor> {
    private static final int X_POSITION_TEXT_FIELD = 0;
    private static final int Y_POSITION_TEXT_FIELD = 1;
    private static final int Z_POSITION_TEXT_FIELD = 2;
    private static final int X_ROTATION_TEXT_FIELD = 0;
    private static final int Y_ROTATION_TEXT_FIELD = 1;
    private static final int Z_ROTATION_TEXT_FIELD = 2;

    private GuiTextField xPosTextField;
    private GuiTextField yPosTextField;
    private GuiTextField zPosTextField;
    private GuiTextField xRotTextField;
    private GuiTextField yRotTextField;
    private GuiTextField zRotTextField;

    public PartsEditorPanel(GuiEditor parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    @Override
    public void initGui() {
        super.initGui();

        xPosTextField = new GuiTextField(X_POSITION_TEXT_FIELD, fontRenderer, 20, 10, 50, 10);
        yPosTextField = new GuiTextField(Y_POSITION_TEXT_FIELD, fontRenderer, 20, 25, 50, 10);
        zPosTextField = new GuiTextField(Z_POSITION_TEXT_FIELD, fontRenderer, 20, 40, 50, 10);

        xRotTextField = new GuiTextField(X_ROTATION_TEXT_FIELD, fontRenderer, 20, 65, 50, 10);
        yRotTextField = new GuiTextField(Y_ROTATION_TEXT_FIELD, fontRenderer, 20, 80, 50, 10);
        zRotTextField = new GuiTextField(Z_ROTATION_TEXT_FIELD, fontRenderer, 20, 95, 50, 10);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        xPosTextField.drawTextBox();
        yPosTextField.drawTextBox();
        zPosTextField.drawTextBox();
        xRotTextField.drawTextBox();
        yRotTextField.drawTextBox();
        zRotTextField.drawTextBox();
    }

    @Override
    public void keyTyped(char key, int keycode) {
        xPosTextField.textboxKeyTyped(key, keycode);
        yPosTextField.textboxKeyTyped(key, keycode);
        zPosTextField.textboxKeyTyped(key, keycode);
        xRotTextField.textboxKeyTyped(key, keycode);
        yRotTextField.textboxKeyTyped(key, keycode);
        zRotTextField.textboxKeyTyped(key, keycode);

        updatePart();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        xPosTextField.mouseClicked(mouseX, mouseY, mouseButton);
        yPosTextField.mouseClicked(mouseX, mouseY, mouseButton);
        zPosTextField.mouseClicked(mouseX, mouseY, mouseButton);
        xRotTextField.mouseClicked(mouseX, mouseY, mouseButton);
        yRotTextField.mouseClicked(mouseX, mouseY, mouseButton);
        zRotTextField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void updatePart() {
        OutfitPart outfitPart = parent.getCurrentOutfitPart();
        if (outfitPart == null) return;

        outfitPart.mountOffset[0] = Float.parseFloat(xPosTextField.getText());
        outfitPart.mountOffset[1] = Float.parseFloat(yPosTextField.getText());
        outfitPart.mountOffset[2] = Float.parseFloat(zPosTextField.getText());

        outfitPart.rotation[0] = Float.parseFloat(xRotTextField.getText());
        outfitPart.rotation[1] = Float.parseFloat(yRotTextField.getText());
        outfitPart.rotation[2] = Float.parseFloat(zRotTextField.getText());
    }
}
