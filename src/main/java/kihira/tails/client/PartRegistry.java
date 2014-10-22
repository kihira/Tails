/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client;

import com.google.common.collect.ArrayListMultimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.client.model.ears.ModelCatEars;
import kihira.tails.client.model.ears.ModelFoxEars;
import kihira.tails.client.model.ears.ModelPandaEars;
import kihira.tails.client.model.tail.*;
import kihira.tails.client.render.RenderPart;
import kihira.tails.client.render.RenderWings;
import kihira.tails.common.PartsData;

import java.util.List;

//Yeah using side only isn't nice but as this is static, it means it only gets constructed on the client
@SideOnly(Side.CLIENT)
public class PartRegistry {

    private static final ArrayListMultimap<PartsData.PartType, RenderPart> partRegistry = ArrayListMultimap.create();

    static {
        //Tails
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.fluffy", 2, new ModelFluffyTail(), "foxTail"));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.dragon", 1, new ModelDragonTail(), "dragonTail", "dragonTailStriped"));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.raccoon", 0, new ModelRaccoonTail(), "racoonTail"));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.devil", 1, new ModelDevilTail(), "devilTail"));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.cat", 0, new ModelCatTail(), "tabbyTail", "tigerTail"));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.bird", 0, new ModelBirdTail(), "birdTail"));

        //Ears
        registerPart(PartsData.PartType.EARS, new RenderPart("ears.fox", 0, new ModelFoxEars(), "foxEars"));
        registerPart(PartsData.PartType.EARS, new RenderPart("ears.cat", 0, new ModelCatEars(), "catEars"));
        registerPart(PartsData.PartType.EARS, new RenderPart("ears.panda", 0, new ModelPandaEars(), "pandaEars"));

        //Wings
        registerPart(PartsData.PartType.WINGS, new RenderWings("wings.big", 1, null, "bigWings", "metalWings"));
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
