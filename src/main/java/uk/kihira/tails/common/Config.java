package uk.kihira.tails.common;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import uk.kihira.tails.Tails;
import uk.kihira.tails.client.outfit.Outfit;

public final class Config
{
    public static final Config CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public ModConfigSpec.ConfigValue<Boolean> forceLegacyRendering;
    public ModConfigSpec.ConfigValue<Boolean> libraryEnabled;
    private ModConfigSpec.ConfigValue<String> localOutfit;

    private Config(ModConfigSpec.Builder builder)
    {
        forceLegacyRendering = builder
                .comment("Forces the legacy renderer which may have better compatibility with other mods")
                .gameRestart()
                .define("forceLegacyRenderer", false);
        libraryEnabled = builder
                .comment("Whether to enable the library system for sharing tails. This mostly matters on servers")
                .gameRestart()
                .define("enableLibrary", true);
        localOutfit = builder.comment("Local Players outfit. Delete to remove all customisation data. Do not try to edit manually").define("localPlayerOutfit", "");
    }

    public Outfit getLocalOutfit()
    {
        return Tails.GSON.fromJson(localOutfit.get(), Outfit.class);
    }

    public void setLocalOutfit(Outfit outfit)
    {
        localOutfit.set(Tails.GSON.toJson(outfit));
        localOutfit.save();
    }

    static
    {
        Pair<Config, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(Config::new);

        //Store the resulting values
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
