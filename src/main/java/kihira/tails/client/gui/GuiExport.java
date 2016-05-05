/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.gui;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import kihira.foxlib.client.gui.GuiBaseScreen;
import kihira.foxlib.client.toast.ToastManager;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.PartsData;
import kihira.tails.common.Tails;
import kihira.tails.common.network.PlayerDataMessage;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.Display;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

class GuiExport extends GuiBaseScreen {

    private final GuiEditor parent;
    private final PartsData partsData;

    private ScaledResolution scaledRes;
    private String exportMessage = "";
    private URI exportLoc;
    private GuiButton openFolderButton;

    GuiExport(GuiEditor parent, PartsData partsData) {
        this.parent = parent;
        this.partsData = partsData;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        this.scaledRes = new ScaledResolution(this.mc);

        //Left
        this.buttonList.add(new GuiButtonTooltip(0, 20, this.height - 90, 130, 20, I18n.format("gui.button.export.userdir"),
                this.scaledRes.getScaledWidth() / 2, I18n.format("gui.button.export.tooltip", System.getProperty("user.home"))));
        this.buttonList.add(new GuiButtonTooltip(1, 20, this.height - 65, 130, 20, I18n.format("gui.button.export.minecraftdir"),
                this.scaledRes.getScaledWidth() / 2, I18n.format("gui.button.export.tooltip", System.getProperty("user.dir"))));
        this.buttonList.add(new GuiButtonTooltip(2, 20, this.height - 40, 130, 20, I18n.format("gui.button.export.custom"),
                this.scaledRes.getScaledWidth() / 2, I18n.format("gui.button.export.custom.tooltip")));

        //Right
        this.buttonList.add(openFolderButton = new GuiButtonTooltip(3, this.width - 150, this.height - 65, 130, 20, I18n.format("gui.button.openfolder"),
                this.scaledRes.getScaledWidth() / 2, I18n.format("gui.button.openfolder.tooltip")));
        this.openFolderButton.visible = !Strings.isNullOrEmpty(this.exportMessage);

        this.buttonList.add(new GuiButtonTooltip(10, this.width - 150, this.height - 40, 130, 20, I18n.format("gui.button.upload"),
                this.scaledRes.getScaledWidth() / 2, I18n.format("tails.upload.tooltip")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        this.drawDefaultBackground();

        this.drawCenteredString(this.fontRendererObj, I18n.format("gui.export.title"), this.width / 2, 25, 0xFFFFFF);
        this.fontRendererObj.drawSplitString(I18n.format("gui.export.information"), this.width / 6, 50, (int) (this.scaledRes.getScaledWidth() / 1.5F), 0xFFFFFF);
        if (!Strings.isNullOrEmpty(this.exportMessage)) this.fontRendererObj.drawSplitString(this.exportMessage, 160, this.height - 88, this.width - 160, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, p_73863_3_);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
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
                this.exportLoc = file.toURI();
                file = new File(file, File.separatorChar + player.getDisplayNameString() + ".png");

                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        setExportMessage(TextFormatting.DARK_RED + String.format("Failed to create skin file! %s", e));
                        e.printStackTrace();
                    }
                }

                BufferedImage image = TextureHelper.writePartsDataToSkin(this.partsData, player);
                if (image != null) {
                    try {
                        ImageIO.write(image, "png", file);
                    } catch (IOException e) {
                        setExportMessage(TextFormatting.DARK_RED + String.format("Failed to save skin file! %s", e));
                        e.printStackTrace();
                    }
                }
                else {
                    setExportMessage(TextFormatting.DARK_RED + "Failed to export skin, image was null!");
                    file.delete();
                }
            }

            if (Strings.isNullOrEmpty(this.exportMessage)) {
                savePartsData();
                this.openFolderButton.visible = true;
                setExportMessage(TextFormatting.GREEN + I18n.format("tails.export.success", file));
            }
        }
        if (button.id == 3 && this.exportLoc != null) {
            try {
                Desktop.getDesktop().browse(this.exportLoc);
            } catch (IOException e) {
                setExportMessage(TextFormatting.DARK_RED + String.format("Failed to open export location: %s", e));
                e.printStackTrace();
            }
        }

        //Upload
        if (button.id == 10) {
            final BufferedImage image = TextureHelper.writePartsDataToSkin(this.partsData, this.mc.thePlayer);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    exportMessage = I18n.format("tails.uploading");
                    new ImgurUpload().uploadImage(image);
                }
            };
            runnable.run();
        }
    }

    @Override
    protected void keyTyped(char key, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(parent);
        }
        else {
            super.keyTyped(key, keyCode);
        }
    }

    private void setExportMessage(String message) {
        exportMessage = message;
        ToastManager.INSTANCE.createCenteredToast(width / 2, height - 45, new ScaledResolution(mc).getScaledWidth() / 3, exportMessage);
    }

    private void savePartsData() {
        Tails.setLocalPartsData(partsData);
        Tails.proxy.addPartsData(mc.thePlayer.getPersistentID(), partsData);
        Tails.networkWrapper.sendToServer(new PlayerDataMessage(mc.getSession().getProfile().getId(), partsData, false));
    }

    private class ImgurUpload {
        static final String CLIENT_ID = "ceb9fca19ef9a31";

        void uploadImage(BufferedImage image) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedReader in = null;

            try {
                URL url = new URL("https://api.imgur.com/3/upload.json");

                ImageIO.write(image, "png", baos);
                baos.flush();

                String base64Image = DatatypeConverter.printBase64Binary(baos.toByteArray());
                String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(base64Image, "UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);

                conn.setRequestProperty("Authorization", "Client-ID " + CLIENT_ID);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.close();

                //Successful uploading!
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    JsonObject jsonElement = new JsonParser().parse(in).getAsJsonObject();
                    if (jsonElement.get("status").getAsInt() == 200) {
                        JsonObject dataJson = jsonElement.get("data").getAsJsonObject();
                        String id = dataJson.get("id").getAsString();

                        String imgurURL = "http://imgur.com/" + id + ".png";
                        String skinURL = "https://minecraft.net/profile/skin/remote?url=";

                        setExportMessage(TextFormatting.GREEN + I18n.format("tails.upload.success"));
                        exportLoc = URI.create(skinURL + imgurURL);
                        openFolderButton.visible = true;
                        savePartsData();

                        Desktop.getDesktop().browse(exportLoc);
                    }
                    else {
                        handleError(jsonElement);
                    }
                }
                else {
                    if (conn.getResponseCode() != 500) {
                        in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        JsonObject jsonElement = new JsonParser().parse(in).getAsJsonObject();
                        handleError(jsonElement);
                    }
                    else setExportMessage(TextFormatting.DARK_RED + I18n.format("tails.upload.failed"));
                }

            } catch (IOException e) {
                Tails.logger.catching(e);
            } catch (JsonParseException e) {
                Tails.logger.catching(e);
            } finally {
                IOUtils.closeQuietly(baos);
                IOUtils.closeQuietly(in);
            }
        }

        private void handleError(JsonObject json) {
            int status = json.get("status").getAsInt();

            //Rate limiting
            if (status == 429 || status == 403) {
                setExportMessage(TextFormatting.DARK_RED + I18n.format("tails.upload.ratelimit"));
            }
            else setExportMessage(TextFormatting.DARK_RED + I18n.format("tails.upload.failed"));
        }
    }

}
