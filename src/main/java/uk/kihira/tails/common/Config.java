package uk.kihira.tails.common;

import net.minecraft.util.StringUtil;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.client.outfit.OutfitPart;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Mod.EventBusSubscriber(modid = Tails.MOD_ID, bus = Bus.MOD)
public final class Config 
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue FORCE_LEGACY_RENDERING = BUILDER
            .comment("Forces the legacy renderer which may have better compatibility with other mods")
            .define("forceLegacyRenderer", false);

    private static final ModConfigSpec.BooleanValue LIBRARY_ENABLED = BUILDER
            .comment("Whether to enable the library system for sharing tails. This mostly matters on servers")
            .define("enableLibrary", true);

    private static final ModConfigSpec.ConfigValue<String> LOCAL_OUTFIT = BUILDER
            .comment("Local Players outfit. Delete to remove all customisation data. Do not try to edit manually")
            .define("localPlayerOutfit", "");

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean forceLegacyRendering;
    public static boolean libraryEnabled;
    public static Outfit localOutfit;

    @SubscribeEvent
    private static void onLoad(final ModConfigEvent event)
    {
        forceLegacyRendering = FORCE_LEGACY_RENDERING.get();
        libraryEnabled = LIBRARY_ENABLED.get();
        localOutfit = StringUtil.isNullOrEmpty(LOCAL_OUTFIT.get()) ? null : Tails.GSON.fromJson(LOCAL_OUTFIT.get(), Outfit.class);

        if (localOutfit == null)
        {
            // todo temp hacks
            try {
                PartRegistry.loadAllPartsFromResources().get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }

            var outfit = new Outfit();
            outfit.parts.add(new OutfitPart(PartRegistry.getPart(UUID.fromString("b783d4b9-dd0e-41bb-8aa3-87efac967c19")).get()));
            Tails.setLocalOutfit(outfit);
        }

        SPEC.save();
    }
}
