package uk.kihira.tails.client.gui.dialog;

import net.minecraft.client.gui.widget.button.Button;

public interface IDialogCallback
{
    void buttonPressed(Dialog<?> dialog, Button button);
}
