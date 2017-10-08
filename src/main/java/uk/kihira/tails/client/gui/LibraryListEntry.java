package uk.kihira.tails.client.gui;

import com.mojang.authlib.GameProfile;
import uk.kihira.tails.client.OutfitPart;
import uk.kihira.tails.common.PartRegistry;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;


public class LibraryListEntry implements GuiListExtended.IGuiListEntry {

    public final LibraryEntryData data;

    public LibraryListEntry(LibraryEntryData libraryEntryData) {
        this.data = libraryEntryData;
    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
        if (data.remoteEntry) {
            Minecraft.getMinecraft().renderEngine.bindTexture(GuiIconButton.iconsTextures);
            GuiIconButton.Icons icon = GuiIconButton.Icons.SERVER;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + listWidth - 16, y + slotHeight - 12, 0F);
            GlStateManager.scale(0.8F, 0.8F, 1F);
            GuiUtils.drawTexturedModalRect(0, 0, icon.u, icon.v, 16, 16, 10);
            GlStateManager.popMatrix();
        }

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.drawString((data.outfit.equals(Tails.localOutfit) ? TextFormatting.GREEN + "" + TextFormatting.ITALIC : "") + data.entryName, 5, y + 3, 0xFFFFFF);

        fontRenderer.setUnicodeFlag(true);
        int offset = 0;
        for (OutfitPart part : data.outfit.parts) {
            fontRenderer.drawString(I18n.format(PartRegistry.getPart(part.basePart).name), x + 5, y + 12 + (8 * offset), 0xFFFFFF);
            for (int i = 1; i < 4; i++) {
                Gui.drawRect(listWidth - (8 * i), y + 13 + (offset * 8), listWidth + 7 - (8 * i), y + 20 + (offset * 8), part.tints[i - 1]);
            }
            offset++;
        }
        fontRenderer.setUnicodeFlag(false);

        if (data.favourite) {
            Minecraft.getMinecraft().renderEngine.bindTexture(GuiIconButton.iconsTextures);
            GuiIconButton.Icons icon = GuiIconButton.Icons.STAR;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + listWidth - 16, y, 0F);
            GlStateManager.scale(0.8F, 0.8F, 1F);
            GuiUtils.drawTexturedModalRect(0, 0, icon.u, icon.v + 32, 16, 16, 10);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {}

    @Override
    public boolean mousePressed(int index, int mouseX, int mouseY, int mouseEvent, int mouseSlotX, int mouseSlotY) {
        return false;
    }

    @Override
    public void mouseReleased(int index, int mouseX, int mouseY, int mouseEvent, int mouseSlotX, int mouseSlotY) {}

    public static class NewLibraryListEntry extends LibraryListEntry {

        private final LibraryPanel panel;

        public NewLibraryListEntry(LibraryPanel panel, LibraryEntryData libraryEntryData) {
            super(libraryEntryData);
            this.panel = panel;
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            Minecraft.getMinecraft().fontRenderer.drawString(I18n.format("gui.library.create"), x + 3, y + (slotHeight / 2) - 4, 0xFFFFFF);
        }

        @Override
        public boolean mousePressed(int index, int mouseX, int mouseY, int mouseEvent, int mouseSlotX, int mouseSlotY) {
            //Create entry and add to library
            GameProfile profile = Minecraft.getMinecraft().player.getGameProfile();
            LibraryEntryData data = new LibraryEntryData(profile.getId(), profile.getName(), I18n.format("gui.library.entry.default"), Tails.localOutfit);
            Tails.proxy.getLibraryManager().addEntry(data);
            panel.addSelectedEntry(new LibraryListEntry(data));
            return false;
        }
    }
}
