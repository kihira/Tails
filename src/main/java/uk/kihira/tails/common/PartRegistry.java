package uk.kihira.tails.common;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.Part;
import uk.kihira.tails.client.model.ears.ModelCatEars;
import uk.kihira.tails.client.model.ears.ModelCatSmallEars;
import uk.kihira.tails.client.model.ears.ModelFoxEars;
import uk.kihira.tails.client.model.ears.ModelPandaEars;
import uk.kihira.tails.client.model.tail.*;
import uk.kihira.tails.client.render.LegacyPartRenderer;
import uk.kihira.tails.client.render.RenderWings;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//Yeah using side only isn't nice but as this is static, it means it only gets constructed on the client
@SideOnly(Side.CLIENT)
public class PartRegistry {
    private static final HashMap<UUID, LegacyPartRenderer> partRenderers = new HashMap<>();

    private static final HashMap<UUID, Part> parts = new HashMap<>();

    static {
        // todo need to rename texture ids?
        // Tails
        addPart(new Part(UUID.fromString("9d3aee5c-82c0-4474-90a2-301435b4ddc3"), "Fluffy Tail", "Kihira",
                MountPoint.CHEST, new int[]{0, 0, 0}, new UUID[]{UUID.fromString("9d3aee5c-82c0-4474-90a2-301435b4ddc3")}), new LegacyPartRenderer(new ModelFluffyTail(0)));
        addPart(new Part(UUID.fromString("21c3fe75-2c9f-4702-b7e8-26a748f01d8a"), "Twin Tails", "Kihira",
                MountPoint.CHEST, new int[]{0, 0, 0}, new UUID[]{UUID.fromString("21c3fe75-2c9f-4702-b7e8-26a748f01d8a")}), new LegacyPartRenderer(new ModelFluffyTail(1)));
        addPart(new Part(UUID.fromString("65112ab3-58ce-498b-aa90-8c1f14f60a52"), "Nine Tails", "Kihira",
                MountPoint.CHEST, new int[]{0, 0, 0}, new UUID[]{UUID.fromString("65112ab3-58ce-498b-aa90-8c1f14f60a52")}), new LegacyPartRenderer(new ModelFluffyTail(2)));

        addPart(new Part(UUID.fromString("820fff32-e420-45f0-a626-8dfef2d0a2c4"), "Lizard Tail", "", MountPoint.CHEST,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("820fff32-e420-45f0-a626-8dfef2d0a2c4")}), new LegacyPartRenderer(new ModelDragonTail(0)));
        addPart(new Part(UUID.fromString("42db3167-aa40-4d9d-bf22-68944ef65cda"), "Dragon Tail", "", MountPoint.CHEST,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("42db3167-aa40-4d9d-bf22-68944ef65cda")}), new LegacyPartRenderer(new ModelDragonTail(1)));
        addPart(new Part(UUID.fromString("b119ff1e-d8ed-4454-a77f-141916e77ebe"), "Raccoon Tail", "", MountPoint.CHEST,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("b119ff1e-d8ed-4454-a77f-141916e77ebe")}), new LegacyPartRenderer(new ModelRaccoonTail()));
        addPart(new Part(UUID.fromString("91f5599a-8be5-4faf-bfbb-0b0bc39489f9"), "Cat Tail", "", MountPoint.CHEST,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("91f5599a-8be5-4faf-bfbb-0b0bc39489f9")}), new LegacyPartRenderer(new ModelDevilTail(0)));
        addPart(new Part(UUID.fromString("833c8546-79e9-434f-9938-a9b4082fbd22"), "Devil Tail", "", MountPoint.CHEST,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("833c8546-79e9-434f-9938-a9b4082fbd22")}), new LegacyPartRenderer(new ModelDevilTail(1)));
        addPart(new Part(UUID.fromString("2f7994c0-a790-4d57-ad59-ed43f72802ec"), "Cat Tail", "", MountPoint.CHEST,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("2f7994c0-a790-4d57-ad59-ed43f72802ec")}), new LegacyPartRenderer(new ModelCatTail()));
        addPart(new Part(UUID.fromString("eb096196-13de-45e1-a2bf-0730882af5a5"), "Bird Tail", "BluSunrize", MountPoint.CHEST,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("eb096196-13de-45e1-a2bf-0730882af5a5")}), new LegacyPartRenderer(new ModelBirdTail()));
        addPart(new Part(UUID.fromString("c371a5c1-7f08-4b3a-974b-abf8bc639cd1"), "Shark Tail", "access_denied", MountPoint.CHEST,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("c371a5c1-7f08-4b3a-974b-abf8bc639cd1")}), new LegacyPartRenderer(new ModelSharkTail()));
        addPart(new Part(UUID.fromString("0218233b-3375-40d1-b47b-ed5fd9c34706"), "Rabbit Tail", "", MountPoint.CHEST,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("0218233b-3375-40d1-b47b-ed5fd9c34706")}), new LegacyPartRenderer(new ModelBunnyTail()));

        //Ears
        addPart(new Part(UUID.fromString("7ee6ceb1-bcbf-44f3-98a5-969094f7f1d6"), "Fox Ears", "Adeon", MountPoint.HEAD,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("7ee6ceb1-bcbf-44f3-98a5-969094f7f1d6")}), new LegacyPartRenderer(new ModelFoxEars(0)));
        addPart(new Part(UUID.fromString("4721ab6b-aaf6-4f56-9b2d-c5feb019b430"), "Alternate Fox Ears", "", MountPoint.HEAD,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("4721ab6b-aaf6-4f56-9b2d-c5feb019b430")}), new LegacyPartRenderer(new ModelFoxEars(1)));
        addPart(new Part(UUID.fromString("bb6c78e9-41ae-4d79-a744-72bc18d6ccc7"), "Feline Ears", "Kihira", MountPoint.HEAD,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("bb6c78e9-41ae-4d79-a744-72bc18d6ccc7")}), new LegacyPartRenderer(new ModelCatEars()));
        addPart(new Part(UUID.fromString("c4a717d7-11ff-493b-804d-938c53e25ba4"), "Panda Ears", "Kihira", MountPoint.HEAD,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("c4a717d7-11ff-493b-804d-938c53e25ba4")}), new LegacyPartRenderer(new ModelPandaEars()));
        addPart(new Part(UUID.fromString("ec0fc665-8a23-44bc-8cbe-1189c5a49b85"), "Small Feline Ears", "Kihira", MountPoint.HEAD,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("ec0fc665-8a23-44bc-8cbe-1189c5a49b85")}), new LegacyPartRenderer(new ModelCatSmallEars()));

        //Wings
        RenderWings renderWings = new RenderWings();
        addPart(new Part(UUID.fromString("824d9b80-fbfe-46e7-9cc4-2fcbffe6d923"), "Wings", "", MountPoint.CHEST,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("824d9b80-fbfe-46e7-9cc4-2fcbffe6d923")}), renderWings);
        addPart(new Part(UUID.fromString("10f9b4af-ed69-4d10-8ee7-0e45672ccd60"), "Metal Wings", "", MountPoint.CHEST,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("10f9b4af-ed69-4d10-8ee7-0e45672ccd60")}), renderWings);
        addPart(new Part(UUID.fromString("71fb85c5-f406-43f9-99cf-eca88708a970"), "Dragon Wings", "", MountPoint.CHEST,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("71fb85c5-f406-43f9-99cf-eca88708a970")}), renderWings);
        addPart(new Part(UUID.fromString("9732b227-62cf-426f-9316-23cfdc4abcdb"), "Dragon Boneless Wings", "", MountPoint.CHEST,
                new int[]{0, 0, 0}, new UUID[]{UUID.fromString("9732b227-62cf-426f-9316-23cfdc4abcdb")}), renderWings);

        // Muzzle
        // todo add in a part for each length of muzzle or show a tip instead saying the player can adjust the offset?
        //addPart(new Part(UUID.fromString("c80c2f56-c8fe-4697-bba8-e32e4070361e")), new LegacyPartRenderer(new ModelSizableMuzzle()));
        //addPart(new Part(UUID.fromString("6ec3bb8d-19b5-457e-af5f-6946ce2c2401")), new LegacyPartRenderer(new ModelSizableMuzzle()));
        //addPart(new Part(UUID.fromString("38676637-4203-49fe-a0b9-b9652dfac40a")), new LegacyPartRenderer(new ModelSizableMuzzle()));

    }

    private static void addPart(Part part, LegacyPartRenderer renderPart) {
        parts.put(part.id, part);
        partRenderers.put(part.id, renderPart);
    }

    public static Part getPart(UUID uuid) {
        return parts.get(uuid);
    }

    public static List<Part> getPartsByMountPoint(MountPoint mountPoint) {
        return parts.values().stream().filter(part -> part.mountPoint == mountPoint).collect(Collectors.toList());
    }

    public static LegacyPartRenderer getRenderer(UUID uuid) {
        return partRenderers.get(uuid);
    }
}
