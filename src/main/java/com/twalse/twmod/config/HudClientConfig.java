package com.twalse.twmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twalse.twmod.TwMod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class HudClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FMLPaths.CONFIGDIR.get().toFile(), "twmod-client.json");

    public int hudX = 10;
    public int hudY = 10;
    public float hudScale = 1.0f;
    public int textColor = 0xFFFFFF;
    public float bgOpacity = 0.5f;

    private static HudClientConfig instance = new HudClientConfig();

    public static HudClientConfig get() {
        if (instance == null) {
            instance = new HudClientConfig();
        }
        return instance;
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                HudClientConfig loaded = GSON.fromJson(reader, HudClientConfig.class);
                if (loaded != null) {
                    instance = loaded;
                }
            } catch (Exception e) {
                TwMod.LOGGER.error("Failed to load twmod-client.json config", e);
            }
        } else {
            save();
        }
    }

    public static void save() {
        try {
            File dir = CONFIG_FILE.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(get(), writer);
            }
        } catch (Exception e) {
            TwMod.LOGGER.error("Failed to save twmod-client.json config", e);
        }
    }
}
