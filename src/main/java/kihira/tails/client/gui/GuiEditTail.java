package kihira.tails.client.gui;

import com.google.common.base.Strings;
import kihira.tails.client.ClientEventHandler;
import kihira.tails.client.FakeEntity;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.TailInfo;
import kihira.tails.common.Tails;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
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

    private GuiSlider rotYawSlider;

    private ScaledResolution scaledRes;

    private int previewWindowEdgeOffset = 100;
    private int previewWindowLeft;
    private int previewWindowRight;
    private int editPaneTop;
    private TailInfo tailInfo;

    private GuiList tailList;
    private FakeEntity fakeEntity;

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        this.buttonList.clear();

        this.scaledRes = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        //this.previewWindowEdgeOffset = this.scaledRes.getScaledWidth() / 5;
        this.previewWindowEdgeOffset = 120;
        this.previewWindowLeft = this.previewWindowEdgeOffset;
        this.previewWindowRight = this.width - this.previewWindowEdgeOffset;
        this.editPaneTop = this.height - 125;
        this.tailInfo = Tails.proxy.getTailInfo(this.mc.thePlayer.getPersistentID());

        //Yaw Rotation
        this.buttonList.add(this.rotYawSlider = new GuiSlider(this, 1, this.previewWindowLeft + 25, this.height - 30, this.width - (this.previewWindowEdgeOffset * 2) - 50, -180, 180, (int) this.yaw));
/*
        this.buttonList.add(new GuiButton(0, this.previewWindowRight - 20, this.height - 50, 20, 20, ">"));
        this.buttonList.add(new GuiButton(1, this.previewWindowLeft, this.height - 50, 20, 20, "<"));
*/

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
        this.buttonList.add(new GuiButton(8, this.previewWindowRight + 5, this.height - 25, 50, 20, "Reset"));
        this.buttonList.add(new GuiButton(9, this.previewWindowRight + 55, this.height - 25, 50, 20, "Save"));

        //Tail List
        List<GuiListExtended.IGuiListEntry> tailList = new ArrayList<GuiListExtended.IGuiListEntry>();
        //Generate tail preview textures and add to list TODO release textures
        for (int type = 0; type < ClientEventHandler.tailTypes.length; type++) {
            for (int subType = 0; subType <= ClientEventHandler.tailTypes[type].getAvailableSubTypes(); subType++) {
                TailInfo tailInfo = new TailInfo(UUID.fromString("18040390-23b0-11e4-8c21-0800200c9a66"), true, type, subType, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null);
                tailInfo.setTexture(TextureHelper.generateTexture(tailInfo));
                tailInfo.needsTextureCompile = false;
                tailList.add(new TailEntry(tailInfo));
            }
        }

        this.tailList = new GuiList(this.mc, this.previewWindowLeft, this.height, 0, this.height, 0, 55, tailList);
        this.fakeEntity = new FakeEntity(this.mc.theWorld);

        this.refreshTintPane();
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        //Background
        this.zLevel = -1000;
        this.drawGradientRect(0, 0, this.previewWindowLeft, this.height, 0xCC000000, 0xCC000000);
        this.drawGradientRect(this.previewWindowLeft, 0, this.previewWindowRight, this.height, 0xEE000000, 0xEE000000); //Hex with alpha in the format ARGB
        this.drawGradientRect(this.previewWindowRight, 0, this.width, this.height, 0xCC000000, 0xCC000000);

        //Tints
        int topOffset = 10;
        for (int tint = 1; tint <= 3; tint++) {
            String s = "Tint " + tint;
            this.fontRendererObj.drawString(s, this.previewWindowRight+ 5, topOffset, 0xFFFFFF);
            this.drawGradientRect(this.previewWindowRight + 5, topOffset + 10, this.previewWindowRight + 25, topOffset + 30, tailInfo.tints[tint - 1], tailInfo.tints[tint - 1]);
            topOffset += 35;
        }

        //Editting tint pane
        if (this.currTintEdit > 0) {
            this.drawHorizontalLine(this.previewWindowRight, this.width, this.editPaneTop, 0xFF000000);
            this.fontRendererObj.drawString("Editing Tint " + this.currTintEdit, this.previewWindowRight + 5, this.editPaneTop + 5, 0xFFFFFF);

            this.fontRendererObj.drawString("Hex:", this.previewWindowRight + 5, this.editPaneTop + 21, 0xFFFFFF);
            this.hexText.drawTextBox();
        }
/*        topOffset = 0;
        GL11.glColor3f(1F, 1F, 1F); //Reset colour as something above seems to set it to something else
        for (int type = 0; type < ClientEventHandler.tailTypes.length; type++) {
            for (int subType = 0; subType <= ClientEventHandler.tailTypes[type].getAvailableSubTypes(); subType++) {
                TailInfo tailInfo = new TailInfo(UUID.fromString("18040390-23b0-11e4-8c21-0800200c9a66"), true, type, subType, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null);
                tailInfo.setTexture(new ResourceLocation("tails_"+tailInfo.id.toString()+"_"+tailInfo.typeid+"_"+tailInfo.subid+"_"+tailInfo.textureID));
                tailInfo.needsTextureCompile = false;
                this.renderTail(this.previewWindowLeft - (this.scaledRes.getScaledHeight() / 16), topOffset - 15, (this.scaledRes.getScaledHeight() / 8), tailInfo);
                this.fontRendererObj.drawString(StatCollector.translateToLocal(ClientEventHandler.tailTypes[type].getUnlocalisedName(subType)), 5, topOffset + (this.scaledRes.getScaledHeight() / 18), 0xFFFFFF);
                topOffset += (this.scaledRes.getScaledHeight() / 8);
            }
        }*/

        //Player
        drawEntity(this.width / 2, (this.height / 2) + (this.scaledRes.getScaledHeight() / 4), this.scaledRes.getScaledHeight() / 4, this.yaw, this.pitch, this.mc.thePlayer);

        //Tails list
        this.tailList.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);

        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
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

        //Reset
        else if (button.id == 8) {
            this.currTintColour = this.tailInfo.tints[this.currTintEdit - 1] & 0xFFFFFF; //Ignore the alpha bits
            this.hexText.setText(Integer.toHexString(this.currTintColour));
            this.refreshTintPane();
        }
        //Save
        else if (button.id == 9) {
            this.tailInfo.tints[this.currTintEdit -1] = this.currTintColour | 0xFF << 24; //Add the alpha manually
        }
    }

    @Override
    protected void keyTyped(char letter, int keyCode) {
        super.keyTyped(letter, keyCode);
        this.hexText.textboxKeyTyped(letter, keyCode);

        try {
            //Gets the current colour from the hex text
            if (!Strings.isNullOrEmpty(this.hexText.getText())) {
                this.currTintColour = Integer.parseInt(this.hexText.getText(), 16);
            }
        } catch (NumberFormatException ignored) {}

        this.refreshTintPane();
    }

    @Override
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.hexText.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
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
            this.rSlider.visible = this.gSlider.visible = this.bSlider.visible = true;
        }
        else {
            this.rSlider.visible = this.gSlider.visible = this.bSlider.visible = false;
        }
    }

    private void drawEntity(int x, int y, int scale, float yaw, float pitch, EntityLivingBase entity) {
        float prevHeadYaw = entity.rotationYawHead;
        float prevRotYaw = entity.rotationYaw;
        float prevRotPitch = entity.rotationPitch;
        ItemStack prevItemStack = entity.getHeldItem();

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 100.0F);
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

        entity.rotationYawHead = prevHeadYaw;
        entity.rotationYaw = prevRotYaw;
        entity.rotationPitch = prevRotPitch;
        entity.setCurrentItemOrArmor(0, prevItemStack);

        GL11.glPopMatrix();
    }

    private void renderTail(int x, int y, int scale, TailInfo tailInfo) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 100.0F);
        GL11.glScalef(-scale, scale, scale);

        RenderHelper.enableStandardItemLighting();
        RenderManager.instance.playerViewY = 180.0F;
        ClientEventHandler.tailTypes[tailInfo.typeid].render(this.fakeEntity, tailInfo);
        RenderHelper.disableStandardItemLighting();

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

    public class TailEntry implements GuiListExtended.IGuiListEntry {

        private final TailInfo tailInfo;

        public TailEntry(TailInfo tailInfo) {
            this.tailInfo = tailInfo;
        }

        @Override
        public void drawEntry(int p_148279_1_, int x, int y, int listWidth, int p_148279_5_, Tessellator tessellator, int p_148279_7_, int p_148279_8_, boolean p_148279_9_) {
            renderTail(previewWindowLeft - 25, y - 25, 50, this.tailInfo);
            fontRendererObj.drawString(StatCollector.translateToLocal(ClientEventHandler.tailTypes[tailInfo.typeid].getUnlocalisedName(tailInfo.subid)), 5, y + (tailList.slotHeight / 2) - 5, 0xFFFFFF);
        }

        @Override
        public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
            return true;
        }

        @Override
        public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_) {

        }
    }
}
