package uk.kihira.tails.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.kihira.gltf.GltfLoader;
import uk.kihira.gltf.Model;
import uk.kihira.tails.common.Tails;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * A registry for all known parts and models
 * TODO: Support for unloading models
 * TODO: Support for unregistering unused parts to save on memory outside of the editor?
 */
@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class PartRegistry {
    private static final HashMap<UUID, Model> models = new HashMap<>();
    private static final ArrayList<UUID> modelsInProgress = new ArrayList<>(); // Current models being loaded
    private static final HashMap<UUID, Part> parts = new HashMap<>();
    private static final ArrayList<UUID> partsInProgress = new ArrayList<>(); // Current parts being loaded

    private static final String MODEL_CACHE_FOLDER = "tails/cache/model";
    private static final String PARTS_CACHE_FOLDER = "tails/cache/part";

    static {
        loadPart(UUID.fromString("b783d4b9-dd0e-41bb-8aa3-87efac967c19")); // Test model
    }

    public static void loadCache() throws IOException {
        Path cachePath = Paths.get(Minecraft.getMinecraft().mcDataDir.getPath(), PARTS_CACHE_FOLDER);
        if (!Files.exists(cachePath)) {
            Files.createDirectories(cachePath);
            return;
        }
        try (Stream<Path> paths = Files.walk(cachePath)) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                try (FileReader reader = new FileReader(path.toFile())) {
                    addPart(Tails.gson.fromJson(reader, Part.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Gets the {@link Part} for the associated ID, or loads it if it is not yet created.
     * <p>
     * A step-by-step procedure is followed to attempt to load the part as outlined:
     * - If the part has been loaded already, this is returned from {@link PartRegistry#parts}
     * - If the part does not exist in there, it will attempt to load it from the cache directory. This method will
     * return null until it is loaded.
     * - If it does not exist in the cache directory, it will attempt to download it from API server. This will return
     * null until it is loaded
     *
     * @param uuid The ID of the part
     * @return The part if loaded, or null if not
     */
    @Nullable
    public static Part getPart(UUID uuid) {
        if (parts.containsKey(uuid)) {
            return parts.get(uuid);
        }
        if (partsInProgress.contains(uuid)) {
            return null;
        }

        // Attempt to load part from cache folder
        partsInProgress.add(uuid);
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Path path = Paths.get(Minecraft.getMinecraft().mcDataDir.getPath(), PARTS_CACHE_FOLDER, uuid.toString() + ".json");

            if (Files.exists(path)) {
                try (FileReader reader = new FileReader(path.toFile())) {
                    parts.put(uuid, Tails.gson.fromJson(reader, Part.class));
                } catch (IOException e) {
                    Tails.logger.error("Failed to load part: " + uuid, e);
                } finally {
                    partsInProgress.remove(uuid);
                }
            } else {
                // todo download from API server. also track if download from API server failed and don't retry
            }
        });
        return null;
    }

    /**
     * Gets a {@link Stream} of Parts whose {@link Part#mountPoint} equals the specified {@link MountPoint}
     *
     * @param mountPoint The mount point to filter by
     * @return A stream of parts
     */
    public static Stream<Part> getPartsByMountPoint(MountPoint mountPoint) {
        return parts.values().stream().filter(part -> part.mountPoint == mountPoint);
    }

    /**
     * Gets the {@link Model} for the associated ID, or loads it if it is not yet created.
     * <p>
     * A step-by-step procedure is followed to attempt to load the model as outlined:
     * - If the model has been loaded already, this is returned from {@link PartRegistry#models}
     * - If the model does not exist in there, it will attempt to load it from the cache directory. This method will
     * return null until it is loaded.
     * - If it does not exist in the cache directory, it will attempt to download it from API server. This will return
     * null until it is loaded
     *
     * @param uuid The ID of the model
     * @return The model if loaded, or null if not
     */
    @Nullable
    public static Model getModel(final UUID uuid) {
        if (models.containsKey(uuid)) {
            return models.get(uuid);
        }
        if (modelsInProgress.contains(uuid)) {
            return null;
        }

        // Attempt to load model from file cache first, then try to download it
        modelsInProgress.add(uuid);
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Path path = Paths.get(Minecraft.getMinecraft().mcDataDir.getPath(), MODEL_CACHE_FOLDER, uuid.toString() + ".glb");

            if (Files.exists(path)) {
                try {
                    models.put(uuid, GltfLoader.LoadGlbFile(path.toFile()));
                } catch (IOException e) {
                    Tails.logger.error("Failed to load model: " + uuid, e);
                } finally {
                    modelsInProgress.remove(uuid);
                }
            } else {
                // todo download from API server. also track if download from API server failed and don't retry
            }
        });
        return null;
    }

    /**
     * Loads a part from the resources folder. Then saves the
     *
     * @param uuid Part ID
     */
    private static void loadPart(UUID uuid) {
        ResourceLocation resLoc = new ResourceLocation(Tails.MOD_ID, "part/" + uuid + ".json");
        try (InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(resLoc).getInputStream()) {
            InputStreamReader reader = new InputStreamReader(is);
            addPart(Tails.gson.fromJson(reader, Part.class));
        } catch (IOException e) {
            Tails.logger.error("Failed to load part: " + uuid, e);
        }
    }

    private static void addPart(Part part) {
        parts.put(part.id, part);
    }
}
