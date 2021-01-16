package uk.kihira.tails.common;

import com.google.gson.Gson;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import uk.kihira.tails.client.outfit.Outfit;

public final class Config 
{
    public static BooleanValue forceLegacyRendering;
    public static BooleanValue libraryEnabled;
    public static ConfigValue<Outfit> localOutfit;

    public static ForgeConfigSpec configuration;

    public static void loadConfig() 
    {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        forceLegacyRendering = builder
                .comment("Forces the legacy renderer which may have better compatibility with other mods")
                .define("forceLegacyRenderer", false);

        libraryEnabled = builder
                .comment("Whether to enable the library system for sharing tails. This mostly matters on servers.")
                .define("enableLibrary", true);

        localOutfit = builder
                .comment("Local Players outfit. Delete to remove all customisation data. Do not try to edit manually")
                .define("localPlayerOutfit", new Outfit());
        
        //Load default if none exists
        if (localOutfit == null) 
        {
            Tails.setLocalOutfit(new Outfit());
        }
        // todo
/*        try
        {
            localOutfit = Gson.fromJson(Tails.configuration.getString("Local Player Outfit",
                    Configuration.CATEGORY_GENERAL, "{}", "Local Players outfit. Delete to remove all customisation data. Do not try to edit manually"), Outfit.class);

        } catch (JsonSyntaxException e) {
            Tails.configuration.getCategory(Configuration.CATEGORY_GENERAL).remove("Local Player Data");
            Tails.LOGGER.error("Failed to load local player data: Invalid JSON syntax! Invalid data being removed");
        }*/

        configuration = builder.build();
        //configuration.save();
    }
}
