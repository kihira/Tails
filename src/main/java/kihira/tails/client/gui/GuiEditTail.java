package kihira.tails.client.gui;

import com.google.common.base.Strings;
import kihira.tails.client.ClientEventHandler;
import kihira.tails.client.FakeEntity;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.TailInfo;
import kihira.tails.common.Tails;
import kihira.tails.common.network.TailInfoMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GuiEditTail extends GuiScreen implements ISliderCallback {

    private float yaw = 0F;
    private float pitch = 0F;

    private int currTintEdit = 0;
    private int currTintColour = 0xFFFFFF;
    private GuiTextField hexText;
    private GuiSlider rSlider;
    private GuiSlider gSlider;
    private GuiSlider bSlider;
    private GuiButton tintReset;
    private GuiButton tintSave;

    private GuiSlider rotYawSlider;

    private ScaledResolution scaledRes;

    private int previewWindowLeft;
    private int previewWindowRight;
    private int previewWindowBottom;
    private int editPaneTop;
    private TailInfo tailInfo;
    private final TailInfo originalTailInfo;

    private GuiList tailList;
    private FakeEntity fakeEntity;

    GuiButtonToggle livePreviewButton;

    public GuiEditTail() {
        //Backup original TailInfo or create default one
        TailInfo tailInfo;
        if (Tails.localPlayerTailInfo == null) {
            tailInfo = new TailInfo(Minecraft.getMinecraft().thePlayer.getPersistentID(), false, 0 , 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null);
        }
        else {
            tailInfo = Tails.localPlayerTailInfo;
        }
        this.originalTailInfo = tailInfo.deepCopy();
        this.tailInfo = this.originalTailInfo.deepCopy();

        this.fakeEntity = new FakeEntity(Minecraft.getMinecraft().theWorld);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        this.buttonList.clear();

        this.scaledRes = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int previewWindowEdgeOffset = 110;
        this.previewWindowLeft = previewWindowEdgeOffset;
        this.previewWindowRight = this.width - previewWindowEdgeOffset;
        this.previewWindowBottom = this.height - 55;
        this.editPaneTop = this.height - 125;

        //Edit tint buttons
        int topOffset = 20;
        for (int i = 2; i <= 4; i++) {
            this.buttonList.add(new GuiButton(i, this.previewWindowRight + 30, topOffset, 40, 20, "Edit"));
            topOffset += 35;
        }

        //Tint edit pane
        this.hexText = new GuiTextField(this.fontRendererObj, this.previewWindowRight + 30, this.editPaneTop + 20, 73, 10);
        this.hexText.setMaxStringLength(6);
        this.hexText.setText(Integer.toHexString(this.currTintColour));

        //RGB sliders
        this.buttonList.add(this.rSlider = new GuiSlider(this, 5, this.previewWindowRight + 5, this.editPaneTop + 35, 100, 0, 255, 0));
        this.buttonList.add(this.gSlider = new GuiSlider(this, 6, this.previewWindowRight + 5, this.editPaneTop + 55, 100, 0, 255, 0));
        this.buttonList.add(this.bSlider = new GuiSlider(this, 7, this.previewWindowRight + 5, this.editPaneTop + 75, 100, 0, 255, 0));

        //Reset/Save
        this.buttonList.add(this.tintReset = new GuiButton(8, this.previewWindowRight + 5, this.height - 25, 50, 20, "Reset"));
        this.buttonList.add(this.tintSave = new GuiButton(9, this.previewWindowRight + 55, this.height - 25, 50, 20, "Save"));

        //Tail List
        List<TailEntry> tailList = new ArrayList<TailEntry>();
        tailList.add(new TailEntry(new TailInfo(UUID.fromString("18040390-23b0-11e4-8c21-0800200c9a66"), false, 0, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null))); //No tail
        //Generate tail preview textures and add to list
        for (int type = 0; type < ClientEventHandler.tailTypes.length; type++) {
            for (int subType = 0; subType <= ClientEventHandler.tailTypes[type].getAvailableSubTypes(); subType++) {
                TailInfo tailInfo = new TailInfo(UUID.fromString("18040390-23b0-11e4-8c21-0800200c9a66"), true, type, subType, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null);
                tailList.add(new TailEntry(tailInfo));
            }
        }

        this.tailList = new GuiList(this, this.previewWindowLeft, this.height, 0, this.height, 55, tailList);
        this.selectDefaultListEntry();

        //General Editing Pane
        //Yaw Rotation
        this.buttonList.add(this.rotYawSlider = new GuiSlider(this, 1, this.previewWindowLeft + (this.scaledRes.getScaledWidth() / 80), this.previewWindowBottom + 5, this.width - (previewWindowEdgeOffset * 2) - (this.scaledRes.getScaledWidth() / 40), -180, 180, (int) this.yaw));

        //Live Preview
        String s = StatCollector.translateToLocal("gui.button.livepreview");
        this.buttonList.add(this.livePreviewButton = new GuiButtonToggle(11, this.previewWindowLeft + 5, this.height - 25, this.fontRendererObj.getStringWidth(s) + 7, 20, s,
                EnumChatFormatting.BOLD + EnumChatFormatting.DARK_RED.toString() + StatCollector.translateToLocal("gui.button.livepreview.0.tooltip"),
                StatCollector.translateToLocal("gui.button.livepreview.1.tooltip")));
        this.livePreviewButton.enabled = false;

        //Reset/Save
        this.buttonList.add(new GuiButton(12, this.previewWindowRight - 85, this.height - 25, 40, 20, "Reset"));
        this.buttonList.add(new GuiButton(13, this.previewWindowRight - 45, this.height - 25, 40, 20, "Done"));

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
            String s = "Tint " + tint;
            this.fontRendererObj.drawString(s, this.previewWindowRight+ 5, topOffset, 0xFFFFFF);
            this.drawGradientRect(this.previewWindowRight + 5, topOffset + 10, this.previewWindowRight + 25, topOffset + 30, tailInfo.tints[tint - 1], tailInfo.tints[tint - 1]);
            topOffset += 35;
        }

        //Editing tint pane
        if (this.currTintEdit > 0) {
            this.drawHorizontalLine(this.previewWindowRight, this.width, this.editPaneTop, 0xFF000000);
            this.fontRendererObj.drawString("Editing Tint " + this.currTintEdit, this.previewWindowRight + 5, this.editPaneTop + 5, 0xFFFFFF);

            this.fontRendererObj.drawString("Hex:", this.previewWindowRight + 5, this.editPaneTop + 21, 0xFFFFFF);
            this.hexText.drawTextBox();
        }

        //Player
        drawEntity(this.width / 2, (this.previewWindowBottom / 2) + (this.scaledRes.getScaledHeight() / 4), this.scaledRes.getScaledHeight() / 4, this.yaw, this.pitch, this.mc.thePlayer);

        this.zLevel = 0;
        //Tails list
        this.tailList.drawScreen(mouseX, mouseY, p_73863_3_);

        super.drawScreen(mouseX, mouseY, p_73863_3_);

        //Tooltips
        for (Object obj : this.buttonList) {
            if (obj instanceof GuiButtonToggle && ((GuiButtonToggle) obj).func_146115_a()) ((GuiButtonToggle) obj).func_146111_b(mouseX, mouseY);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        //Yaw Rotation
        if (button.id == 1) MathHelper.clamp_float(this.yaw = this.rotYawSlider.currentValue, -180F, 180F);
        //Edit buttons
        else if (button.id >= 2 && button.id <= 4) {
            this.currTintEdit = button.id - 1;
            this.currTintColour = this.tailInfo.tints[this.currTintEdit - 1] & 0xFFFFFF; //Ignore the alpha bits
            this.hexText.setText(Integer.toHexString(this.currTintColour));
            this.refreshTintPane();
        }
        //Reset Tint
        else if (button.id == 8) {
            this.currTintColour = this.originalTailInfo.tints[this.currTintEdit - 1] & 0xFFFFFF; //Ignore the alpha bits
            this.hexText.setText(Integer.toHexString(this.currTintColour));
            this.refreshTintPane();
        }
        //Save Tint
        else if (button.id == 9) {
            this.tailInfo.tints[this.currTintEdit -1] = this.currTintColour | 0xFF << 24; //Add the alpha manually
            this.updateTailInfo();
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
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.hexText.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
    }

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

        this.rSlider.setCurrentValue(this.currTintColour >> 16 & 255);
        this.rSlider.packedFGColour = (255 | this.rSlider.currentValue) << 16;
        this.gSlider.setCurrentValue(this.currTintColour >> 8 & 255);
        this.gSlider.packedFGColour = (255 | this.gSlider.currentValue) << 8;
        this.bSlider.setCurrentValue(this.currTintColour & 255);
        this.bSlider.packedFGColour = (255 | this.bSlider.currentValue);

        if (this.currTintEdit > 0) {
            this.rSlider.visible = this.gSlider.visible = this.bSlider.visible = this.tintReset.visible = this.tintSave.visible = true;
        }
        else {
            this.rSlider.visible = this.gSlider.visible = this.bSlider.visible = this.tintReset.visible = this.tintSave.visible = false;
        }

        if (!this.livePreviewButton.enabled) {
            this.updateTailInfo();
        }
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
    public boolean onValueChange(GuiSlider slider, int oldValue, int newValue) {
        if (slider == this.rotYawSlider) {
            this.yaw = newValue;
        }
        //RGB sliders
        if (slider == this.rSlider || slider == this.gSlider || slider == this.bSlider) {
            int colour = 0;
            colour = colour | this.rSlider.currentValue << 16;
            colour = colour | this.gSlider.currentValue << 8;
            colour = colour | this.bSlider.currentValue;
            this.currTintColour = colour;
            this.hexText.setText(Integer.toHexString(this.currTintColour));
            this.refreshTintPane();
        }
        return true;
    }

    void updateTailInfo() {
        UUID uuid = this.mc.thePlayer.getPersistentID();
        TailEntry tailEntry = (TailEntry) this.tailList.getListEntry(this.tailList.getCurrrentIndex());

        if (this.currTintEdit > 0) this.tailInfo.tints[this.currTintEdit -1] = this.currTintColour | 0xFF << 24; //Add the alpha manually
        this.tailInfo = new TailInfo(uuid, tailEntry.tailInfo.hastail, tailEntry.tailInfo.typeid, tailEntry.tailInfo.subid, this.tailInfo.tints, null);
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

    public class TailEntry implements GuiListExtended.IGuiListEntry {

        public final TailInfo tailInfo;

        public TailEntry(TailInfo tailInfo) {
            this.tailInfo = tailInfo;
        }

        @Override
        public void drawEntry(int p_148279_1_, int x, int y, int listWidth, int p_148279_5_, Tessellator tessellator, int p_148279_7_, int p_148279_8_, boolean p_148279_9_) {
            if (this.tailInfo.hastail) {
                renderTail(previewWindowLeft - 25, y - 25, 50, this.tailInfo);
                fontRendererObj.drawString(StatCollector.translateToLocal(ClientEventHandler.tailTypes[tailInfo.typeid].getUnlocalisedName(tailInfo.subid)), 5, y + (tailList.slotHeight / 2) - 5, 0xFFFFFF);

            }
            else {
                fontRendererObj.drawString(StatCollector.translateToLocal("tail.none.name"), 5, y + (tailList.slotHeight / 2) - 5, 0xFFFFFF);
            }
        }

        @Override
        public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
            return true;
        }

        @Override
        public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_) {

        }
    }

    public class GuiButtonToggle extends GuiButton {

        private final String[] tooltips;

        public GuiButtonToggle(int id, int x, int y, int width, int height, String text, String ... tooltips) {
            super(id, x, y, width, height, text);
            this.tooltips = tooltips;
        }

        @Override
        public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
            if (this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height) {
                this.enabled = !this.enabled;
                return true;
            }
            return false;
        }

        @Override
        public void func_146111_b(int x, int y) {
            if (this.tooltips != null && this.tooltips.length > 0) {
                List<String> list = new ArrayList<String>();
                Collections.addAll(list, this.tooltips);
                list.add((!this.enabled ? EnumChatFormatting.GREEN + EnumChatFormatting.ITALIC.toString() + "Enabled" : EnumChatFormatting.RED + EnumChatFormatting.ITALIC.toString() + "Disabled"));
                func_146283_a(list, x, y);
            }
        }
    }
}
