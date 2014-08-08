package kihira.tails.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * A base class that all tails extend
 */
public abstract class ModelTailBase extends ModelBase {

    /**
     * Returns a list of parts that can be enabled or disabled by the owner of the tail
     * @return List of parts
     */
    public abstract List<String> getToggleableParts();

    /**
     * Renders the tail with the optional parts list provided
     * @param thePlayer The owner of the tail
     * @param partsEnabled The parts enabled
     */
    public abstract void renderWithParts(EntityPlayer thePlayer, List<String> partsEnabled);
}
