package me.yaakul.kaiding.client;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;

public class KaidingCommand  {

    public static void register(){
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, buildContext) ->
                dispatcher.register(
                        ClientCommands.literal("kaiding").executes(context -> {
                            MenuOpener.request();
                            return 1;
                        })
                )));
    }
}
