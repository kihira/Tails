package kihira.tails.client.gui;

import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.client.config.GuiUtils;
import kihira.foxlib.client.gui.GuiIconButton;
import kihira.tails.client.PartRegistry;
import kihira.tails.common.PartInfo;
import kihira.tails.common.PartsData;
import kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

public class LibraryEntry implements GuiListExtended.IGuiListEntry {

    @Expose public PartsData partsData;
    @Expose public String entryName = "";
    @Expose public String comment = "";
    @Expose public GameProfile creatorGameProfile;
    @Expose public boolean favourite;
    @Expose public long creationDate;

    public LibraryEntry(GameProfile gameProfile, PartsData partsData) {
        this(partsData);
        this.creatorGameProfile = gameProfile;
    }

    public LibraryEntry(PartsData partsData) {
        this.partsData = partsData;
        this.creationDate = Minecraft.getSystemTime();
    }

    @Override
    public void drawEntry(int index, int x, int y, int listWidth, int slotHeight, Tessellator tessellator, int mouseX, int mouseY, boolean mouseOver) {
        FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRenderer;
        fontRendererObj.drawString((partsData.equals(Tails.localPartsData) ? EnumChatFormatting.GREEN + "" + EnumChatFormatting.ITALIC : "") + entryName, 5, y + 3, 0xFFFFFF);

        fontRendererObj.setUnicodeFlag(true);
        PartInfo partInfo = partsData.getPartInfo(PartsData.PartType.TAIL);
        fontRendererObj.drawString(I18n.format(PartRegistry.getRenderPart(partInfo.partType, partInfo.typeid).getUnlocalisedName(partInfo.subid)), x + 5, y + 12, 0xFFFFFF);
        for (int i = 1; i < 4; i++) {
            //libraryPanel.drawGradientRect((int) (listWidth * 1.25) - (10 * i), -1, (int) (listWidth * 1.25) + 10 - (10 * i), 9, partInfo.tints[i - 1], partInfo.tints[i - 1]);
        }

        partInfo = partsData.getPartInfo(PartsData.PartType.EARS);
        fontRendererObj.drawString(I18n.format(PartRegistry.getRenderPart(partInfo.partType, partInfo.typeid).getUnlocalisedName(partInfo.subid)), x + 5, y + 19, 0xFFFFFF);
        for (int i = 1; i < 4; i++) {
            //libraryPanel.drawGradientRect((int) (listWidth * 1.25) - (10 * i), -1, (int) (listWidth * 1.25) + 10 - (10 * i), 9, partInfo.tints[i - 1], partInfo.tints[i - 1]);
        }

        partInfo = partsData.getPartInfo(PartsData.PartType.WINGS);
        fontRendererObj.drawString(I18n.format(PartRegistry.getRenderPart(partInfo.partType, partInfo.typeid).getUnlocalisedName(partInfo.subid)), x + 5, y + 26, 0xFFFFFF);
        for (int i = 1; i < 4; i++) {
            //libraryPanel.drawGradientRect((int) (listWidth * 1.25) - (10 * i), -1, (int) (listWidth * 1.25) + 10 - (10 * i), 9, partInfo.tints[i - 1], partInfo.tints[i - 1]);
        }
        fontRendererObj.setUnicodeFlag(false);

        if (favourite) {
            Minecraft.getMinecraft().renderEngine.bindTexture(GuiIconButton.iconsTextures);
            GuiIconButton.Icons icon = GuiIconButton.Icons.STAR;
            GL11.glPushMatrix();
            GL11.glTranslatef(x + listWidth - 16, y, 0F);
            GL11.glScalef(0.8F, 0.8F, 1F);
            GuiUtils.drawTexturedModalRect(0, 0, icon.u, icon.v + 32, 16, 16, 10);
            GL11.glPopMatrix();
        }
    }

    @Override
    public boolean mousePressed(int index, int mouseX, int mouseY, int mouseEvent, int mouseSlotX, int mouseSlotY) {
        return false;
    }

    @Override
    public void mouseReleased(int index, int mouseX, int mouseY, int mouseEvent, int mouseSlotX, int mouseSlotY) {

    }

    public static class NewLibraryEntry extends LibraryEntry {

        private final LibraryPanel panel;

        public NewLibraryEntry(LibraryPanel panel, PartsData partsData) {
            super(partsData);
            this.panel = panel;
        }

        @Override
        public void drawEntry(int index, int x, int y, int listWidth, int slotHeight, Tessellator tessellator, int mouseX, int mouseY, boolean mouseOver) {
            Minecraft.getMinecraft().fontRenderer.drawString(I18n.format("gui.library.create"), x + 3, y + (slotHeight / 2) - 4, 0xFFFFFF);
        }

        @Override
        public boolean mousePressed(int index, int mouseX, int mouseY, int mouseEvent, int mouseSlotX, int mouseSlotY) {
            LibraryEntry libraryEntry = new LibraryEntry(Minecraft.getMinecraft().getSession().func_148256_e(), Tails.localPartsData);
            libraryEntry.entryName = I18n.format("gui.library.entry.default");
            panel.addSelectedEntry(libraryEntry);
            return false;
        }
    }

    public static class RemoteLibraryEntry extends LibraryEntry {

        public RemoteLibraryEntry(PartsData partsData) {
            super(partsData);
        }

        @Override
        public void drawEntry(int index, int x, int y, int listWidth, int slotHeight, Tessellator tessellator, int mouseX, int mouseY, boolean mouseOver) {
            super.drawEntry(index, x, y, listWidth, slotHeight, tessellator, mouseX, mouseY, mouseOver);

            Minecraft.getMinecraft().renderEngine.bindTexture(GuiIconButton.iconsTextures);
            GuiIconButton.Icons icon = GuiIconButton.Icons.SERVER;
            GL11.glPushMatrix();
            GL11.glTranslatef(x + listWidth - 16, y + slotHeight - 12, 0F);
            GL11.glScalef(0.8F, 0.8F, 1F);
            GuiUtils.drawTexturedModalRect(0, 0, icon.u, icon.v, 16, 16, 10);
            GL11.glPopMatrix();
        }
    }
}
