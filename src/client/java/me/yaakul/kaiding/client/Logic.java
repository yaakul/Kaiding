package me.yaakul.kaiding.client;

import com.mojang.blaze3d.platform.InputConstants;
import me.yaakul.kaiding.mixin.client.MinecraftAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

public class Logic {

    private static double ticksUntilClick = 0.0;
    //private static int saveTimer = 0;
    //private static long lastSavedClicks = 0;

    public static void init(){

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while(KaidingKeybinds.toggleKey.consumeClick()){
                if(!ClickerState.holdMode){ // Check if hold is toggled
                    ClickerState.toggle();
                    ticksUntilClick = 0.0;
                    if(client.player != null){
                        client.player.sendSystemMessage(Component.literal("Clicker: " + (ClickerState.enabled ? "ON" : "OFF")));
                    }
                }
            }

            boolean active = ClickerState.holdMode
                    ? KaidingKeybinds.toggleKey.isDown()
                    : ClickerState.enabled;

            // Menu check
            if(!active || client.player == null || client.level == null || !client.mouseHandler.isMouseGrabbed()){
                ticksUntilClick = 0.0;
                return;
            }

            ticksUntilClick -= 1.0;
            while(ticksUntilClick <= 0.0){
                ((MinecraftAccessor) client).kaiding$setMissTime(0);
                KeyMapping.click(InputConstants.Type.MOUSE.getOrCreate(ClickerState.button.inputButton));
                ClickerState.sessionClicks++;
                ticksUntilClick += 20.0 / ClickerState.nextCps();
            }
        });
    }
}
