package com.twalse.twcinematic.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CinematicConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec.ConfigValue<String> VIDEO_FOLDER_PATH;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("TwCinematic Client Settings").push("client");

        VIDEO_FOLDER_PATH = builder
                .comment("Absolute or relative path to the folder containing MP4 video files.")
                .define("videoFolderPath", "cinematic");

        builder.pop();

        CLIENT_SPEC = builder.build();
    }
}
