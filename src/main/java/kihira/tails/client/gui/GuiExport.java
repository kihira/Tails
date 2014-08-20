package kihira.tails.client.gui;

import com.google.common.base.Strings;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.TailInfo;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.Display;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GuiExport extends GuiBaseScreen {

    private final GuiEditTail parent;
    private final TailInfo tailInfo;

    private ScaledResolution scaledRes;
    private String exportMessage = "";
    private GuiButtonTooltip openFolderButton;
    private File exportLoc;

    public GuiExport(GuiEditTail parent, TailInfo tailInfo) {
        this.parent = parent;
        this.tailInfo = tailInfo;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        this.scaledRes = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);

        this.buttonList.add(new GuiButtonTooltip(0, 20, this.height - 90, 130, 20, I18n.format("gui.button.export.userdir"),
                this.scaledRes.getScaledWidth() / 2, I18n.format("gui.button.export.tooltip", System.getProperty("user.home"))));
        this.buttonList.add(new GuiButtonTooltip(1, 20, this.height - 65, 130, 20, I18n.format("gui.button.export.minecraftdir"),
                this.scaledRes.getScaledWidth() / 2, I18n.format("gui.button.export.tooltip", System.getProperty("user.dir"))));
        this.buttonList.add(new GuiButtonTooltip(2, 20, this.height - 40, 130, 20, I18n.format("gui.button.export.custom"),
                this.scaledRes.getScaledWidth() / 2, I18n.format("gui.button.export.custom.tooltip")));
        this.buttonList.add(this.openFolderButton = new GuiButtonTooltip(3, this.width - 150, this.height - 65, 130, 20, I18n.format("gui.button.openfolder"),
                this.scaledRes.getScaledWidth() / 2, I18n.format("gui.button.openfolder.tooltip")));
        this.openFolderButton.visible = !Strings.isNullOrEmpty(this.exportMessage);

        GuiButton button;
        this.buttonList.add(button = new GuiButtonTooltip(10, this.width - 150, this.height - 40, 130, 20, I18n.format("gui.button.upload"),
                this.scaledRes.getScaledWidth() / 2, I18n.format("tails.upload.unavailable")));
        button.enabled = false;
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        this.drawDefaultBackground();

        this.drawCenteredString(this.fontRendererObj, I18n.format("gui.export.title"), this.width / 2, 25, 0xFFFFFF);
        this.fontRendererObj.drawSplitString(I18n.format("gui.export.information"), this.width / 6, 50, (int) (this.scaledRes.getScaledWidth() / 1.5F), 0xFFFFFF);
        if (!Strings.isNullOrEmpty(this.exportMessage)) this.fontRendererObj.drawSplitString(this.exportMessage, 160, this.height - 88, this.width - 160, 0xFFFFFF);

        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        //Export to file
        if (button.id == 0 || button.id == 1 || button.id == 2) {
            AbstractClientPlayer player = this.mc.thePlayer;
            File file;

            this.exportMessage = "";
            this.exportLoc = null;
            if (button.id == 0) file = new File(System.getProperty("user.home"));
            else if (button.id == 1) file = new File(System.getProperty("user.dir"));
            else {
                JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (fileChooser.showSaveDialog(Display.getParent()) == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                }
                else return;
            }

            if (file.exists() && file.canWrite()) {
                this.exportLoc = file;
                file = new File(file, File.separatorChar + player.getCommandSenderName() + ".png");

                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        this.exportMessage = String.format("Failed to create skin file! %s", e);
                        e.printStackTrace();
                    }
                }

                BufferedImage image = TextureHelper.writeTailInfoToSkin(this.tailInfo, player);
                if (image != null) {
                    try {
                        ImageIO.write(image, "png", file);
                    } catch (IOException e) {
                        this.exportMessage = String.format("Failed to save skin file! %s", e);
                        e.printStackTrace();
                    }
                }
            }

            if (Strings.isNullOrEmpty(this.exportMessage)) {
                this.openFolderButton.visible = true;
                this.exportMessage = I18n.format("tails.export.success", file);
            }
        }
        if (button.id == 3 && this.exportLoc != null) {
            try {
                Desktop.getDesktop().browse(this.exportLoc.toURI());
            } catch (IOException e) {
                this.exportMessage = String.format("Failed to open export location: %s", e);
                e.printStackTrace();
            }
        }

        //Upload
        if (button.id == 10) {
/*            try {
                BufferedImage image = TextureHelper.writeTailInfoToSkin(this.tailInfo, this.mc.thePlayer);
                String dataURI = TextureHelper.bufferedImageToBase64(image);
                String skinURL = "https://minecraft.net/profile/skin/remote?url=";

                Desktop.getDesktop().browse(URI.create(skinURL + dataURI));

                //System.out.println(URI.create(skinURL + dataURI));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            this.exportMessage = I18n.format("tail.upload.unavailable");
        }
    }
}
