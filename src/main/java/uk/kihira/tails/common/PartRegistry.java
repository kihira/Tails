/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import com.google.common.collect.ArrayListMultimap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import uk.kihira.tails.client.Part;
import uk.kihira.tails.client.model.ModelSizableMuzzle;
import uk.kihira.tails.client.model.ears.ModelCatEars;
import uk.kihira.tails.client.model.ears.ModelCatSmallEars;
import uk.kihira.tails.client.model.ears.ModelFoxEars;
import uk.kihira.tails.client.model.ears.ModelPandaEars;
import uk.kihira.tails.client.model.tail.*;
import uk.kihira.tails.client.render.LegacyPartRenderer;
import uk.kihira.tails.client.render.RenderWings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

//Yeah using side only isn't nice but as this is static, it means it only gets constructed on the client
@SideOnly(Side.CLIENT)
public class PartRegistry {
    private static final ArrayListMultimap<PartsData.PartType, LegacyPartRenderer> partRegistry = ArrayListMultimap.create();

    private static final HashMap<UUID, Part> parts = new HashMap<>();

    static {
        //Tails
        registerPart(PartsData.PartType.TAIL, new LegacyPartRenderer("tail.fluffy", 2, new ModelFluffyTail(), null, "fox_tail"));
        registerPart(PartsData.PartType.TAIL, new LegacyPartRenderer("tail.dragon", 1, new ModelDragonTail(), null, "dragon_tail", "dragon_tail_striped").setAuthor("@TTFTCUTS", 0, 0).setAuthor("@TTFTCUTS", 1, 0));
        registerPart(PartsData.PartType.TAIL, new LegacyPartRenderer("tail.raccoon", 0, new ModelRaccoonTail(), null, "racoon_tail"));
        registerPart(PartsData.PartType.TAIL, new LegacyPartRenderer("tail.devil", 1, new ModelDevilTail(), null, "devil_tail"));
        registerPart(PartsData.PartType.TAIL, new LegacyPartRenderer("tail.cat", 0, new ModelCatTail(), null, "tabby_tail", "tiger_tail"));
        registerPart(PartsData.PartType.TAIL, new LegacyPartRenderer("tail.bird", 0, new ModelBirdTail(), null, "bird_tail").setAuthor("@blusunrize", 0, 0));
        registerPart(PartsData.PartType.TAIL, new LegacyPartRenderer("tail.shark", 0, new ModelSharkTail(), "access_denied", "shark_tail"));
        registerPart(PartsData.PartType.TAIL, new LegacyPartRenderer("tail.bunny", 0, new ModelBunnyTail(), "@carrotcodes", "bunny_tail"));

        //Ears
        registerPart(PartsData.PartType.EARS, new LegacyPartRenderer("ears.fox", 1, new ModelFoxEars(), "@Adeon", "fox_ears"));
        registerPart(PartsData.PartType.EARS, new LegacyPartRenderer("ears.cat", 0, new ModelCatEars(), null, "cat_ears"));
        registerPart(PartsData.PartType.EARS, new LegacyPartRenderer("ears.panda", 0, new ModelPandaEars(), null, "panda_ears"));
        registerPart(PartsData.PartType.EARS, new LegacyPartRenderer("ears.catSmall", 0, new ModelCatSmallEars(), null, "cat_small_ears"));

        //Wings
        registerPart(PartsData.PartType.WINGS, new RenderWings("wings.big", 1, null, null, "big_wings", "metal_wings", "dragon_wings", "dragon_boneless_wings")
                .setAuthor("@littlechippie").setAuthor("Dracyoshi", 0, 2).setAuthor("Dracyoshi", 0, 3).setAuthor("Dracyoshi", 1, 2).setAuthor("Dracyoshi", 1, 3));

        // Muzzle
        registerPart(PartsData.PartType.MUZZLE, new LegacyPartRenderer("muzzle.standard", 4, new ModelSizableMuzzle(-2f, -3f, -9f, 4, 3, 5), null, "standard_muzzle", "alt_muzzle"));
        registerPart(PartsData.PartType.MUZZLE, new LegacyPartRenderer("muzzle.slim", 4, new ModelSizableMuzzle(-2f, -2f, -9f, 4, 2, 5), null, "standard_muzzle", "alt_muzzle"));
        registerPart(PartsData.PartType.MUZZLE, new LegacyPartRenderer("muzzle.thin", 4, new ModelSizableMuzzle(-1.5f, -2f, -9f, 3, 2, 5, 0, 9), null, "standard_muzzle", "alt_muzzle"));

        try (InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("tails", "parts.json")).getInputStream();){
            Gson gson = new Gson();
            InputStreamReader reader = new InputStreamReader(stream);
            ArrayList<Part> parts = gson.fromJson(reader, new TypeToken<ArrayList<Part>>(){}.getType());

            for (Part part : parts) {
                addPart(part);
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addPart(Part part) {
        parts.put(part.id, part);
    }

    private static void registerPart(PartsData.PartType partType, LegacyPartRenderer renderPart) {
        partRegistry.put(partType, renderPart);
    }

    public static Part getPart(UUID uuid) {
        return parts.get(uuid);
    }

    public static List<LegacyPartRenderer> getParts(PartsData.PartType partType) {
        return partRegistry.get(partType);
    }

    /**
     * Safely gets a render part. By safely, this means it checks if the type id is within bounds of the list for that
     * part type and if not, returns the LegacyPartRenderer associated with type id 0.
     * @param partType The part type
     * @param index The index/type id
     * @return The render part
     */
    public static LegacyPartRenderer getRenderPart(PartsData.PartType partType, int index) {
        List<LegacyPartRenderer> parts = PartRegistry.getParts(partType);
        index = index >= parts.size() ? 0 : index;
        return parts.get(index);
    }
}
