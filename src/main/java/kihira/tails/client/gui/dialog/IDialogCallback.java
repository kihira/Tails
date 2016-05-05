package kihira.tails.client.gui.dialog;

import net.minecraft.client.gui.GuiButton;

public interface IDialogCallback {

    void buttonPressed(Dialog dialog, GuiButton button);
}
