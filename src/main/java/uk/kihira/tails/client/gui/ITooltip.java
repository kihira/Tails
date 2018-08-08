package uk.kihira.tails.client.gui;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Implemented by GuiButton's that has a tooltip
 */
@Nonnull
public interface ITooltip {
    List<String> getTooltip(int mouseX, int mouseY, float mouseIdleTime);
}
