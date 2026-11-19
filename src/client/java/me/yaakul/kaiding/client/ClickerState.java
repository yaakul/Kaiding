package me.yaakul.kaiding.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ClickerState {

    public enum ClickButton{
        LEFT(InputConstants.MOUSE_BUTTON_LEFT, "Left"),
        RIGHT(InputConstants.MOUSE_BUTTON_RIGHT, "Right");

        public final int inputButton;
        public final String label;

        ClickButton(int inputButton, String label){
            this.inputButton = inputButton;
            this.label = label;
        }

        public ClickButton next(){
            ClickButton[] all = values();
            return all[(ordinal() + 1) % all.length];
        }
    }


    public static final double LIMIT_MIN = 1.0;
    public static final double LIMIT_MAX = 20.0;

    public static ClickButton button = ClickButton.LEFT;
    public static boolean autoSprint = false;
    // public static long totalClicks = 0;
    public static long sessionClicks = 0;
    public static boolean enabled = false;
    public static double minCps = 8.0;
    public static double maxCps = 12.0;
    public static boolean randomize = true;
    public static boolean holdMode = false;
    public static boolean humanize = true;

    // fields
    public static boolean itemFilter = false;
    public static final Set<String> registeredItems = new LinkedHashSet<>();

    // The registry ID of an item, e.g. "minecraft:diamond_sword". An empty hand is "minecraft:air"
    public static String itemId(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    // Add the item if it isn't registered, remove it if it is
    public static void toggleRegistered(ItemStack stack) {
        String id = itemId(stack);
        if (!registeredItems.remove(id)) {
            registeredItems.add(id);
        }
    }

    /** Is the clicker allowed to run with whatever the player is holding? */
    public static boolean heldItemAllowed(Player player) {
        if (!itemFilter) return true;
        return registeredItems.contains(itemId(player.getMainHandItem()));
    }

    public static void toggle(){
        enabled = !enabled;
    }

    public static void setMinCps(double v){
        minCps = Math.min(v, maxCps);
    }

    public static void setMaxCps(double v){
        maxCps = Math.max(v, minCps);
    }

    public static double randomCps(){
        if(maxCps <= minCps) return minCps;
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        if(!humanize){
            return rng.nextDouble(minCps, maxCps);
        }

        double mean = (minCps + maxCps) / 2.0;
        double sigma = (maxCps - minCps) / 6.0;
        double value = mean + sigma * rng.nextGaussian();
        return Math.clamp(value, minCps, maxCps);
    }

    public static double nextCps(){
        return randomize ? randomCps() : maxCps;
    }
}


