package me.yaakul.kaiding.client;

import com.mojang.blaze3d.platform.InputConstants;

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


