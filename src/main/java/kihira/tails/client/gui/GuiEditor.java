/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.gui;

import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.PartInfo;
import kihira.tails.common.PartsData;
import kihira.tails.common.Tails;

public class GuiEditor extends GuiBase {

    public int textureID;
    private PartsData.PartType partType;
    private PartsData partsData;
    private PartInfo partInfo;
    public PartInfo originalPartInfo;

    public TintPanel tintPanel;
    public PartsPanel partsPanel;
    private PreviewPanel previewPanel;
    public TexturePanel texturePanel;
    public ControlsPanel controlsPanel;
    public LibraryPanel libraryPanel;
    public LibraryInfoPanel libraryInfoPanel;

    public GuiEditor() {
        //Backup original PartInfo or create default one
        PartInfo partInfo;
        if (Tails.localPartsData == null) {
            Tails.setLocalPartsData(new PartsData());
        }

        //Default to Tail
        partType = PartsData.PartType.TAIL;
        for (PartsData.PartType partType : PartsData.PartType.values()) {
            if (!Tails.localPartsData.hasPartInfo(partType)) {
                Tails.localPartsData.setPartInfo(partType, new PartInfo(false, 0, 0, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null, partType));
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
            panels.add(previewPanel = new PreviewPanel(this, previewWindowEdgeOffset, 0, previewWindowRight - previewWindowEdgeOffset, previewWindowBottom));
            panels.add(partsPanel = new PartsPanel(this, 0, 0, previewWindowEdgeOffset, height - 43));
            panels.add(libraryPanel = new LibraryPanel(this, 0, 0, previewWindowEdgeOffset, height));
            panels.add(texturePanel = new TexturePanel(this, 0, height - 43, previewWindowEdgeOffset, 43));
            panels.add(tintPanel = new TintPanel(this, previewWindowRight, 0, width - previewWindowRight, height));
            panels.add(libraryInfoPanel = new LibraryInfoPanel(this, previewWindowRight, 0, width - previewWindowRight, height / 2));
            panels.add(controlsPanel = new ControlsPanel(this, previewWindowEdgeOffset, previewWindowBottom, previewWindowRight - previewWindowEdgeOffset, height - previewWindowBottom));

/*            partsPanel.enabled = false;
            texturePanel.enabled = false;
            tintPanel.enabled = false;*/

            libraryInfoPanel.enabled = false;
            libraryPanel.enabled = false;
        }
        else {
            tintPanel.resize(previewWindowRight, 0, width - previewWindowRight, height);
            libraryInfoPanel.resize(previewWindowRight, 0, width - previewWindowRight, height / 2);
            partsPanel.resize(0, 0, previewWindowEdgeOffset, height - 43);
            libraryPanel.resize(0, 0, previewWindowEdgeOffset, height);
            previewPanel.resize(previewWindowEdgeOffset, 0, previewWindowRight - previewWindowEdgeOffset, previewWindowBottom);
            texturePanel.resize(0, height - 43, previewWindowEdgeOffset, 43);
            controlsPanel.resize(previewWindowEdgeOffset, previewWindowBottom, previewWindowRight - previewWindowEdgeOffset, height - previewWindowBottom);
        }
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Tails.proxy.addPartsData(mc.getSession().func_148256_e().getId(), Tails.localPartsData);
        super.onGuiClosed();
    }

    public void refreshTintPane() {
        tintPanel.refreshTintPane();
    }

    public void setPartsInfo(PartInfo newPartInfo) {
        partInfo.setTexture(null);
        if (tintPanel.currTintEdit > 0) partInfo.tints[tintPanel.currTintEdit -1] = tintPanel.currTintColour | 0xFF << 24; //Add the alpha manually
        partInfo = new PartInfo(newPartInfo.hasPart, newPartInfo.typeid, newPartInfo.subid, textureID, partInfo.tints, partType, null);
        if (partInfo.hasPart) partInfo.setTexture(TextureHelper.generateTexture(partInfo));

        partsData.setPartInfo(partType, partInfo);
        setPartsData(partsData);
    }

    public PartInfo getPartInfo() {
        return partInfo;
    }

    public void setPartsData(PartsData newPartsData) {
        partsData = newPartsData;
        Tails.proxy.addPartsData(mc.thePlayer.getPersistentID(), partsData);
    }

    public PartsData getPartsData() {
        return partsData;
    }

    public void setPartType(PartsData.PartType partType) {
        this.partType = partType;

        PartInfo newPartInfo = partsData.getPartInfo(partType);
        if (newPartInfo == null) {
            newPartInfo = new PartInfo(false, 0, 0, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null, partType);
        }
        originalPartInfo = newPartInfo.deepCopy();
        PartInfo partInfo = originalPartInfo.deepCopy();
        textureID = partInfo.textureID;

        setCurrTintEdit(0);
        initPartList();
        refreshTintPane();
        setPartsInfo(partInfo);
    }

    public PartsData.PartType getPartType() {
        return partType;
    }

    public void setCurrTintEdit(int currTintEdit) {
        tintPanel.currTintEdit = currTintEdit;
    }

    public void initPartList() {
        partsPanel.initPartList();
    }
}
