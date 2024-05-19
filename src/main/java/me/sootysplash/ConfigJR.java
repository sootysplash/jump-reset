package me.sootysplash;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.autoconfig.ConfigData;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@me.shedaniel.autoconfig.annotation.Config(name = "jump-reset-indicator")
class ConfigJR implements ConfigData {

    //Andy is the goat https://github.com/AndyRusso/pvplegacyutils/blob/main/src/main/java/io/github/andyrusso/pvplegacyutils/PvPLegacyUtilsConfig.java

    private static final Path file = FabricLoader.getInstance().getConfigDir().resolve("jump-reset-indicator.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ConfigJR instance;

    public boolean enabled = true;
    public boolean background = false;
    public int ticks = 10;
    public int x = 300;
    public int y = 200;

    public static ConfigJR getInstance() {
        if (instance == null) {
            try {
                instance = GSON.fromJson(Files.readString(file), ConfigJR.class);
            } catch (IOException exception) {
                JumpResetIndicator.LOGGER.warn("JumpResetIndicator couldn't load the config, using defaults.");
                instance = new ConfigJR();
            }
        }

        return instance;
    }

    public void save() {
        try {
            Files.writeString(file, GSON.toJson(this));
        } catch (IOException e) {
            JumpResetIndicator.LOGGER.error("JumpResetIndicator could not save the config.");
            throw new RuntimeException(e);
        }
    }

}
