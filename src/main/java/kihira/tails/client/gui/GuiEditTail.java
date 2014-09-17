/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.gui;

import com.google.common.base.Strings;
import kihira.foxlib.client.gui.GuiBaseScreen;
import kihira.foxlib.client.gui.GuiIconButton;
import kihira.foxlib.client.gui.GuiList;
import kihira.foxlib.client.gui.IListCallback;
import kihira.tails.client.ClientEventHandler;
import kihira.tails.client.FakeEntity;
import kihira.tails.client.gui.controls.GuiHSBSlider;
import kihira.tails.client.gui.controls.GuiHSBSlider.HSBSliderType;
import kihira.tails.client.gui.controls.GuiHSBSlider.IHSBSliderCallback;
import kihira.tails.client.render.RenderTail;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.TailInfo;
import kihira.tails.common.Tails;
import kihira.tails.common.network.TailInfoMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuiEditTail extends GuiBaseScreen implements IListCallback, IHSBSliderCallback {

    private float yaw = 0F;
    private float pitch = 10F;

    private int currTintEdit = 0;
    private int currTintColour = 0xFFFFFF;
    private GuiTextField hexText;
    private GuiHSBSlider[] hsbSliders;
    private GuiHSBSlider[] rgbSliders;
    private GuiIconButton tintReset;
    private int textureID;

    private ScaledResolution scaledRes;

    private int previewWindowLeft;
    private int previewWindowRight;
    private int previewWindowBottom;
    private int editPaneTop;
    private TailInfo tailInfo;
    private final TailInfo originalTailInfo;

    private GuiList tailList;
    private FakeEntity fakeEntity;

    public GuiEditTail() {
        //Backup original TailInfo or create default one
        TailInfo tailInfo;
        if (Tails.localPlayerTailInfo == null) {
            Tails.setLocalPlayerTailInfo(new TailInfo(Minecraft.getMinecraft().thePlayer.getPersistentID(), false, 0 , 0, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null));
        }
        tailInfo = Tails.localPlayerTailInfo;
        this.originalTailInfo = tailInfo.deepCopy();
        this.tailInfo = this.originalTailInfo.deepCopy();

        this.fakeEntity = new FakeEntity(Minecraft.getMinecraft().theWorld);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        this.scaledRes = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int previewWindowEdgeOffset = 110;
        this.previewWindowLeft = previewWindowEdgeOffset;
        this.previewWindowRight = this.width - previewWindowEdgeOffset;
        this.previewWindowBottom = this.height - 30;
        this.editPaneTop = this.height - 107;

        //Edit tint buttons
        int topOffset = 20;
        for (int i = 2; i <= 4; i++) {
            this.buttonList.add(new GuiButton(i, this.previewWindowRight + 30, topOffset, 40, 20, I18n.format("gui.button.edit")));
            topOffset += 35;
        }

        //Tint edit pane
        this.hexText = new GuiTextField(this.fontRendererObj, this.previewWindowRight + 30, this.editPaneTop + 20, 73, 10);
        this.hexText.setMaxStringLength(6);
        this.hexText.setText(Integer.toHexString(this.currTintColour));

        //RGB sliders
        rgbSliders = new GuiHSBSlider[3];
        rgbSliders[0] = new GuiHSBSlider(100, this.previewWindowRight + 5, this.editPaneTop + 70, 100, 10, this, HSBSliderType.SATURATION);
        rgbSliders[1] = new GuiHSBSlider(100, this.previewWindowRight + 5, this.editPaneTop + 80, 100, 10, this, HSBSliderType.SATURATION);
        rgbSliders[2] = new GuiHSBSlider(100, this.previewWindowRight + 5, this.editPaneTop + 90, 100, 10, this, HSBSliderType.SATURATION);
        rgbSliders[0].setHue(0);
        rgbSliders[1].setHue(1F/3F);
        rgbSliders[2].setHue(2F/3F);

        this.buttonList.add(rgbSliders[0]);
        this.buttonList.add(rgbSliders[1]);
        this.buttonList.add(rgbSliders[2]);

        //HBS sliders
        hsbSliders = new GuiHSBSlider[3];
        hsbSliders[0] = new GuiHSBSlider(15, this.previewWindowRight + 5, this.editPaneTop + 35, 100, 10, this, HSBSliderType.HUE);
        hsbSliders[1] = new GuiHSBSlider(16, this.previewWindowRight + 5, this.editPaneTop + 45, 100, 10, this, HSBSliderType.SATURATION);
        hsbSliders[2] = new GuiHSBSlider(17, this.previewWindowRight + 5, this.editPaneTop + 55, 100, 10, this, HSBSliderType.BRIGHTNESS);
        this.buttonList.add(hsbSliders[0]);
        this.buttonList.add(hsbSliders[1]);
        this.buttonList.add(hsbSliders[2]);

        //Reset/Save
        this.buttonList.add(this.tintReset = new GuiIconButton(8, this.width - 20, this.editPaneTop + 2, GuiIconButton.Icons.UNDO, new ArrayList<String>() {{ add(I18n.format("gui.button.reset")); }}));
        tintReset.enabled = false;

        //Tail List
        List<TailEntry> tailList = new ArrayList<TailEntry>();
        UUID uuid = UUID.fromString("18040390-23b0-11e4-8c21-0800200c9a66"); //Just a random UUID
        tailList.add(new TailEntry(new TailInfo(uuid, false, 0, 0, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null))); //No tail
        //Generate tail preview textures and add to list
        for (int type = 0; type < ClientEventHandler.tailTypes.length; type++) {
            for (int subType = 0; subType <= ClientEventHandler.tailTypes[type].getAvailableSubTypes(); subType++) {
                TailInfo tailInfo = new TailInfo(uuid, true, type, subType, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null);
                tailList.add(new TailEntry(tailInfo));
            }
        }

        this.tailList = new GuiList(this, this.previewWindowLeft, this.height - 43, 0, this.height - 43, 55, tailList);
        this.selectDefaultListEntry();

        //General Editing Pane
        //Reset/Save
        this.buttonList.add(new GuiButton(12, this.previewWindowRight - 83, this.height - 25, 40, 20, I18n.format("gui.button.reset")));
        this.buttonList.add(new GuiButton(13, this.previewWindowRight - 43, this.height - 25, 40, 20, I18n.format("gui.done")));

        //Export
        this.buttonList.add(new GuiButtonTooltip(14, (this.width / 2) - 20, this.height - 25, 40, 20, I18n.format("gui.button.export"),
                this.scaledRes.getScaledWidth() / 3, I18n.format("gui.button.export.0.tooltip")));

        //Texture select
        this.buttonList.add(new GuiButton(15, 5, this.height - 25, 15, 20, "<"));
        this.buttonList.add(new GuiButton(15, this.previewWindowLeft - 20, this.height - 25, 15, 20, ">"));

        this.refreshTintPane();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        this.zLevel = -1000;
        //Background
        this.drawGradientRect(0, 0, this.previewWindowLeft, this.height, 0xCC000000, 0xCC000000);
        this.drawGradientRect(this.previewWindowLeft, 0, this.previewWindowRight, this.previewWindowBottom, 0xEE000000, 0xEE000000); //Hex with alpha in the format ARGB
        this.drawGradientRect(this.previewWindowRight, 0, this.width, this.height, 0xCC000000, 0xCC000000);
        this.drawGradientRect(this.previewWindowLeft, this.previewWindowBottom, this.previewWindowRight, this.height, 0xDD000000, 0xDD000000);

        //Tints
        int topOffset = 10;
        for (int tint = 1; tint <= 3; tint++) {
            String s = I18n.format("gui.tint", tint);
            this.fontRendererObj.drawString(s, this.previewWindowRight+ 5, topOffset, 0xFFFFFF);
            this.drawGradientRect(this.previewWindowRight + 5, topOffset + 10, this.previewWindowRight + 25, topOffset + 30, tailInfo.tints[tint - 1], tailInfo.tints[tint - 1]);
            topOffset += 35;
        }

        //Editing tint pane
        if (this.currTintEdit > 0) {
            this.drawHorizontalLine(this.previewWindowRight, this.width, this.editPaneTop, 0xFF000000);
            this.fontRendererObj.drawString(I18n.format("gui.tint.edit", this.currTintEdit), this.previewWindowRight + 5, this.editPaneTop + 5, 0xFFFFFF);

            this.fontRendererObj.drawString(I18n.format("gui.hex") + ":", this.previewWindowRight + 5, this.editPaneTop + 21, 0xFFFFFF);
            this.hexText.drawTextBox();
        }

        //Player
        drawEntity(this.width / 2, (this.previewWindowBottom / 2) + (this.scaledRes.getScaledHeight() / 4), this.scaledRes.getScaledHeight() / 4, this.yaw, this.pitch, this.mc.thePlayer);

        this.zLevel = 0;
        //Tails list
        this.tailList.drawScreen(mouseX, mouseY, p_73863_3_);

        //Texture select
        fontRendererObj.drawString("Texture:", 7, this.height - 37, 0xFFFFFF);
        fontRendererObj.drawString(ClientEventHandler.tailTypes[tailInfo.typeid].getTextureNames()[tailInfo.textureID], 25, this.height - 19, 0xFFFFFF);
        textureID = tailInfo.textureID;

        super.drawScreen(mouseX, mouseY, p_73863_3_);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        RenderTail tail = ClientEventHandler.tailTypes[tailInfo.typeid];
        //Edit buttons
        if (button.id >= 2 && button.id <= 4) {
            this.currTintEdit = button.id - 1;
            this.currTintColour = this.tailInfo.tints[this.currTintEdit - 1] & 0xFFFFFF; //Ignore the alpha bits
            this.hexText.setText(Integer.toHexString(this.currTintColour));
            this.refreshTintPane();
            this.tintReset.enabled = false;
        }
        //Reset Tint
        else if (button.id == 8) {
            this.currTintColour = this.originalTailInfo.tints[this.currTintEdit - 1] & 0xFFFFFF; //Ignore the alpha bits
            this.hexText.setText(Integer.toHexString(this.currTintColour));
            this.refreshTintPane();
            tintReset.enabled = false;
        }
        //Reset All
        else if (button.id == 12) {
            this.tailInfo = this.originalTailInfo.deepCopy();
            this.selectDefaultListEntry();
            this.currTintEdit = 0;
            this.refreshTintPane();
            this.updateTailInfo();
        }
        //Save All
        else if (button.id == 13) {
            //Update tail info, set local and send it to the server
            this.updateTailInfo();
            Tails.setLocalPlayerTailInfo(this.tailInfo);
            Tails.proxy.addTailInfo(this.tailInfo.uuid, this.tailInfo);
            Tails.networkWrapper.sendToServer(new TailInfoMessage(tailInfo, false));

            this.mc.displayGuiScreen(null);
        }
        //Export
        else if (button.id == 14) {
            this.updateTailInfo();
            this.mc.displayGuiScreen(new GuiExport(this, this.tailInfo));
        }
        //Texture select
        else if (button.id == 150) {
            if (tail.getTextureNames().length > textureID + 1) {
                textureID++;
            }
            else {
                textureID = 0;
            }
            updateTailInfo();
        }
        else if (button.id == 160) {
            if (textureID - 1 > 0) {
                textureID--;
            }
            else {
                textureID = tail.getTextureNames().length - 1;
            }
            updateTailInfo();
        }
    }

    @Override
    protected void keyTyped(char letter, int keyCode) {
        this.hexText.textboxKeyTyped(letter, keyCode);

        try {
            //Gets the current colour from the hex text
            if (!Strings.isNullOrEmpty(this.hexText.getText())) {
                this.currTintColour = Integer.parseInt(this.hexText.getText(), 16);
            }
        } catch (NumberFormatException ignored) {}

        this.refreshTintPane();

        super.keyTyped(letter, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseEvent) {
/*        if (mouseEvent != 0 || !this.tailList.func_148179_a(mouseX, mouseY, mouseEvent)) {
            super.mouseClicked(mouseX, mouseY, mouseEvent);
        }*/
        super.mouseClicked(mouseX, mouseY, mouseEvent);
        this.hexText.mouseClicked(mouseX, mouseY, mouseEvent);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        if (lastButtonClicked == 0) {
            //Yaw
            if (mouseX > previewWindowLeft && mouseX < previewWindowRight) {
                float previewWindowWidth = previewWindowRight - previewWindowLeft;
                yaw = (mouseX - (width / 2F) / (previewWindowWidth / 2F)) * 2F;
            }
            //Pitch
/*            if (mouseY < previewWindowBottom) {
                pitch = (mouseY - (previewWindowBottom / 2F) / (previewWindowBottom / 2F));
            }*/
        }
    }

/*    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int mouseEvent) {
        if (mouseEvent != 0 || !this.tailList.func_148181_b(mouseX, mouseY, mouseEvent)) {
            super.mouseMovedOrUp(mouseX, mouseY, mouseEvent);
        }
    }*/

    @Override
    public void onGuiClosed() {
        //Delete textures on close
        for (GuiListExtended.IGuiListEntry entry : this.tailList.getEntries()) {
            TailEntry tailEntry = (TailEntry) entry;
            tailEntry.tailInfo.setTexture(null);
        }

        Tails.proxy.addTailInfo(this.mc.thePlayer.getPersistentID(), Tails.localPlayerTailInfo);
    }

    //Refreshes the text and text colour to the current set tint colour
    private void refreshTintPane() {
        this.hexText.setTextColor(this.currTintColour);

        //RGB Sliders
        Color c = new Color(this.currTintColour);
        rgbSliders[0].setValue(c.getRed() / 255F);
        rgbSliders[1].setValue(c.getGreen() / 255F);
        rgbSliders[2].setValue(c.getBlue() / 255F);

        //HSB Sliders
        float[] hsbvals = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        hsbSliders[0].setValue(hsbvals[0]);
        hsbSliders[1].setValue(hsbvals[1]);
        hsbSliders[2].setValue(hsbvals[2]);
        //The saturation slider needs to know the value of the other 2 sliders
        hsbSliders[1].setHue((float) hsbSliders[0].getValue());
        hsbSliders[1].setBrightness((float) hsbSliders[2].getValue());

        if (this.currTintEdit > 0) {
            this.rgbSliders[0].visible = this.rgbSliders[1].visible = this.rgbSliders[2].visible = true;
            this.hsbSliders[0].visible = this.hsbSliders[1].visible = this.hsbSliders[2].visible = true;
            this.tintReset.visible = true;
        }
        
        else {
            this.rgbSliders[0].visible = this.rgbSliders[1].visible = this.rgbSliders[2].visible = false;
            this.hsbSliders[0].visible = this.hsbSliders[1].visible = this.hsbSliders[2].visible = false;
            this.tintReset.visible = false;
        }

        tintReset.enabled = true;
        this.updateTailInfo();
    }

    private void drawEntity(int x, int y, int scale, float yaw, float pitch, EntityLivingBase entity) {
        float prevHeadYaw = entity.rotationYawHead;
        float prevRotYaw = entity.rotationYaw;
        float prevRotPitch = entity.rotationPitch;
        ItemStack prevItemStack = entity.getHeldItem();

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, -10F);
        GL11.glScalef(-scale, scale, scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(0.0F, entity.yOffset, 0.0F);
        GL11.glRotatef(pitch, 1F, 0.0F, 0.0F);
        GL11.glRotatef(yaw, 0F, 1F, 0.0F);

        entity.rotationYawHead = 0F;
        entity.rotationYaw = 0F;
        entity.rotationPitch = 0F;
        entity.renderYawOffset = 0F;
        entity.setSneaking(false);
        entity.setCurrentItemOrArmor(0, null);

        RenderHelper.enableStandardItemLighting();
        RenderManager.instance.playerViewY = 180.0F;
        RenderManager.instance.renderEntityWithPosYaw(entity, 0D, 0D, 0D, 0F, 1F);
        RenderHelper.disableStandardItemLighting();
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        entity.rotationYawHead = prevHeadYaw;
        entity.rotationYaw = prevRotYaw;
        entity.rotationPitch = prevRotPitch;
        entity.setCurrentItemOrArmor(0, prevItemStack);

        GL11.glPopMatrix();
    }

    private void renderTail(int x, int y, int scale, TailInfo tailInfo) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 10F);
        GL11.glScalef(-scale, scale, 1F);

        RenderHelper.enableStandardItemLighting();
        RenderManager.instance.playerViewY = 180.0F;
        ClientEventHandler.tailTypes[tailInfo.typeid].render(this.fakeEntity, tailInfo, 0);
        RenderHelper.disableStandardItemLighting();
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        GL11.glPopMatrix();
    }

    @Override
    public void onValueChangeHSBSlider(GuiHSBSlider source, double sliderValue) {
        if (source == rgbSliders[0] || source == rgbSliders[1] || source == rgbSliders[2]) {
            this.currTintColour = new Color((int) (rgbSliders[0].getValue() * 255F), (int) (rgbSliders[1].getValue() * 255F), (int) (rgbSliders[2].getValue() * 255F)).getRGB();
        }
        else {
            float[] hsbvals = { (float)hsbSliders[0].getValue(), (float)hsbSliders[1].getValue(), (float)hsbSliders[2].getValue() };
            hsbvals[source.getType().ordinal()] = (float)sliderValue;
            this.currTintColour = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]).getRGB();
        }
        this.hexText.setText(Integer.toHexString(this.currTintColour));
        this.refreshTintPane();
    }

    void updateTailInfo() {
        UUID uuid = this.mc.thePlayer.getPersistentID();
        TailEntry tailEntry = (TailEntry) this.tailList.getListEntry(this.tailList.getCurrrentIndex());

        if (this.currTintEdit > 0) this.tailInfo.tints[this.currTintEdit -1] = this.currTintColour | 0xFF << 24; //Add the alpha manually
        this.tailInfo = new TailInfo(uuid, tailEntry.tailInfo.hastail, tailEntry.tailInfo.typeid, tailEntry.tailInfo.subid, textureID, this.tailInfo.tints, null);
        this.tailInfo.setTexture(TextureHelper.generateTexture(this.tailInfo));
        this.tailInfo.needsTextureCompile = false;

        Tails.proxy.addTailInfo(uuid, this.tailInfo);
    }

    private void selectDefaultListEntry() {
        //Default selection
        for (GuiListExtended.IGuiListEntry entry : this.tailList.getEntries()) {
            TailEntry tailEntry = (TailEntry) entry;
            if (tailEntry.tailInfo.typeid == this.originalTailInfo.typeid && tailEntry.tailInfo.subid == this.originalTailInfo.subid) {
                this.tailList.setCurrrentIndex(this.tailList.getEntries().indexOf(tailEntry));
            }
        }
    }

    @Override
    public boolean onEntrySelected(GuiList guiList, int index, GuiListExtended.IGuiListEntry entry) {
        this.updateTailInfo();
        return true;
    }

    public class TailEntry implements GuiListExtended.IGuiListEntry {

        public final TailInfo tailInfo;

        public TailEntry(TailInfo tailInfo) {
            this.tailInfo = tailInfo;
        }

        @Override
        public void drawEntry(int index, int x, int y, int listWidth, int p_148279_5_, Tessellator tessellator, int mouseX, int mouseY, boolean mouseOver) {
            if (tailInfo.hastail) {
                RenderTail tail = ClientEventHandler.tailTypes[tailInfo.typeid];
                renderTail(previewWindowLeft - 25, y - 25, 50, tailInfo);
                fontRendererObj.drawString(I18n.format(tail.getUnlocalisedName(tailInfo.subid)), 5, y + (tailList.slotHeight / 2) - 5, 0xFFFFFF);
            }
            else {
                fontRendererObj.drawString(I18n.format("tail.none.name"), 5, y + (tailList.slotHeight / 2) - 5, 0xFFFFFF);
            }
        }

        @Override
        public boolean mousePressed(int index, int mouseX, int mouseY, int p_148278_4_, int mouseSlotX, int mouseSlotY) {
            return true;
        }

        @Override
        public void mouseReleased(int index, int mouseX, int mouseY, int p_148278_4_, int mouseSlotX, int mouseSlotY) {
        }
    }
}
