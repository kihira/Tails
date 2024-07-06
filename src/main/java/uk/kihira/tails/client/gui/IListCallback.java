package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.components.ObjectSelectionList;

public interface IListCallback<T extends ObjectSelectionList.Entry<T>>
{
    boolean onEntrySelected(GuiList<T> guiList, int index, T entry);
}
