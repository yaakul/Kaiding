package me.yaakul.kaiding.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MenuOpener {

    private static boolean pending = false;

    public static void request(){
        pending = true;
    }

    public static void init(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while(KaidingKeybinds.menuKey.consumeClick()){
                pending = true;
            }

            if(pending){
                pending = false;
                client.setScreenAndShow(new KaidingScreen());
            }
        });
    }

}
