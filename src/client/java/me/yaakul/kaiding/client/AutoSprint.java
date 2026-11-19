package me.yaakul.kaiding.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;

public class AutoSprint {
    private static boolean wasActive = false;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeyMapping sprintKey = client.options.keySprint;

            boolean active = ClickerState.autoSprint
                    && client.player != null
                    && client.level != null;

            if (active) {
                if (!sprintKey.isDown()) {
                    sprintKey.setDown(true);
                }
            } else if (wasActive && sprintKey.isDown()) {
                sprintKey.setDown(false); // let go when the feature is switched off
            }

            wasActive = active;
        });
    }
}