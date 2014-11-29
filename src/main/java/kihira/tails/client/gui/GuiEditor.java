/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.gui;

import kihira.tails.common.PartInfo;
import kihira.tails.common.PartsData;
import kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;

public class GuiEditor extends GuiBase {

    public int textureID;
    public PartsData.PartType partType;
    public PartsData partsData;
    public PartInfo partInfo;
    public PartInfo originalPartInfo;

    private TintPanel tintPanel;
    private PartsPanel partsPanel;
    private PreviewPanel previewPanel;
    private TexturePanel texturePanel;
    private ControlsPanel controlsPanel;

    public GuiEditor() {
        //Backup original PartInfo or create default one
        PartInfo partInfo;
        if (Tails.localPartsData == null) {
            Tails.setLocalPartsData(new PartsData(Minecraft.getMinecraft().thePlayer.getPersistentID()));
        }

        //Default to Tail
        partType = PartsData.PartType.TAIL;
        for (PartsData.PartType partType : PartsData.PartType.values()) {
            if (!Tails.localPartsData.hasPartInfo(partType)) {
                Tails.localPartsData.setPartInfo(partType, new PartInfo(Minecraft.getMinecraft().thePlayer.getPersistentID(), false, 0, 0, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null, partType));
            }
        }
        partInfo = Tails.localPartsData.getPartInfo(partType);

        originalPartInfo = partInfo.deepCopy();
        partsData = Tails.localPartsData.deepCopy();
        this.partInfo = originalPartInfo.deepCopy();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        int previewWindowEdgeOffset = 110;
        int previewWindowRight = width - previewWindowEdgeOffset;
        int previewWindowBottom = height - 30;

        //Not an ideal solution but keeps everything from resetting on resize
        if (tintPanel == null) {
            tintPanel = new TintPanel(this, previewWindowRight, 0, width - previewWindowRight, height);
            partsPanel = new PartsPanel(this, 0, 0, previewWindowEdgeOffset, height - 43);
            previewPanel = new PreviewPanel(this, previewWindowEdgeOffset, 0, previewWindowRight - previewWindowEdgeOffset, previewWindowBottom);
            texturePanel = new TexturePanel(this, 0, height - 43, previewWindowEdgeOffset, 43);
            controlsPanel = new ControlsPanel(this, previewWindowEdgeOffset, previewWindowBottom, previewWindowRight - previewWindowEdgeOffset, height - previewWindowBottom);

            panels.add(previewPanel);
            panels.add(partsPanel);
            panels.add(texturePanel);
            panels.add(tintPanel);
            panels.add(controlsPanel);
        }
        else {
            tintPanel.resize(previewWindowRight, 0, width - previewWindowRight, height);
            partsPanel.resize(0, 0, previewWindowEdgeOffset, height - 43);
            previewPanel.resize(previewWindowEdgeOffset, 0, previewWindowRight - previewWindowEdgeOffset, previewWindowBottom);
            texturePanel.resize(0, height - 43, previewWindowEdgeOffset, 43);
            controlsPanel.resize(previewWindowEdgeOffset, previewWindowBottom, previewWindowRight - previewWindowEdgeOffset, height - previewWindowBottom);
        }
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Tails.proxy.addPartsData(this.mc.thePlayer.getPersistentID(), Tails.localPartsData);
        super.onGuiClosed();
    }

    public void refreshTintPane() {
        tintPanel.refreshTintPane();
    }

    public void updatePartsData() {
        partsPanel.updatePartsData();
    }

    public int getCurrTintEdit() {
        return tintPanel.currTintEdit;
    }

    public void setCurrTintEdit(int currTintEdit) {
        tintPanel.currTintEdit = currTintEdit;
    }

    public int getCurrTintColour() {
        return tintPanel.currTintColour;
    }

    public void setCurrTintColour(int currTintColour) {
        tintPanel.currTintColour = currTintColour;
    }

    public void selectDefaultListEntry() {
        partsPanel.selectDefaultListEntry();
    }

    public void initPartList() {
        partsPanel.initPartList();
    }
}
