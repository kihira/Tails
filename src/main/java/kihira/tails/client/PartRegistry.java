/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client;

import com.google.common.collect.ArrayListMultimap;
import kihira.tails.client.model.ModelSizableMuzzle;
import kihira.tails.client.model.ears.ModelCatEars;
import kihira.tails.client.model.ears.ModelCatSmallEars;
import kihira.tails.client.model.ears.ModelFoxEars;
import kihira.tails.client.model.ears.ModelPandaEars;
import kihira.tails.client.model.tail.*;
import kihira.tails.client.render.RenderPart;
import kihira.tails.client.render.RenderWings;
import kihira.tails.common.PartsData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

//Yeah using side only isn't nice but as this is static, it means it only gets constructed on the client
@SideOnly(Side.CLIENT)
public class PartRegistry {

    private static final ArrayListMultimap<PartsData.PartType, RenderPart> partRegistry = ArrayListMultimap.create();

    static {
        //Tails
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.fluffy", 2, new ModelFluffyTail(), null, "foxTail"));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.dragon", 1, new ModelDragonTail(), null, "dragonTail", "dragonTailStriped").setAuthor("@TTFTCUTS", 0, 0).setAuthor("@TTFTCUTS", 1, 0));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.raccoon", 0, new ModelRaccoonTail(), null, "racoonTail"));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.devil", 1, new ModelDevilTail(), null, "devilTail"));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.cat", 0, new ModelCatTail(), null, "tabbyTail", "tigerTail"));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.bird", 0, new ModelBirdTail(), null, "birdTail").setAuthor("@blusunrize", 0, 0));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.shark", 0, new ModelSharkTail(), "access_denied", "sharkTail"));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.bunny", 0, new ModelBunnyTail(), "@carrotcodes", "bunnyTail"));

        //Ears
        registerPart(PartsData.PartType.EARS, new RenderPart("ears.fox", 1, new ModelFoxEars(), "@Adeon", "foxEars"));
        registerPart(PartsData.PartType.EARS, new RenderPart("ears.cat", 0, new ModelCatEars(), null, "catEars"));
        registerPart(PartsData.PartType.EARS, new RenderPart("ears.panda", 0, new ModelPandaEars(), null, "pandaEars"));
        registerPart(PartsData.PartType.EARS, new RenderPart("ears.catSmall", 0, new ModelCatSmallEars(), null, "catSmallEars"));

        //Wings
        registerPart(PartsData.PartType.WINGS, new RenderWings("wings.big", 1, null, null, "bigWings", "metalWings", "dragonWings", "dragonBonelessWings")
                .setAuthor("@littlechippie").setAuthor("Dracyoshi", 0, 2).setAuthor("Dracyoshi", 0, 3).setAuthor("Dracyoshi", 1, 2).setAuthor("Dracyoshi", 1, 3));

        // Muzzle
        registerPart(PartsData.PartType.MUZZLE, new RenderPart("muzzle.standard", 4, new ModelSizableMuzzle(-2f, -3f, -9f, 4, 3, 5), null, "standardMuzzle", "altMuzzle"));
        registerPart(PartsData.PartType.MUZZLE, new RenderPart("muzzle.slim", 4, new ModelSizableMuzzle(-2f, -2f, -9f, 4, 2, 5), null, "standardMuzzle", "altMuzzle"));
        registerPart(PartsData.PartType.MUZZLE, new RenderPart("muzzle.thin", 4, new ModelSizableMuzzle(-1.5f, -2f, -9f, 3, 2, 5, 0, 9), null, "standardMuzzle", "altMuzzle"));

    }

    public static void registerPart(PartsData.PartType partType, RenderPart renderPart) {
        partRegistry.put(partType, renderPart);
    }

    public static List<RenderPart> getParts(PartsData.PartType partType) {
        return partRegistry.get(partType);
    }

    /**
     * Safely gets a render part. By safely, this means it checks if the type id is within bounds of the list for that
     * part type and if not, returns the RenderPart associated with type id 0.
     * @param partType The part type
     * @param index The index/type id
     * @return The render part
     */
    public static RenderPart getRenderPart(PartsData.PartType partType, int index) {
        List<RenderPart> parts = PartRegistry.getParts(partType);
        index = index >= parts.size() ? 0 : index;
        return parts.get(index);
    }
}
