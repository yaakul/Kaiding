package me.yaakul.kaiding.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class KaidingKeybinds {

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(KaidingClient.MOD_ID, "main")
    );

    public static KeyMapping toggleKey;
    public static KeyMapping menuKey;

    public static void init(){
        toggleKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.kaiding.toggle",
                        InputConstants.Type.KEYBOARD,
                        InputConstants.KEY_J,
                        CATEGORY
                )
        );

        menuKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.kaiding.menu",
                        InputConstants.Type.KEYBOARD,
                        InputConstants.KEY_K,
                        CATEGORY
                )
        );
    }
}
