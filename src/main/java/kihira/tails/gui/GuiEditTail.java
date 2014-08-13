package kihira.tails.gui;

import com.google.common.base.Strings;
import kihira.tails.EventHandler;
import kihira.tails.TailInfo;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class GuiEditTail extends GuiScreen {

    private float yaw = 0F;
    private float pitch = 0F;

    private int currTintEdit = 0;
    private int currTintColour;
    private GuiTextField hexText;
    private GuiTextField rText;
    private GuiTextField gText;
    private GuiTextField bText;

    private ScaledResolution scaledRes;

    private int previewWindowEdgeOffset = 100;
    private int previewWindowLeft;
    private int previewWindowRight;
    private int editPaneTop;
    private TailInfo tailInfo;

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        this.buttonList.clear();

        this.scaledRes = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        //this.previewWindowEdgeOffset = this.scaledRes.getScaledWidth() / 5;
        this.previewWindowEdgeOffset = 100;
        this.previewWindowLeft = this.previewWindowEdgeOffset;
        this.previewWindowRight = this.width - this.previewWindowEdgeOffset;
        this.editPaneTop = this.height - 125;
        this.tailInfo = EventHandler.TailMap.get(this.mc.thePlayer.getGameProfile().getId());

        //Yaw Rotation
        this.buttonList.add(0, new GuiButton(0, this.previewWindowRight - 20, this.height - 50, 20, 20, ">"));
        this.buttonList.add(1, new GuiButton(1, this.previewWindowLeft, this.height - 50, 20, 20, "<"));

        //Edit tint buttons
        int topOffset = 20;
        for (int i = 2; i <= 4; i++) {
            this.buttonList.add(i, new GuiButton(i, this.previewWindowRight + 30, topOffset, 40, 20, "Edit"));
            topOffset += 35;
        }

        //Tint edit pane
        this.hexText = new GuiTextField(this.fontRendererObj, this.previewWindowRight + 5, this.editPaneTop + 20, 70, 10);
        this.rText = new GuiTextField(this.fontRendererObj, this.previewWindowRight + 5, this.editPaneTop + 40, 26, 10);
        this.gText = new GuiTextField(this.fontRendererObj, this.previewWindowRight + 36, this.editPaneTop + 40, 26, 10);
        this.bText = new GuiTextField(this.fontRendererObj, this.previewWindowRight + 67, this.editPaneTop + 40, 26, 10);

        this.hexText.setMaxStringLength(6);
        this.rText.setMaxStringLength(3);
        this.gText.setMaxStringLength(3);
        this.bText.setMaxStringLength(3);
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
        this.currTintEdit = 1;
        if (this.currTintEdit > 0) {
            this.drawHorizontalLine(this.previewWindowRight, this.width, editPaneTop, 0xFF000000);
            this.fontRendererObj.drawString("Editting Tint " + this.currTintEdit, this.previewWindowRight + 5, editPaneTop + 5, 0xFFFFFF);

            this.hexText.drawTextBox();
            this.rText.drawTextBox();
            this.gText.drawTextBox();
            this.bText.drawTextBox();
        }

        //Player
        drawPlayer(this.width / 2, (this.height / 2) + (this.scaledRes.getScaledHeight() / 5), (this.scaledRes.getScaledHeight() / 5), this.yaw, this.pitch, this.mc.thePlayer);

        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        //Yaw Rotation
        if (button.id == 0) MathHelper.clamp_float(this.yaw += 5F, -360F, 360F);
        if (button.id == 1) MathHelper.clamp_float(this.yaw -= 5F, -360F, 360F);

        //Edit buttons
        if (button.id >= 2 && button.id <= 4) {
            this.currTintEdit = button.id - 1;
            this.currTintColour = this.tailInfo.tints[this.currTintEdit - 1] & 0xFFFFFF; //Ignore the alpha bits
            this.hexText.setText(Integer.toHexString(this.currTintColour));
            this.refreshTextBoxes();
        }
    }

    @Override
    protected void keyTyped(char letter, int keyCode) {
        super.keyTyped(letter, keyCode);
        this.hexText.textboxKeyTyped(letter, keyCode);
        this.rText.textboxKeyTyped(letter, keyCode);
        this.gText.textboxKeyTyped(letter, keyCode);
        this.bText.textboxKeyTyped(letter, keyCode);

        try {
            //Gets the current colour from the hex text
            if (!Strings.isNullOrEmpty(this.hexText.getText())) {
                this.currTintColour = Integer.parseInt(this.hexText.getText(), 16);
            }
        } catch (NumberFormatException ignored) {
            ignored.printStackTrace();
        }

        this.refreshTextBoxes();
    }

    @Override
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.hexText.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.rText.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.gText.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.bText.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    //Refreshes the text and text colour to the current set tint colour
    private void refreshTextBoxes() {
        this.hexText.setTextColor(this.currTintColour);

        int colourValue = this.currTintColour >> 16 & 255;
        this.rText.setText(String.valueOf(colourValue));
        this.rText.setTextColor(colourValue << 16);

        colourValue = this.currTintColour >> 8 & 255;
        this.gText.setText(String.valueOf(colourValue));
        this.gText.setTextColor(colourValue << 8);

        colourValue = this.currTintColour & 255;
        this.bText.setText(String.valueOf(colourValue));
        this.bText.setTextColor(colourValue);
    }

    public void drawPlayer(int x, int y, int scale, float yaw, float pitch, EntityLivingBase entity) {
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
}
