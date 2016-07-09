package kihira.tails.client.gui;

import kihira.tails.client.PartRegistry;
import kihira.tails.client.render.RenderPart;
import kihira.tails.common.PartInfo;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.io.IOException;

public class TexturePanel extends Panel<GuiEditor> {

    private final int texSelectX = 17;

    public TexturePanel(GuiEditor parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        //Texture select
        buttonList.add(new GuiButtonExt(18, 5, texSelectX, 15, 15, "<"));
        buttonList.add(new GuiButtonExt(19, width - 20, texSelectX, 15, 15, ">"));
        parent.textureID = parent.getEditingPartInfo().textureID;
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        PartInfo partInfo = parent.getEditingPartInfo();
        RenderPart renderPart = PartRegistry.getRenderPart(parent.getPartType(), partInfo.typeid);

        zLevel = -10;
        drawGradientRect(0, 0, width, height, 0xCC000000, 0xCC000000);
        zLevel = -5;
        drawGradientRect(7, texSelectX, width - 15, texSelectX + 15, 0x55000000, 0x55000000); //Use gradientRect so it actually takes into account zlevel

        //Texture select
        drawCenteredString(fontRendererObj, I18n.format("gui.texture"), width/2, texSelectX - 12, 0xFFFFFF);
        fontRendererObj.drawString(I18n.format(parent.getPartType().name().toLowerCase() + ".texture." + PartRegistry.getRenderPart(parent.getPartType(),
                partInfo.typeid).getTextureNames(partInfo.subid)[parent.textureID] + ".name"), 25, texSelectX + 4, 0xFFFFFF);

//        if (renderPart.hasAuthor(partInfo.subid, partInfo.textureID)) {
//            //Yeah its not nice but eh, works
//            GL11.glPushMatrix();
//            GL11.glTranslatef(7, height - 10, 0);
//            GL11.glScalef(0.6F, 0.6F, 1F);
//            fontRendererObj.drawString(I18n.format("gui.createdby") + ": " + TextFormatting.AQUA + renderPart.getAuthor(partInfo.subid, partInfo.textureID), 0, 0, 0xFFFFFF);
//            GL11.glColor4f(1F, 1F, 1F, 1F);
//            GL11.glPopMatrix();
//        }
        super.drawScreen(mouseX, mouseY, p_73863_3_);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
/*        PartInfo partInfo = parent.getEditingPartInfo();
        RenderPart renderPart = PartRegistry.getRenderPart(parent.getPartType(), partInfo.typeid);
        if (renderPart.hasAuthor(partInfo.subid, parent.textureID)) {
            String author = renderPart.getAuthor(partInfo.subid, parent.textureID);
            if (author.startsWith("@")) {
                float authorNameWidth = fontRendererObj.getStringWidth(author) * 0.6F;
                float authorWidth = fontRendererObj.getStringWidth(I18n.format("gui.createdby")) * 0.6F;
                if (mouseX > 9 + authorWidth && mouseX < 9 + authorWidth + authorNameWidth && mouseY > height - 10 && mouseY < height - 4) {
                    try {
                        Desktop.getDesktop().browse(URI.create("https://twitter.com/" + author.replace("@", "")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
        }*/
        super.mouseClicked(mouseX, mouseY, mouseButton);
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
}
