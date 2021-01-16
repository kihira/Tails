package uk.kihira.tails.client.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.GuiUtils;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.common.Config;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;

public class LibraryListEntry extends AbstractList.AbstractListEntry<LibraryListEntry>
{
    public final LibraryEntryData data;

    public LibraryListEntry(LibraryEntryData libraryEntryData) {
        this.data = libraryEntryData;
    }

    @Override
    public void render(MatrixStack matrixStack, int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks)
    {
        if (this.data.remoteEntry)
        {
            Minecraft.getInstance().getTextureManager().bindTexture(IconButton.ICONS_TEXTURES);
            IconButton.Icons icon = IconButton.Icons.SERVER;
            matrixStack.push();
            matrixStack.translate(x + listWidth - 16, y + slotHeight - 12, 0F);
            matrixStack.scale(0.8F, 0.8F, 1F);
            GuiUtils.drawTexturedModalRect(matrixStack, 0, 0, icon.u, icon.v, 16, 16, 10);
            matrixStack.pop();
        }

        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        fontRenderer.drawString(matrixStack, (this.data.outfit.equals(Config.localOutfit.get()) ? TextFormatting.GREEN + "" + TextFormatting.ITALIC : "") + this.data.entryName, 5, y + 3, 0xFFFFFF);

        //todo fontRenderer.setUnicodeFlag(true);
        int offset = 0;
        for (OutfitPart part : data.outfit.parts)
        {
            fontRenderer.drawString(matrixStack, I18n.format(PartRegistry.getPart(part.basePart).get().name), x + 5, y + 12 + (8 * offset), 0xFFFFFF);
            for (int i = 1; i < 4; i++)
            {
                //todo Gui.drawRect(listWidth - (8 * i), y + 13 + (offset * 8), listWidth + 7 - (8 * i), y + 20 + (offset * 8), part.tint[i - 1]);
            }
            offset++;
        }
        //fontRenderer.setUnicodeFlag(false);

        if (this.data.favourite)
        {
            Minecraft.getInstance().getTextureManager().bindTexture(IconButton.ICONS_TEXTURES);
            IconButton.Icons icon = IconButton.Icons.STAR;
            matrixStack.push();
            matrixStack.translate(x + listWidth - 16, y, 0F);
            matrixStack.scale(0.8F, 0.8F, 1F);
            GuiUtils.drawTexturedModalRect(matrixStack, 0, 0, icon.u, icon.v + 32, 16, 16, 10);
            matrixStack.pop();
        }
    }

    public static class NewLibraryListEntry extends LibraryListEntry
    {
        private final LibraryPanel panel;

        public NewLibraryListEntry(LibraryPanel panel, LibraryEntryData libraryEntryData)
        {
            super(libraryEntryData);
            this.panel = panel;
        }

        @Override
        public void render(MatrixStack matrixStack, int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks)
        {
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, I18n.format("gui.library.create"), x + 3, y + (slotHeight / 2) - 4, 0xFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            //Create entry and add to library
            GameProfile profile = Minecraft.getInstance().player.getGameProfile();
            LibraryEntryData data = new LibraryEntryData(profile.getId(), profile.getName(), I18n.format("gui.library.entry.default"), Config.localOutfit.get());
            Tails.proxy.getLibraryManager().addEntry(data);
            this.panel.addSelectedEntry(new LibraryListEntry(data));
            return false;
        }
    }
}
