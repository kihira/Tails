package uk.kihira.tails.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.IModGuiFactory;
import uk.kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Set;

@OnlyIn(Dist.CLIENT)
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
