package uk.kihira.tails.client.gui.dialog;

import net.minecraft.client.gui.components.events.GuiEventListener;

public interface IDialogCallback
{
    void buttonPressed(Dialog<?> dialog, GuiEventListener button);
}
