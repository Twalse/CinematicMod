package com.twalse.twcinematic.networking;

import com.twalse.twcinematic.networking.message.SendVideoPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("twcinematic", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void init() {
        INSTANCE.messageBuilder(SendVideoPlayer.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SendVideoPlayer::encode)
                .decoder(SendVideoPlayer::decode)
                .consumerMainThread(SendVideoPlayer::handle)
                .add();
    }

    public static void sendToPlayer(SendVideoPlayer msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static void sendToAll(SendVideoPlayer msg) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }
}
