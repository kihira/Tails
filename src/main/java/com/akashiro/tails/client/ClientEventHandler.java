package com.akashiro.tails.client;

import com.akashiro.tails.client.gui.GuiTailsEditor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class ClientEventHandler {

    public static final KeyMapping KEY_OPEN_EDITOR = new KeyMapping(
            "key.tails.openEditor",
            GLFW.GLFW_KEY_U,
            "key.categories.tails"
    );

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KEY_OPEN_EDITOR);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        if (KEY_OPEN_EDITOR.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.screen == null) {
                mc.setScreen(new GuiTailsEditor());
            }
        }
    }
}
