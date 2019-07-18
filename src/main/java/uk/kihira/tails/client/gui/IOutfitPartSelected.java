package uk.kihira.tails.client.gui;

import uk.kihira.tails.client.OutfitPart;

import javax.annotation.Nullable;

public interface IOutfitPartSelected
{
    /**
     * This method is called when the player selects a part that exists on the current outfit
     * This can also include if the part was just added. It is guaranteed that the part is in the outfit at this stage
     *
     * @param part The part that was just selected
     */
    void OnOutfitPartSelected(@Nullable final OutfitPart part);
}
