package com.twalse.twmod.util;

import net.minecraft.client.Minecraft;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GeneratorSoundManager {
    private static final Map<String, Clip> activeClips = new ConcurrentHashMap<>();

    public static void updateSoundState(String posKey, boolean isActive) {
        if (isActive) {
            if (!activeClips.containsKey(posKey)) {
                try {
                    File gameDir = Minecraft.getInstance().gameDirectory;
                    File soundsDir = new File(gameDir, "tw_sounds");
                    if (!soundsDir.exists()) {
                        soundsDir.mkdirs();
                    }

                    File soundFile = new File(soundsDir, "generator.wav");
                    if (!soundFile.exists()) {
                        soundFile = new File(soundsDir, "generator.ogg");
                    }

                    if (soundFile.exists()) {
                        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                        Clip clip = AudioSystem.getClip();
                        clip.open(ais);
                        clip.loop(Clip.LOOP_CONTINUOUSLY);
                        clip.start();
                        activeClips.put(posKey, clip);
                    }
                } catch (Exception e) {
                    TwLogger.error("Error playing generator sound for position: " + posKey, e);
                }
            }
        } else {
            Clip clip = activeClips.remove(posKey);
            if (clip != null) {
                try {
                    clip.stop();
                    clip.close();
                } catch (Exception ignored) {}
            }
        }
    }

    public static void stopAll() {
        for (Clip clip : activeClips.values()) {
            try {
                clip.stop();
                clip.close();
            } catch (Exception ignored) {}
        }
        activeClips.clear();
    }
}
