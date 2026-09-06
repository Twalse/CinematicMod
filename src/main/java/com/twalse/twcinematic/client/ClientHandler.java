package com.twalse.twcinematic.client;

import com.twalse.twcinematic.TwCinematic;
import com.twalse.twcinematic.client.gui.ConfigScreen;
import com.twalse.twcinematic.client.render.VideoScreen;
import com.twalse.twcinematic.networking.message.SendVideoPlayer;
import com.twalse.twcinematic.util.FileManager;
import com.twalse.twcinematic.util.KeyBinding;
import com.twalse.twcinematic.util.Video;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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

    @Mod.EventBusSubscriber(modid = TwCinematic.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerKey(RegisterKeyMappingsEvent e) {
            e.register(KeyBinding.EXIT_KEY);
            e.register(KeyBinding.OPEN_MENU_KEY);
        }
    }

    @Mod.EventBusSubscriber(modid = TwCinematic.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (KeyBinding.OPEN_MENU_KEY.consumeClick()) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.screen == null) {
                    mc.setScreen(new ConfigScreen(null));
                }
            }
        }
    }
}
