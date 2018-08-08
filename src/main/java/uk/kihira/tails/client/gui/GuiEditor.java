package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import uk.kihira.tails.client.OutfitPart;
import uk.kihira.tails.common.Outfit;
import uk.kihira.tails.common.Tails;

import javax.annotation.Nullable;
import java.util.UUID;

public class GuiEditor extends GuiBase {
    public static final int TEXT_COLOUR = 0xFFFFFF;

    @Nullable
    public Outfit originalOutfit; // The outfit from before the GUI was opened. Is updated when player saves new outfit
    private Outfit outfit;
    @Nullable
    private OutfitPart currentOutfitPart;
    private UUID playerUUID;

    TintPanel tintPanel;
    PartsPanel partsPanel;
    private PreviewPanel previewPanel;
    private ControlsPanel controlsPanel;
    public LibraryPanel libraryPanel;
    public LibraryInfoPanel libraryInfoPanel;

    public GuiEditor() {
        super(4);

        playerUUID = EntityPlayer.getUUID(Minecraft.getMinecraft().getSession().getProfile());

        // Load outfit or create empty one
        if (Tails.localOutfit == null) originalOutfit = new Outfit();
        else originalOutfit = Tails.localOutfit;

        // Copy outfit for modifying and set as our current outfit
        // todo should a new UUID be generated?
        // if one isn't generated, won't be able to properly cache data on clients.
        // but then again, we would probably be sent the entire json blob when trying to get the player outfit anyway
        outfit = Tails.gson.fromJson(Tails.gson.toJson(originalOutfit), Outfit.class);
        if (outfit == null) outfit = new Outfit();
        setOutfit(outfit);
    }

    @Override
    public void initGui() {
        int previewWindowEdgeOffset = 110;
        int previewWindowRight = width - previewWindowEdgeOffset;
        int previewWindowBottom = height - 30;
        int texSelectHeight = 35;

        //Not an ideal solution but keeps everything from resetting on resize
        if (tintPanel == null) {
            getLayer(0).add(previewPanel = new PreviewPanel(this, previewWindowEdgeOffset, 0, previewWindowRight - previewWindowEdgeOffset, previewWindowBottom));
            getLayer(1).add(partsPanel = new PartsPanel(this, 0, 0, previewWindowEdgeOffset, height - texSelectHeight));
            getLayer(1).add(libraryPanel = new LibraryPanel(this, 0, 0, previewWindowEdgeOffset, height));
            getLayer(1).add(tintPanel = new TintPanel(this, previewWindowRight, 0, width - previewWindowRight, height));
            getLayer(1).add(libraryInfoPanel = new LibraryInfoPanel(this, previewWindowRight, 0, width - previewWindowRight, height - 60));
            getLayer(1).add(controlsPanel = new ControlsPanel(this, previewWindowEdgeOffset, previewWindowBottom, previewWindowRight - previewWindowEdgeOffset, height - previewWindowBottom));

            libraryInfoPanel.enabled = false;
            libraryPanel.enabled = false;
        }
        else {
            tintPanel.resize(previewWindowRight, 0, width - previewWindowRight, height);
            libraryInfoPanel.resize(previewWindowRight, 0, width - previewWindowRight, height - 60);
            partsPanel.resize(0, 0, previewWindowEdgeOffset, height - texSelectHeight);
            libraryPanel.resize(0, 0, previewWindowEdgeOffset, height);
            previewPanel.resize(previewWindowEdgeOffset, 0, previewWindowRight - previewWindowEdgeOffset, previewWindowBottom);
            controlsPanel.resize(previewWindowEdgeOffset, previewWindowBottom, previewWindowRight - previewWindowEdgeOffset, height - previewWindowBottom);
        }
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Tails.proxy.setActiveOutfit(playerUUID, Tails.localOutfit);
        //setScale(guiScale);
        super.onGuiClosed();
    }

    void refreshTintPane() {
        tintPanel.refreshTintPane();
    }

    /**
     * Sets the current OutfitPart that is being edited to the one supplied
     * @param outfitPart The outfit part
     */
    void setActiveOutfitPart(@Nullable OutfitPart outfitPart) {
        currentOutfitPart = outfitPart;
    }

    /**
     * Adds a new OutfitPart to the Outfit and sets it to the current edited one
     * @param outfitPart
     */
    void addOutfitPart(OutfitPart outfitPart) {
        outfit.parts.add(outfitPart);
        setActiveOutfitPart(outfitPart);
    }

    @Nullable
    OutfitPart getCurrentOutfitPart() {
        return currentOutfitPart;
    }

    public void setOutfit(Outfit outfit) {
        this.outfit = outfit;
        Tails.proxy.setActiveOutfit(playerUUID, this.outfit);
    }

    public Outfit getOutfit() {
        return outfit;
    }

    void clearCurrTintEdit() {
        // todo still needed? tintPanel.currTintEdit = 0;
    }
}
