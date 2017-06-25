package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiListExtended;

public interface IListCallback<T extends GuiListExtended.IGuiListEntry> {

    boolean onEntrySelected(GuiList guiList, int index, T entry);
}
