/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import uk.kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

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
    @Deprecated
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return null;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    @Deprecated
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

    public static class TailsConfigGUI extends GuiConfig {

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public TailsConfigGUI(GuiScreen parentScreen) {
            super(parentScreen, new ConfigElement(Tails.configuration.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                    Tails.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(Tails.configuration.toString()));
        }
    }
}
