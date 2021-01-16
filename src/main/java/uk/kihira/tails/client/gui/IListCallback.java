package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.widget.list.AbstractList;

public interface IListCallback<T extends AbstractList.AbstractListEntry<T>>
{
    boolean onEntrySelected(GuiList<T> guiList, int index, T entry);
}
