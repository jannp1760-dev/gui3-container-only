package de.gui3;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class Gui3Client implements ClientModInitializer {
    private static KeyMapping openSettingsKey;

    @Override
    public void onInitializeClient() {
        Gui3Config.load();

        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("gui3container", "main")
        );

        openSettingsKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.gui3container.open_settings",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openSettingsKey.consumeClick()) {
                if (!(client.screen instanceof Gui3SettingsScreen)) {
                    client.setScreen(new Gui3SettingsScreen(client.screen));
                }
            }
        });
    }
}
