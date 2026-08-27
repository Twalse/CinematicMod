package com.mitosv.cinematic.client;

import com.mitosv.cinematic.client.render.VideoScreen;
import com.mitosv.cinematic.networking.message.SendVideoPlayer;
import com.mitosv.cinematic.util.FileManager;
import com.mitosv.cinematic.util.Video;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientHandler {

    public static void handlePacket(SendVideoPlayer msg) {
        Video video = null;
        if (FileManager.getInstance() != null) {
            video = FileManager.getInstance().getVideoFromName(msg.name);
        }
        if (video == null) {
            video = new Video(msg.name);
        }
        openVideo(video, msg.volume);
    }

    public static void openVideo(Video video, int volume) {
        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().setScreen(new VideoScreen(video, volume));
        });
    }
}
