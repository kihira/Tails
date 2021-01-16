package uk.kihira.tails.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.kihira.gltf.GltfLoader;
import uk.kihira.gltf.Model;
import uk.kihira.tails.common.Tails;

import com.google.gson.reflect.TypeToken;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

/**
 * A registry for all known parts and models
 * TODO: Support for unloading models
 * TODO: Support for unregistering unused parts to save on memory outside of the editor?
 */
public final class PartRegistry
{
    private static final Logger LOGGER = LogManager.getFormatterLogger();
    private static final String MODEL_CACHE_FOLDER = "tails/cache/model";
    private static final String PARTS_CACHE_FOLDER = "tails/cache/part";

    private static final LazyLoadAssetRegistry<UUID, Part> parts = new LazyLoadAssetRegistry<>(LOGGER, PartRegistry::loadPart, null, null);
    private static final LazyLoadAssetRegistry<UUID, Model> models = new LazyLoadAssetRegistry<>(LOGGER, PartRegistry::loadModel, null, null);

    /**
     * Loads the cache from disk locally 
     * @throws IOException
     */
    public static void loadAllPartsFromCache() throws IOException
    {
        Path cachePath = Paths.get(Minecraft.getInstance().gameDir.getPath(), PARTS_CACHE_FOLDER);
        try (Stream<Path> paths = Files.walk(cachePath))
        {
            paths.filter(Files::isRegularFile).forEach(path ->
            {
                try (FileReader reader = new FileReader(path.toFile()))
                {
                    addPart(Tails.GSON.fromJson(reader, Part.class));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
        }
    }

    private static void initCache()
    {
        String gameDir = Minecraft.getInstance().gameDir.getPath();
        Path partCachePath = Paths.get(gameDir, PARTS_CACHE_FOLDER);
        Path modelCachePath = Paths.get(gameDir, MODEL_CACHE_FOLDER);

        try
        {
            if (!Files.exists(partCachePath))
            {
                Files.createDirectories(partCachePath);
            }
            if (!Files.exists(modelCachePath))
            {
                Files.createDirectories(modelCachePath);
            }
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to create cache directories", e);
        }
    }

    /**
     * Gets the {@link Part} for the associated ID, or loads it if it is not yet created.
     * <p>
     * A step-by-step procedure is followed to attempt to load the part as outlined:
     * - If the part has been loaded already, this is returned from {@link PartRegistry#parts}
     * - If the part does not exist in there, it will attempt to load it from the cache directory. This method will
     * return an empty optional until it is loaded.
     * - If it does not exist in the cache directory, it will attempt to download it from API server. This will return
     * an empty optional until it is loaded
     *
     * @param uuid The ID of the part
     * @return The part if loaded, or empty Optional
     */
    public static Optional<Part> getPart(final UUID uuid)
    {
        return parts.get(uuid);
    }

    private static CompletableFuture<Part> loadPart(final UUID partId)
    {
        return loadPartFromCache(partId)
                .thenCompose(part -> part
                        .map(CompletableFuture::completedFuture)
                        .orElseGet(() -> loadPartFromApiAndSaveToCache(partId)))
                .exceptionally(ex ->
                {
                    LOGGER.error("Failed to load part " + partId.toString(), ex);
                    return null;
                });
    }

    private static CompletableFuture<Part> loadPartFromApiAndSaveToCache(final UUID partId)
    {
        // TODO Implement call to API
        return CompletableFuture.completedFuture(Optional.empty())
                .thenApply(optionalPart ->
                {
                    if (optionalPart.isPresent())
                    {
                        savePartToCache((Part) optionalPart.get());
                        return (Part) optionalPart.get();
                    }
                    return null;
                });
    }

    /**
     * Asynchronously loads a part from the cache if it exists, and populates an Optional object with it
     * If it does not exist or it fails to load, returns an empty Optional.
     * @param partId The ID of the part to load
     * @return A CompletableFuture that provides the part
     */
    private static CompletableFuture<Optional<Part>> loadPartFromCache(final UUID partId)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            Path path = Paths.get(Minecraft.getInstance().gameDir.getPath(), PARTS_CACHE_FOLDER, partId.toString() + ".json");

            if (Files.exists(path))
            {
                try (FileReader reader = new FileReader(path.toFile()))
                {
                    return Optional.ofNullable(Tails.GSON.fromJson(reader, Part.class));
                }
                catch (IOException e)
                {
                    LOGGER.error("Failed to load part " + partId + " from cache", e);
                }
            }
            return Optional.empty();
        });
    }

    /**
     * Saves a {@link Part} to the cache folder
     * @param part The part to save
     */
    private static void savePartToCache(@Nonnull Part part)
    {
        Path path = Paths.get(Minecraft.getInstance().gameDir.getPath(), PARTS_CACHE_FOLDER, part.id.toString() + ".json");
        try (FileWriter writer = new FileWriter(path.toFile()))
        {
            IOUtils.write(Tails.GSON.toJson(part), writer);
        }
        catch (IOException e)
        {
            LOGGER.error(String.format("Failed to save part %s (%s) to cache", part.name, part.id), e);
        }
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
    public static Optional<Model> getModel(final UUID uuid)
    {
        return models.get(uuid);
    }

    private static CompletableFuture<Model> loadModel(final UUID uuid)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            Path path = Paths.get(Minecraft.getInstance().gameDir.getPath(), MODEL_CACHE_FOLDER, uuid.toString() + ".glb");

            if (Files.exists(path))
            {
                try {
                    // todo seperate loading and OpenGL calls
                    return GltfLoader.LoadGlbFile(path.toFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                // todo download from API server. also track if download from API server failed and don't retry
                return null;
            }
            return null;
        })
        .exceptionally(ex ->
        {
            LOGGER.error("Failed to load model " + uuid.toString(), ex);
            return null;
        });
    }

    /**
     * Loads all parts from the resources directory into the cache and memory
     */
    public static void loadAllPartsFromResources()
    {
        ResourceLocation resLoc = new ResourceLocation(Tails.MOD_ID, "parts.json");
        try (InputStream is = Minecraft.getInstance().getResourceManager().getResource(resLoc).getInputStream())
        {
            InputStreamReader reader = new InputStreamReader(is);
            List<UUID> parts = Tails.GSON.fromJson(reader, new TypeToken<List<UUID>>(){}.getType());

            CompletableFuture.allOf(parts.stream()
                    .map(uuid -> CompletableFuture.runAsync(() -> PartRegistry.loadPartFromResources(uuid)))
                    .toArray(CompletableFuture[]::new))
                    .thenRun(() -> LOGGER.info("Loaded %d parts from resources", parts.size()));
        }
        catch (IOException e)
        {
            Tails.LOGGER.error("Cannot load parts list", e);
        }
    }

    /**
     * Loads a part from the resources folder and copies it model
     *
     * @param uuid Part ID
     */
    private static void loadPartFromResources(final UUID uuid)
    {
        IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        ResourceLocation resLoc = new ResourceLocation(Tails.MOD_ID, "part/" + uuid + ".json");
        try (InputStream is = resourceManager.getResource(resLoc).getInputStream())
        {
            InputStreamReader reader = new InputStreamReader(is);
            addPart(Tails.GSON.fromJson(reader, Part.class));

            // Copy model to cache
            ResourceLocation modelResLoc = new ResourceLocation(Tails.MOD_ID, "model/" + uuid + ".glb");
            Path path = Paths.get(Minecraft.getInstance().gameDir.getPath(), MODEL_CACHE_FOLDER, uuid.toString() + ".glb");
            try (InputStream modelStream = resourceManager.getResource(modelResLoc).getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(path.toFile()))
            {
                IOUtils.copy(modelStream, outputStream);
            }
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to load part: " + uuid, e);
        }
    }

    private static void addPart(Part part)
    {
        parts.put(part.id, part);
    }
}
