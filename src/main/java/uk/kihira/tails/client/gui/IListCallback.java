package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiListExtended;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IListCallback<T extends GuiListExtended.IGuiListEntry> {

    boolean onEntrySelected(GuiList guiList, int index, T entry);
}
