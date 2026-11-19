package me.yaakul.kaiding.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("kaiding.json");

    private static class Data{
        double minCps = 8.0;
        double maxCps = 12.0;
        boolean randomize = true;
        boolean holdMode = false;
        ClickerState.ClickButton button = ClickerState.ClickButton.LEFT;
        //long totalClicks = 0;
        boolean humanize = true;
        boolean autoSprint = false;

    }

    public static void load(){
        if(!Files.exists(PATH)){
            save();
            System.out.println("[Kaiding] Config Load");
            return;
        }

        try(Reader reader = Files.newBufferedReader(PATH)){
            Data data = GSON.fromJson(reader, Data.class);
            if(data == null) return;

            //double min = clamp(data.minCps);
            //double max = clamp(data.maxCps);
            ClickerState.randomize = data.randomize;
            ClickerState.holdMode = data.holdMode;
            ClickerState.button = data.button != null ? data.button : ClickerState.ClickButton.LEFT;
            //ClickerState.totalClicks = Math.max(0, data.totalClicks);
            ClickerState.humanize = data.humanize;
            ClickerState.autoSprint = data.autoSprint;
        }
        catch(Exception e){
            System.err.println("[Kaiding] Could not read config, using defaults: " + e);
        }
    }

    public static void save(){
        Data data = new Data();
        data.minCps = ClickerState.minCps;
        data.maxCps = ClickerState.maxCps;
        data.randomize = ClickerState.randomize;
        data.holdMode = ClickerState.holdMode;
        data.button = ClickerState.button;
        //data.totalClicks = ClickerState.totalClicks;
        data.humanize = ClickerState.humanize;
        data.autoSprint = ClickerState.autoSprint;

        try{
            Files.writeString(PATH, GSON.toJson(data));
        } catch (IOException e) {
            System.err.println("[Kaiding] Could not save config: " + e);
        }
    }

    private static double clamp(double v){
        return Math.clamp(v, ClickerState.LIMIT_MIN, ClickerState.LIMIT_MAX);
    }
}
