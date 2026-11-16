package me.yaakul.kaiding.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class KaidingClient implements ClientModInitializer {

    public static final String MOD_ID = "kaiding";

    @Override
    public void onInitializeClient() {
        ConfigManager.load();
        KaidingCommand.register();
        MenuOpener.init();
        KaidingKeybinds.init();
        Logic.init();

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.save());
    }
}
