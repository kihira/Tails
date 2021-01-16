package uk.kihira.tails.client.gui;

import uk.kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;

import java.util.Set;

public class TailsGuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {}

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new TailsConfigGUI(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    public static class TailsConfigGUI extends GuiConfig {

        public TailsConfigGUI(GuiScreen parentScreen) {
            super(parentScreen, new ConfigElement(Tails.configuration.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                    Tails.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(Tails.configuration.toString()));
        }
    }
}
