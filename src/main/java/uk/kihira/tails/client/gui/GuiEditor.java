package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.Config;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.proxy.ClientProxy;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class GuiEditor extends GuiBase
{
    static final int TEXT_COLOUR = 0xFFFFFF;
    static final int HOZ_LINE_COLOUR = 0xFF000000;
    static final int SOFT_BLACK = 0xEA000000;
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

    public GuiEditor()
    {
        super(new TranslationTextComponent("tails.editor.title"), 4);

        this.playerUUID = PlayerEntity.getUUID(getMinecraft().getSession().getProfile());

        // Load outfit or create empty one
        this.originalOutfit = Config.localOutfit.get() == null ? new Outfit() : Config.localOutfit.get();

        // Copy outfit for modifying and set as our current outfit
        // todo should a new UUID be generated?
        // if one isn't generated, won't be able to properly cache data on clients.
        // but then again, we would probably be sent the entire json blob when trying to get the player outfit anyway
        this.outfit = Tails.GSON.fromJson(Tails.GSON.toJson(this.originalOutfit), Outfit.class);
        if (this.outfit == null)
        {
            this.outfit = new Outfit();
        }
        setOutfit(this.outfit);
    }

    @Override
    public void init()
    {
        final int previewWindowEdgeOffset = 150;
        final int previewWindowRight = this.width - previewWindowEdgeOffset;
        final int previewWindowBottom = this.height - 30;
        final int texSelectHeight = 35;

        //Not an ideal solution but keeps everything from resetting on resize
        if (this.tintPanel == null)
        {
            final ArrayList<Panel<?>> layer0 = getLayer(0);
            final ArrayList<Panel<?>> layer1 = getLayer(1);

            layer0.add(this.previewPanel = new PreviewPanel(
                    this,
                    previewWindowEdgeOffset,
                    0,
                    previewWindowRight - previewWindowEdgeOffset,
                    previewWindowBottom)
            );
            layer1.add(this.partsPanel = new PartsPanel(
                    this,
                    0,
                    0,
                    previewWindowEdgeOffset,
                    this.height - texSelectHeight)
            );
            layer1.add(this.libraryPanel = new LibraryPanel(
                    this,
                    0,
                    0,
                    previewWindowEdgeOffset,
                    this.height)
            );
            layer1.add(this.tintPanel = new TintPanel(
                    this,
                    previewWindowRight,
                    0,
                    this.width - previewWindowRight,
                    TINT_PANEL_HEIGHT)
            );
            layer1.add(this.transformPanel = new TransformPanel(
                    this,
                    previewWindowRight,
                    TINT_PANEL_HEIGHT,
                    this.width - previewWindowRight,
                    this.height - TINT_PANEL_HEIGHT)
            );
            layer1.add(this.libraryInfoPanel =
                    new LibraryInfoPanel(
                            this,
                            previewWindowRight,
                            0,
                            this.width - previewWindowRight,
                            this.height - 60)
            );
            layer1.add(this.controlsPanel = new ControlsPanel(
                    this,
                    previewWindowEdgeOffset,
                    previewWindowBottom,
                    previewWindowRight - previewWindowEdgeOffset,
                    this.height - previewWindowBottom)
            );

            this.libraryInfoPanel.enabled = false;
            this.libraryPanel.enabled = false;
        }
        else
        {
            this.tintPanel.resize(previewWindowRight, 0, this.width - previewWindowRight, TINT_PANEL_HEIGHT);
            this.transformPanel.resize(previewWindowRight, TINT_PANEL_HEIGHT,this.width - previewWindowRight, this.height - TINT_PANEL_HEIGHT);
            this.libraryInfoPanel.resize(previewWindowRight, 0, this.width - previewWindowRight, this.height - 60);
            this.partsPanel.resize(0, 0, previewWindowEdgeOffset, this.height - texSelectHeight);
            this.libraryPanel.resize(0, 0, previewWindowEdgeOffset, this.height);
            this.previewPanel.resize(previewWindowEdgeOffset, 0, previewWindowRight - previewWindowEdgeOffset, previewWindowBottom);
            this.controlsPanel.resize(previewWindowEdgeOffset, previewWindowBottom, previewWindowRight - previewWindowEdgeOffset, this.height - previewWindowBottom);
        }

        super.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        // Render any parts that have been queued up whilst in GUI as RenderWorldLast is called before GUIs
        ((ClientProxy) Tails.proxy).partRenderer.doRender(matrixStack);
    }

    @Override
    public void onClose()
    {
        Tails.proxy.setActiveOutfit(playerUUID, Config.localOutfit.get());
        super.onClose();
    }

    void refreshTintPane()
    {
        tintPanel.updateTints(true);
    }

    /**
     * Sets the current OutfitPart that is being edited to the one supplied
     * @param outfitPart The outfit part
     */
    void setActiveOutfitPart(@Nullable OutfitPart outfitPart)
    {
        // todo should maintain a list of IOutfitPartSelected instead
        getAllPanels().forEach((final Panel panel) -> {
            if (panel instanceof IOutfitPartSelected)
            {
                ((IOutfitPartSelected) panel).OnOutfitPartSelected(outfitPart);
            }
        });

        currentOutfitPart = outfitPart;
    }

    /**
     * Adds a new OutfitPart to the Outfit and sets it to the current edited one
     * @param outfitPart The part to be added
     */
    void addOutfitPart(OutfitPart outfitPart)
    {
        outfit.parts.add(outfitPart);
        setActiveOutfitPart(outfitPart);
    }

    /**
     * Gets the current part on the outfit that is selected
     * @return The selected part on the outfit. May be null if there is no part or outfit
     */
    @Nullable
    OutfitPart getCurrentOutfitPart()
    {
        if (outfit == null)
        {
            return null;
        }
        return currentOutfitPart;
    }

    public void setOutfit(Outfit newOutfit)
    {
        outfit = newOutfit;
        Tails.proxy.setActiveOutfit(playerUUID, outfit);
    }

    public Outfit getOutfit()
    {
        return outfit;
    }
}
