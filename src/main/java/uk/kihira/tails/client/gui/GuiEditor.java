package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.kihira.tails.client.OutfitPart;
import uk.kihira.tails.common.Outfit;
import uk.kihira.tails.common.OutfitManager;
import uk.kihira.tails.common.Tails;

import javax.annotation.Nullable;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class GuiEditor extends GuiBase {
    static final int TEXT_COLOUR = 0xFFFFFF;
    static final int HOZ_LINE_COLOUR = 0xFF000000;
    static final int DARK_GREY = 0xFF1A1A1A;
    static final int GREY = 0xFF666666;

    private static final int TINT_PANEL_HEIGHT = 145;

    @Nullable
    private Outfit originalOutfit; // The outfit from before the GUI was opened. Is updated when player saves new outfit
    private Outfit outfit;
    @Nullable
    private OutfitPart currentOutfitPart;
    private UUID playerUUID;

    TintPanel tintPanel;
    PartsPanel partsPanel;
    private TransformPanel transformPanel;
    private PreviewPanel previewPanel;
    private ControlsPanel controlsPanel;
    public LibraryPanel libraryPanel;
    public LibraryInfoPanel libraryInfoPanel;

    public GuiEditor() {
        super(4);

        playerUUID = EntityPlayer.getUUID(Minecraft.getInstance().getSession().getProfile());

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
        int previewWindowEdgeOffset = 150;
        int previewWindowRight = width - previewWindowEdgeOffset;
        int previewWindowBottom = height - 30;
        int texSelectHeight = 35;

        //Not an ideal solution but keeps everything from resetting on resize
        if (tintPanel == null) {
            getLayer(0).add(previewPanel = new PreviewPanel(this, previewWindowEdgeOffset, 0, previewWindowRight - previewWindowEdgeOffset, previewWindowBottom));
            getLayer(1).add(partsPanel = new PartsPanel(this, 0, 0, previewWindowEdgeOffset, height - texSelectHeight));
            getLayer(1).add(libraryPanel = new LibraryPanel(this, 0, 0, previewWindowEdgeOffset, height));
            getLayer(1).add(tintPanel = new TintPanel(this, previewWindowRight, 0, width - previewWindowRight, TINT_PANEL_HEIGHT));
            getLayer(1).add(transformPanel = new TransformPanel(this, previewWindowRight, TINT_PANEL_HEIGHT, width - previewWindowRight, height - TINT_PANEL_HEIGHT));
            getLayer(1).add(libraryInfoPanel = new LibraryInfoPanel(this, previewWindowRight, 0, width - previewWindowRight, height - 60));
            getLayer(1).add(controlsPanel = new ControlsPanel(this, previewWindowEdgeOffset, previewWindowBottom, previewWindowRight - previewWindowEdgeOffset, height - previewWindowBottom));

            libraryInfoPanel.enabled = false;
            libraryPanel.enabled = false;
        }
        else {
            tintPanel.resize(previewWindowRight, 0, width - previewWindowRight, TINT_PANEL_HEIGHT);
            transformPanel.resize(previewWindowRight, TINT_PANEL_HEIGHT,width - previewWindowRight, height - TINT_PANEL_HEIGHT);
            libraryInfoPanel.resize(previewWindowRight, 0, width - previewWindowRight, height - 60);
            partsPanel.resize(0, 0, previewWindowEdgeOffset, height - texSelectHeight);
            libraryPanel.resize(0, 0, previewWindowEdgeOffset, height);
            previewPanel.resize(previewWindowEdgeOffset, 0, previewWindowRight - previewWindowEdgeOffset, previewWindowBottom);
            controlsPanel.resize(previewWindowEdgeOffset, previewWindowBottom, previewWindowRight - previewWindowEdgeOffset, height - previewWindowBottom);
        }
        super.initGui();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        // Render any parts that have been queued up whilst in GUI as RenderWorldLast is called before GUIs
        ((ClientProxy) Tails.proxy).partRenderer.doRender();
    }

    @Override
    public void onGuiClosed() {
        OutfitManager.INSTANCE.setActiveOutfit(playerUUID, Tails.localOutfit);
        super.onGuiClosed();
    }

    void refreshTintPane() {
        tintPanel.updateTints(true);
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
     * @param outfitPart The part to be added
     */
    void addOutfitPart(OutfitPart outfitPart) {
        outfit.parts.add(outfitPart);
        setActiveOutfitPart(outfitPart);
    }

    @Nullable
    OutfitPart getCurrentOutfitPart() {
        return currentOutfitPart;
    }

    public void setOutfit(Outfit newOutfit) {
        outfit = newOutfit;
        OutfitManager.INSTANCE.setActiveOutfit(playerUUID, outfit);
    }

    public Outfit getOutfit() {
        return outfit;
    }

    void clearCurrTintEdit() {
        // todo still needed? tintPanel.currTintEdit = 0;
    }
}
