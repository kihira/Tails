package uk.kihira.tails.client.gui;

import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.render.RenderPart;
import uk.kihira.tails.common.PartInfo;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class TexturePanel extends Panel<GuiEditor> {
    private final int texSelectX = 17;

    private GuiButtonExt leftBtn;
    private GuiButtonExt rightBtn;

    public TexturePanel(GuiEditor parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        //Texture select
        buttonList.add(leftBtn = new GuiButtonExt(18, 5, texSelectX, 15, 15, "<"));
        buttonList.add(rightBtn = new GuiButtonExt(19, width - 20, texSelectX, 15, 15, ">"));
        parent.textureID = parent.getEditingPartInfo().textureID;

        updateButtons();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        PartInfo partInfo = parent.getEditingPartInfo();

        zLevel = -10;
        drawGradientRect(0, 0, width, height, 0xCC000000, 0xCC000000);
        zLevel = -5;
        drawGradientRect(7, texSelectX, width - 15, texSelectX + 15, 0x55000000, 0x55000000); //Use gradientRect so it actually takes into account zlevel

        //Texture select
        drawCenteredString(fontRendererObj, I18n.format("gui.texture"), width/2, texSelectX - 12, 0xFFFFFF);
        fontRendererObj.drawString(I18n.format(parent.getPartType().name().toLowerCase() + ".texture." + PartRegistry.getRenderPart(parent.getPartType(),
                partInfo.typeid).getTextureNames(partInfo.subid)[parent.textureID] + ".name"), 25, texSelectX + 4, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, p_73863_3_);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        PartInfo originalPartInfo = parent.getEditingPartInfo();
        RenderPart part = PartRegistry.getRenderPart(parent.getPartType(), originalPartInfo.typeid);
        //Texture select
        if (button.id == 18) {
            if (parent.textureID - 1 >= 0) {
                parent.textureID--;
            }
            else {
                parent.textureID = part.getTextureNames(originalPartInfo.subid).length - 1;
            }
        }
        else if (button.id == 19) {
            if (part.getTextureNames(originalPartInfo.subid).length > parent.textureID + 1) {
                parent.textureID++;
            }
            else {
                parent.textureID = 0;
            }
        }
        PartInfo partInfo = new PartInfo(true, originalPartInfo.typeid, originalPartInfo.subid, parent.textureID,
                originalPartInfo.tints, originalPartInfo.partType, null);
        parent.setPartsInfo(partInfo);
    }

    void updateButtons() {
        PartInfo originalPartInfo = parent.getEditingPartInfo();
        RenderPart part = PartRegistry.getRenderPart(parent.getPartType(), originalPartInfo.typeid);

        int texCount = part.getTextureNames(originalPartInfo.subid).length;
        leftBtn.enabled = rightBtn.enabled = texCount > 1;
    }
}
