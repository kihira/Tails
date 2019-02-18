package uk.kihira.tails.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Implemented by GuiButton's that has a tooltip
 */
@Nonnull
@OnlyIn(Dist.CLIENT)
public interface ITooltip {
    List<String> getTooltip(int mouseX, int mouseY, float mouseIdleTime);
}
