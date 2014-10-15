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
import kihira.tails.client.FakeEntity;
import kihira.tails.client.PartRegistry;
import kihira.tails.client.gui.controls.GuiHSBSlider;
import kihira.tails.client.gui.controls.GuiHSBSlider.HSBSliderType;
import kihira.tails.client.gui.controls.GuiHSBSlider.IHSBSliderCallback;
import kihira.tails.client.render.RenderPart;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.PartInfo;
import kihira.tails.common.PartsData;
import kihira.tails.common.Tails;
import kihira.tails.common.network.PlayerDataMessage;
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
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuiEditor extends GuiBaseScreen implements IListCallback, IHSBSliderCallback {

    private float yaw = 0F;
    private float pitch = 10F;
    private int prevMouseX = -1;

    private int currTintEdit = 0;
    private int currTintColour = 0xFFFFFF;
    private GuiTextField hexText;
    private GuiHSBSlider[] hsbSliders;
    private GuiHSBSlider[] rgbSliders;
    private GuiIconButton tintReset;
    private GuiIconButton colourPicker;
    private int textureID;
    private IntBuffer pixelBuffer;
    private boolean selectingColour = false;

    private ScaledResolution scaledRes;

    private int previewWindowLeft;
    private int previewWindowRight;
    private int previewWindowBottom;
    private int editPaneTop;

    private PartsData.PartType partType;
    private PartsData partsData;
    private PartInfo partInfo;
    private PartInfo originalPartInfo;

    private GuiButton partTypeButton;
    private GuiList partList;
    private FakeEntity fakeEntity;

    public GuiEditor() {
        //Backup original PartInfo or create default one
        PartInfo partInfo;
        if (Tails.localPartsData == null) {
            Tails.setLocalPartsData(new PartsData(Minecraft.getMinecraft().thePlayer.getPersistentID()));
        }

        //Default to Tail
        partType = PartsData.PartType.TAIL;
        if (!Tails.localPartsData.hasPartInfo(partType)) {
            Tails.localPartsData.setPartInfo(partType, new PartInfo(Minecraft.getMinecraft().thePlayer.getPersistentID(), false, 0, 0, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null, partType));
        }
        partInfo = Tails.localPartsData.getPartInfo(partType);

        originalPartInfo = partInfo.deepCopy();
        partsData = Tails.localPartsData.deepCopy();
        this.partInfo = originalPartInfo.deepCopy();

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
        rgbSliders[0] = new GuiHSBSlider(5, this.previewWindowRight + 5, this.editPaneTop + 70, 100, 10, this, HSBSliderType.SATURATION);
        rgbSliders[1] = new GuiHSBSlider(6, this.previewWindowRight + 5, this.editPaneTop + 80, 100, 10, this, HSBSliderType.SATURATION);
        rgbSliders[2] = new GuiHSBSlider(7, this.previewWindowRight + 5, this.editPaneTop + 90, 100, 10, this, HSBSliderType.SATURATION);
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

        //General Editing Pane
        //Reset/Save
        this.buttonList.add(new GuiButton(12, this.previewWindowRight - 83, this.height - 25, 40, 20, I18n.format("gui.button.reset")));
        this.buttonList.add(new GuiButton(13, this.previewWindowRight - 43, this.height - 25, 40, 20, I18n.format("gui.done")));

        //Export
        this.buttonList.add(new GuiButtonTooltip(14, (this.width / 2) - 20, this.height - 25, 40, 20, I18n.format("gui.button.export"),
                this.scaledRes.getScaledWidth() / 3, I18n.format("gui.button.export.0.tooltip")));

        //Texture select
        this.buttonList.add(new GuiButton(18, 5, this.height - 25, 15, 20, "<"));
        this.buttonList.add(new GuiButton(19, this.previewWindowLeft - 20, this.height - 25, 15, 20, ">"));
        textureID = partInfo.textureID;

        //PartType Select
        buttonList.add(partTypeButton = new GuiButton(20, previewWindowLeft + 3, height - 25, 40, 20, partType.name()));

        //Colour Picker
        buttonList.add(colourPicker = new GuiIconButton(21, this.width - 36, this.editPaneTop + 1, GuiIconButton.Icons.EYEDROPPER, new ArrayList<String>()));
        colourPicker.visible = false;

        //Reset Camera
        buttonList.add(new GuiIconButton(22, previewWindowRight - 18, 22, GuiIconButton.Icons.UNDO, new ArrayList<String>() {{ add(I18n.format("gui.button.reset.camera")); }}));

        //Help
        this.buttonList.add(new GuiIconButton(500, this.previewWindowRight - 18, 4, GuiIconButton.Icons.QUESTION, new ArrayList<String>() {{
            add(I18n.format("gui.button.help.camera.0"));
            add(I18n.format("gui.button.help.camera.1"));
        }}));

        initPartList();
        refreshTintPane();
    }

    private void initPartList() {
        //Part List
        java.util.List<PartEntry> partList = new ArrayList<PartEntry>();
        UUID uuid = UUID.fromString("18040390-23b0-11e4-8c21-0800200c9a66"); //Just a random UUID
        partList.add(new PartEntry(new PartInfo(uuid, false, 0, 0, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null, partType))); //No tail
        //Generate tail preview textures and add to list
        List<RenderPart> parts = PartRegistry.getParts(partType);
        for (int type = 0; type < parts.size(); type++) {
            for (int subType = 0; subType <= parts.get(type).getAvailableSubTypes(); subType++) {
                PartInfo partInfo = new PartInfo(uuid, true, type, subType, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null, partType);
                partList.add(new PartEntry(partInfo));
            }
        }

        this.partList = new GuiList(this, this.previewWindowLeft, this.height - 43, 0, this.height - 43, 55, partList);
        this.selectDefaultListEntry();
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
            this.drawGradientRect(this.previewWindowRight + 5, topOffset + 10, this.previewWindowRight + 25, topOffset + 30, partInfo.tints[tint - 1], partInfo.tints[tint - 1]);
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
        this.partList.drawScreen(mouseX, mouseY, p_73863_3_);

        //Texture select
        fontRendererObj.drawString(I18n.format("gui.texture") + ":", 7, this.height - 37, 0xFFFFFF);
        fontRendererObj.drawString(I18n.format(partType.name().toLowerCase() + ".texture." + PartRegistry.getRenderPart(partType, partInfo.typeid).getTextureNames(partInfo.subid)[textureID] + ".name"), 25, this.height - 19, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, p_73863_3_);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        RenderPart part = PartRegistry.getRenderPart(partType, partInfo.typeid);
        //Edit buttons
        if (button.id >= 2 && button.id <= 4) {
            this.currTintEdit = button.id - 1;
            this.currTintColour = this.partInfo.tints[this.currTintEdit - 1] & 0xFFFFFF; //Ignore the alpha bits
            this.hexText.setText(Integer.toHexString(this.currTintColour));
            this.refreshTintPane();
            this.tintReset.enabled = false;
            colourPicker.enabled = true;
        }
        //Reset Tint
        else if (button.id == 8) {
            this.currTintColour = this.originalPartInfo.tints[this.currTintEdit - 1] & 0xFFFFFF; //Ignore the alpha bits
            this.hexText.setText(Integer.toHexString(this.currTintColour));
            this.refreshTintPane();
            tintReset.enabled = false;
        }
        //Reset All
        else if (button.id == 12) {
            this.partInfo = this.originalPartInfo.deepCopy();
            this.selectDefaultListEntry();
            this.currTintEdit = 0;
            this.refreshTintPane();
            this.updatePartsData();
        }
        //Save All
        else if (button.id == 13) {
            //Update part info, set local and send it to the server
            this.updatePartsData();
            Tails.setLocalPartsData(partsData);
            Tails.proxy.addPartsData(partsData.uuid, partsData);
            Tails.networkWrapper.sendToServer(new PlayerDataMessage(partsData, false));

            this.mc.displayGuiScreen(null);
        }
        //Export
        else if (button.id == 14) {
            this.updatePartsData();
            this.mc.displayGuiScreen(new GuiExport(this, this.partInfo));
        }
        //Texture select
        else if (button.id == 18) {
            if (textureID - 1 >= 0) {
                textureID--;
            }
            else {
                textureID = part.getTextureNames(partInfo.subid).length - 1;
            }
            updatePartsData();
        }
        else if (button.id == 19) {
            if (part.getTextureNames(partInfo.subid).length > textureID + 1) {
                textureID++;
            }
            else {
                textureID = 0;
            }
            updatePartsData();
        }
        //PartType
        else if (button.id == 20) {
            if (partType.ordinal() + 1 >= PartsData.PartType.values().length) {
                partType = PartsData.PartType.values()[0];
            }
            else {
                partType = PartsData.PartType.values()[partType.ordinal() + 1];
            }

            PartInfo newPartInfo = partsData.getPartInfo(partType);
            if (newPartInfo == null) {
                newPartInfo = new PartInfo(partsData.uuid, false, 0, 0, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null, partType);
            }
            originalPartInfo = newPartInfo.deepCopy();
            partInfo = originalPartInfo.deepCopy();

            partTypeButton.displayString = partType.name();
            initPartList();
            refreshTintPane();
        }
        //Colour Picker
        else if (button.id == 21) {
            setSelectingColour(true);
        }
        //Reset Camera
        else if (button.id == 22) {
            yaw = 0;
            pitch = 10F;
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

        if (keyCode == Keyboard.KEY_ESCAPE && selectingColour) {
            setSelectingColour(false);
        }
        else {
            super.keyTyped(letter, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseEvent) {
/*        if (mouseEvent != 0 || !this.partList.func_148179_a(mouseX, mouseY, mouseEvent)) {
            super.mouseClicked(mouseX, mouseY, mouseEvent);
        }*/
        if (selectingColour && mouseEvent == 0) {
            currTintColour = getColourAtPoint(Mouse.getEventX(), mc.displayHeight - Mouse.getEventY()) & 0xFFFFFF; //Ignore alpha
            setSelectingColour(false);
            refreshTintPane();
        }
        else {
            super.mouseClicked(mouseX, mouseY, mouseEvent);
            this.hexText.mouseClicked(mouseX, mouseY, mouseEvent);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        if (lastButtonClicked == 0 && mouseY < previewWindowBottom && mouseX > previewWindowLeft && mouseX < previewWindowRight) {
            //Yaw
            if (prevMouseX == -1) prevMouseX = mouseX;
            else {
                yaw += (mouseX - prevMouseX) * 1.5F;
                prevMouseX = mouseX;
            }
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int mouseEvent) {
        prevMouseX = -1;
        super.mouseMovedOrUp(mouseX, mouseY, mouseEvent);
    }

    @Override
    public void onGuiClosed() {
        //Delete textures on close
        for (GuiListExtended.IGuiListEntry entry : this.partList.getEntries()) {
            PartEntry tailEntry = (PartEntry) entry;
            tailEntry.partInfo.setTexture(null);
        }

        Tails.proxy.addPartsData(this.mc.thePlayer.getPersistentID(), Tails.localPartsData);
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
            colourPicker.visible = true;
        }
        
        else {
            this.rgbSliders[0].visible = this.rgbSliders[1].visible = this.rgbSliders[2].visible = false;
            this.hsbSliders[0].visible = this.hsbSliders[1].visible = this.hsbSliders[2].visible = false;
            this.tintReset.visible = false;
            colourPicker.visible = false;
        }

        tintReset.enabled = true;
        this.updatePartsData();
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

    private void renderPart(int x, int y, int scale, PartInfo partInfo) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 10F);
        GL11.glScalef(-scale, scale, 1F);

        RenderHelper.enableStandardItemLighting();
        RenderManager.instance.playerViewY = 180.0F;
        PartRegistry.getRenderPart(partInfo.partType, partInfo.typeid).render(this.fakeEntity, partInfo, 0, 0, 0, 0);
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

    private void updatePartsData() {
        UUID uuid = this.mc.thePlayer.getPersistentID();
        PartEntry tailEntry = (PartEntry) partList.getListEntry(partList.getCurrrentIndex());

        partInfo.setTexture(null);
        if (currTintEdit > 0) partInfo.tints[currTintEdit -1] = currTintColour | 0xFF << 24; //Add the alpha manually
        partInfo = new PartInfo(uuid, tailEntry.partInfo.hasPart, tailEntry.partInfo.typeid, tailEntry.partInfo.subid, textureID, partInfo.tints, partType, null);
        if (partInfo.hasPart) partInfo.setTexture(TextureHelper.generateTexture(partInfo));

        partsData.setPartInfo(partType, partInfo);

        Tails.proxy.addPartsData(uuid, partsData);
    }

    private void selectDefaultListEntry() {
        //Default selection
        for (GuiListExtended.IGuiListEntry entry : this.partList.getEntries()) {
            PartEntry partEntry = (PartEntry) entry;
            if ((!partEntry.partInfo.hasPart && !partInfo.hasPart) || (partInfo.hasPart && partEntry.partInfo.hasPart && partEntry.partInfo.typeid == partInfo.typeid && partEntry.partInfo.subid == partInfo.subid)) {
                this.partList.setCurrrentIndex(this.partList.getEntries().indexOf(partEntry));
                break;
            }
        }
    }

    private int getColourAtPoint(int x, int y) {
        int[] pixelData;
        int pixels = 1;

        if (pixelBuffer == null) {
            pixelBuffer = BufferUtils.createIntBuffer(pixels);
        }
        pixelData = new int[pixels];

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        pixelBuffer.clear();

        GL11.glReadPixels(x, mc.displayHeight - y, 1, 1, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);

        pixelBuffer.get(pixelData);
        return pixelData[0];
    }

    private void setSelectingColour(boolean selectingColour) {
        this.selectingColour = selectingColour;

        if (selectingColour) {
            try {
                BufferedImage bufferedImage = ImageIO.read(mc.getResourceManager().getResource(GuiIconButton.iconsTextures).getInputStream());
                int[] pixelData;
                int pixels = 16 * 16;
                pixelData = new int[pixels];
                IntBuffer buffer = IntBuffer.wrap(bufferedImage.getRGB(GuiIconButton.Icons.EYEDROPPER.u, GuiIconButton.Icons.EYEDROPPER.v + 16, 16, 16, pixelData, 0, 16));
                org.lwjgl.input.Cursor cursor = new org.lwjgl.input.Cursor(16, 16, 0, 15, 1, buffer, null);

                Mouse.setNativeCursor(cursor);

            } catch (LWJGLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                Mouse.setNativeCursor(null);
            } catch (LWJGLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onEntrySelected(GuiList guiList, int index, GuiListExtended.IGuiListEntry entry) {
        //Reset texture ID
        textureID = 0;
        this.updatePartsData();
        return true;
    }

    public class PartEntry implements GuiListExtended.IGuiListEntry {

        public final PartInfo partInfo;

        public PartEntry(PartInfo partInfo) {
            this.partInfo = partInfo;
        }

        @Override
        public void drawEntry(int index, int x, int y, int listWidth, int p_148279_5_, Tessellator tessellator, int mouseX, int mouseY, boolean mouseOver) {
            if (partInfo.hasPart) {
                renderPart(previewWindowLeft - 25, y - 25, 50, partInfo);
                fontRendererObj.drawString(I18n.format(PartRegistry.getRenderPart(partInfo.partType, partInfo.typeid)
                        .getUnlocalisedName(partInfo.subid)), 5, y + (partList.slotHeight / 2) - 5, 0xFFFFFF);
            }
            else {
                fontRendererObj.drawString(I18n.format("tail.none.name"), 5, y + (partList.slotHeight / 2) - 5, 0xFFFFFF);
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
