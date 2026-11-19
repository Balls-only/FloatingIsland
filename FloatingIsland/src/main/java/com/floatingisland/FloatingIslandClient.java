package com.floatingisland;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class FloatingIslandClient implements ClientModInitializer {
    private static KeyBinding toggleKey;
    private static KeyBinding scaleKey;

    private static boolean islandEnabled = false;
    private static int islandSize = 16;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.floatingisland.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                "category.floatingisland"
        ));

        scaleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.floatingisland.scale",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.floatingisland"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) {
                return;
            }

            while (toggleKey.wasPressed()) {
                islandEnabled = !islandEnabled;
                sendTogglePacket();

                client.player.sendMessage(
                        Text.literal("Floating Island: " + (islandEnabled ? "ON" : "OFF")),
                        true
                );
            }

            while (scaleKey.wasPressed()) {
                islandSize = islandSize == 16 ? 32 : 16;

                if (islandEnabled) {
                    sendTogglePacket();
                }

                client.player.sendMessage(
                        Text.literal("Island Size: " + islandSize + "x" + islandSize),
                        true
                );
            }
        });
    }

    private static void sendTogglePacket() {
        ClientPlayNetworking.send(new ToggleIslandPayload(islandEnabled, islandSize));
    }
}
