package com.yurei.ends;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class Ends implements ClientModInitializer {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    
    public static boolean aimAssistEnabled = true;
    public static boolean autoGappleEnabled = true;
    public static boolean shieldBreakerEnabled = true;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                if (autoGappleEnabled) AutoGapple.tick();
                if (shieldBreakerEnabled) ShieldBreaker.tick();
                if (aimAssistEnabled) AimAssist.tick();
            }
        });
        
        HudRenderCallback.EVENT.register(new Hud());
    }
}
