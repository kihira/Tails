package uk.kihira.tails.common;

import uk.kihira.tails.client.MountPoint;
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

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

//Yeah using side only isn't nice but as this is static, it means it only gets constructed on the client
@SideOnly(Side.CLIENT)
public class PartRegistry {
    private static final HashMap<UUID, LegacyPartRenderer> partRenderers = new HashMap<>();

    private static final HashMap<UUID, Part> parts = new HashMap<>();

    static {
        // Tails
        addPart(new Part(UUID.fromString("9d3aee5c-82c0-4474-90a2-301435b4ddc3"), "Fluffy Tail", "Kihira", MountPoint.CHEST, new int[]{0, 0, 0}), new LegacyPartRenderer(new ModelFluffyTail()));
        addPart(new Part(UUID.fromString("820fff32-e420-45f0-a626-8dfef2d0a2c4"), "Dragon Tail", "", MountPoint.CHEST, new int[]{0, 0, 0}), new LegacyPartRenderer(new ModelDragonTail()));
        addPart(new Part(UUID.fromString("b119ff1e-d8ed-4454-a77f-141916e77ebe"), "Raccoon Tail", "", MountPoint.CHEST, new int[]{0, 0, 0}), new LegacyPartRenderer(new ModelRaccoonTail()));
        addPart(new Part(UUID.fromString("91f5599a-8be5-4faf-bfbb-0b0bc39489f9"), "Devil Tail", "", MountPoint.CHEST, new int[]{0, 0, 0}), new LegacyPartRenderer(new ModelDevilTail()));
        addPart(new Part(UUID.fromString("2f7994c0-a790-4d57-ad59-ed43f72802ec"), "Cat Tail", "", MountPoint.CHEST, new int[]{0, 0, 0}), new LegacyPartRenderer(new ModelCatTail()));
        addPart(new Part(UUID.fromString("eb096196-13de-45e1-a2bf-0730882af5a5"), "Bird Tail", "BluSunrize", MountPoint.CHEST, new int[]{0, 0, 0}), new LegacyPartRenderer(new ModelBirdTail()));
        addPart(new Part(UUID.fromString("c371a5c1-7f08-4b3a-974b-abf8bc639cd1"), "Shark Tail", "access_denied", MountPoint.CHEST, new int[]{0, 0, 0}), new LegacyPartRenderer(new ModelSharkTail()));
        addPart(new Part(UUID.fromString("0218233b-3375-40d1-b47b-ed5fd9c34706"), "Rabbit Tail", "", MountPoint.CHEST, new int[]{0, 0, 0}), new LegacyPartRenderer(new ModelBunnyTail()));

        //Ears
        addPart(new Part(UUID.fromString("7ee6ceb1-bcbf-44f3-98a5-969094f7f1d6"), "Fox Ears", "Adeon", MountPoint.HEAD, new int[]{0, 0, 0}), new LegacyPartRenderer(new ModelFoxEars()));
        addPart(new Part(UUID.fromString("bb6c78e9-41ae-4d79-a744-72bc18d6ccc7"), "Feline Ears", "Kihira", MountPoint.HEAD, new int[]{0, 0, 0}), new LegacyPartRenderer(new ModelCatEars()));
        addPart(new Part(UUID.fromString("c4a717d7-11ff-493b-804d-938c53e25ba4"), "Panda Ears", "Kihira", MountPoint.HEAD, new int[]{0, 0, 0}), new LegacyPartRenderer(new ModelPandaEars()));
        addPart(new Part(UUID.fromString("ec0fc665-8a23-44bc-8cbe-1189c5a49b85"), "Small Feline Ears", "Kihira", MountPoint.HEAD, new int[]{0, 0, 0}), new LegacyPartRenderer(new ModelCatSmallEars()));

        //Wings
        addPart(new Part(UUID.fromString("824d9b80-fbfe-46e7-9cc4-2fcbffe6d923"), "Wings", "", MountPoint.CHEST, new int[]{0, 0, 0}), new LegacyPartRenderer(new RenderWings()));

        // Muzzle
        addPart(new Part(UUID.fromString("c80c2f56-c8fe-4697-bba8-e32e4070361e")), new LegacyPartRenderer(new ModelSizableMuzzle()));
        addPart(new Part(UUID.fromString("6ec3bb8d-19b5-457e-af5f-6946ce2c2401")), new LegacyPartRenderer(new ModelSizableMuzzle()));
        addPart(new Part(UUID.fromString("38676637-4203-49fe-a0b9-b9652dfac40a")), new LegacyPartRenderer(new ModelSizableMuzzle()));

    }

    private static void addPart(Part part, LegacyPartRenderer renderPart) {
        parts.put(part.id, part);
        partRenderers.put(part.id, renderPart);
    }

    public static Part getPart(UUID uuid) {
        return parts.get(uuid);
    }

    public static LegacyPartRenderer getRenderer(UUID uuid) {
        return partRenderers.get(uuid);
    }
}
