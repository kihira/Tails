/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */
package kihira.tails.client.gui;

import kihira.foxlib.client.gui.GuiList;
import kihira.foxlib.client.gui.IListCallback;
import kihira.tails.client.FakeEntity;
import kihira.tails.client.PartRegistry;
import kihira.tails.client.render.RenderPart;
import kihira.tails.common.PartInfo;
import kihira.tails.common.PartsData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PartsPanel extends Panel<GuiEditor> implements IListCallback<PartsPanel.PartEntry> {

    private GuiList<PartEntry> partList;
    private final FakeEntity fakeEntity;

    public PartsPanel(GuiEditor parent, int left, int top, int right, int bottom) {
        super(parent, left, top, right, bottom);
        alwaysReceiveMouse = true;

        fakeEntity = new FakeEntity(Minecraft.getMinecraft().theWorld);
    }

    @Override
    public void initGui() {
        initPartList();
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        //zLevel = -100;
        drawGradientRect(0, 0, width, height, 0xCC000000, 0xCC000000);
        zLevel = 0;
        GlStateManager.color(1, 1, 1, 1);
        //Tails list
        partList.drawScreen(mouseX, mouseY, p_73863_3_);

        super.drawScreen(mouseX, mouseY, p_73863_3_);
    }

    void initPartList() {
        //Part List
        List<PartEntry> partList = new ArrayList<PartEntry>();
        PartsData.PartType partType = parent.getPartType();
        partList.add(new PartEntry(new PartInfo(false, 0, 0, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null, partType))); //No tail
        //Generate tail preview textures and add to list
        List<RenderPart> parts = PartRegistry.getParts(partType);
        for (int type = 0; type < parts.size(); type++) {
            for (int subType = 0; subType <= parts.get(type).getAvailableSubTypes(); subType++) {
                PartInfo partInfo = new PartInfo(true, type, subType, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null, partType);
                partList.add(new PartEntry(partInfo));
            }
        }

        this.partList = new GuiList<PartEntry>(this, width, height, 0, height, 55, partList);
        this.partList.width = width;
        selectDefaultListEntry();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.partList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.partList.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException {
        this.partList.handleMouseInput();
    }

    @Override
    public void onGuiClosed() {
        //Delete textures on close
        for (PartEntry entry : this.partList.getEntries()) {
            entry.partInfo.setTexture(null);
        }
    }

    @Override
    public boolean onEntrySelected(GuiList guiList, int index, PartEntry entry) {
        //Reset texture ID
        parent.textureID = 0;
        //Need to keep tints from original part
        PartInfo partInfo = new PartInfo(entry.partInfo.hasPart, entry.partInfo.typeid, entry.partInfo.subid, entry.partInfo.textureID,
                parent.getEditingPartInfo().tints.clone(), entry.partInfo.partType, null);
        parent.setPartsInfo(partInfo);
        return true;
    }

    void selectDefaultListEntry() {
        //Default selection
        for (GuiListExtended.IGuiListEntry entry : partList.getEntries()) {
            PartEntry partEntry = (PartEntry) entry;
            PartInfo partInfo = parent.getEditingPartInfo();
            if ((!partEntry.partInfo.hasPart && !partInfo.hasPart) || (partInfo.hasPart && partEntry.partInfo.hasPart
                    && partEntry.partInfo.typeid == partInfo.typeid && partEntry.partInfo.subid == partInfo.subid)) {
                partList.setCurrrentIndex(partList.getEntries().indexOf(partEntry));
                break;
            }
        }
    }

    private void renderPart(int x, int y, int z, int scale, PartInfo partInfo) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glScalef(-scale, scale, 1F);

        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderManager().playerViewY = 180.0F;
        PartRegistry.getRenderPart(partInfo.partType, partInfo.typeid).render(fakeEntity, partInfo, 0, 0, 0, 0);
        RenderHelper.disableStandardItemLighting();
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        GL11.glPopMatrix();
    }

    public class PartEntry implements GuiListExtended.IGuiListEntry {

        public final PartInfo partInfo;

        public PartEntry(PartInfo partInfo) {
            this.partInfo = partInfo;
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
            if (partInfo.hasPart) {
                boolean currentPart = partList.getCurrrentIndex() == slotIndex;
                renderPart(right - 25, y - 25, currentPart ? 10 : 1, 50, partInfo);
                fontRendererObj.drawString(I18n.format(PartRegistry.getRenderPart(partInfo.partType, partInfo.typeid)
                        .getUnlocalisedName(partInfo.subid)), 5, y + 17, 0xFFFFFF);

                if (currentPart) {
                    RenderPart renderPart = PartRegistry.getRenderPart(parent.getPartType(), partInfo.typeid);
                    if (renderPart.getModelAuthor() != null) {
                        //Yeah its not nice but eh, works
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(5, y + 27, 0);
                        GlStateManager.scale(0.6F, 0.6F, 1F);
                        zLevel = 100;
                        fontRendererObj.drawString(I18n.format("gui.createdby") + ":", 0, 0, 0xFFFFFF);
                        GlStateManager.translate(0, 10, 0);
                        fontRendererObj.drawString(TextFormatting.AQUA + renderPart.getModelAuthor(), 0, 0, 0xFFFFFF);
                        GlStateManager.popMatrix();
                        zLevel = 0;
                    }
                }
            }
            else {
                fontRendererObj.drawString(I18n.format("tail.none.name"), 5, y + (partList.slotHeight / 2) - 5, 0xFFFFFF);
            }
        }

        @Override
        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {}

        @Override
        public boolean mousePressed(int index, int mouseX, int mouseY, int mouseEvent, int mouseSlotX, int mouseSlotY) {
/*            RenderPart renderPart = PartRegistry.getRenderPart(parent.getPartType(), partInfo.typeid);
            if (partList.getCurrrentIndex() == index && renderPart.hasAuthor(partInfo.subid, partInfo.textureID)) {
                String author = renderPart.getAuthor(partInfo.subid, partInfo.textureID);
                if (author.startsWith("@")) {
                    float authorNameWidth = fontRendererObj.getStringWidth(author) * 0.6F;
                    if (mouseSlotX > 5 && mouseSlotX < 5 + authorNameWidth && mouseSlotY > 30 && mouseSlotY < 38) {
                        try {
                            Desktop.getDesktop().browse(URI.create("https://twitter.com/" + author.replace("@", "")));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }*/
            return true;
        }

        @Override
        public void mouseReleased(int index, int mouseX, int mouseY, int mouseEvent, int mouseSlotX, int mouseSlotY) {
            //System.out.println(partList.getListEntry(index));
        }
    }
}
