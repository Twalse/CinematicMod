package com.twalse.twmod.util;

import com.twalse.twmod.TwMod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static FileManager instance;
    private File dir;

    public FileManager(File dir) {
        instance = this;
        this.dir = dir;
        try {
            this.init();
        } catch (IOException e) {
            TwMod.LOGGER.error("Failed to initialize cinematic directory", e);
        }
    }

    public void setDirectory(File dir) {
        this.dir = dir;
        try {
            this.init();
        } catch (IOException e) {
            TwMod.LOGGER.error("Failed to set cinematic directory", e);
        }
    }

    public Video getVideoFromName(String name) {
        File[] files = this.getFiles();
        if (files == null) {
            return null;
        }
        for (File file : files) {
            if (name.equalsIgnoreCase(file.getName())) {
                return getVideoFromFile(file);
            }
        }
        return null;
    }

    public static Video getVideoFromFile(File file) {
        return new Video(file.getAbsolutePath(), file.getName());
    }

    public Video[] getAllVideos() {
        File[] files = this.getFiles();
        if (files == null) return new Video[0];
        List<Video> videos = new ArrayList<>();
        for (File file : files) {
            videos.add(getVideoFromFile(file));
        }
        return videos.toArray(new Video[0]);
    }

    public File[] getFiles() {
        return this.dir != null ? this.dir.listFiles() : null;
    }

    public String[] getFilesNames() {
        File[] files = this.getFiles();
        if (files == null) return new String[0];
        List<String> names = new ArrayList<>();
        for (File file : files) {
            names.add(file.getName());
        }
        return names.toArray(new String[0]);
    }

    private void init() throws IOException {
        if (this.dir != null && !this.dir.exists()) {
            this.dir.mkdirs();
        }
    }

    public String getPath() {
        return this.dir != null ? this.dir.getAbsolutePath() : "";
    }

    public static FileManager getInstance() {
        return instance;
    }
}
