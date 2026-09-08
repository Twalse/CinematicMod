package com.twalse.twmod.networking;

import com.twalse.twmod.networking.message.LockpickResultPacket;
import com.twalse.twmod.networking.message.OpenAdminScreenPacket;
import com.twalse.twmod.networking.message.OpenLockpickPacket;
import com.twalse.twmod.networking.message.OpenPhonePacket;
import com.twalse.twmod.networking.message.SendVideoPlayer;
import com.twalse.twmod.networking.message.SyncQuestDataPacket;
import com.twalse.twmod.quest.PlayerQuestProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("twmod", "main"),
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

        INSTANCE.messageBuilder(SyncQuestDataPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncQuestDataPacket::encode)
                .decoder(SyncQuestDataPacket::decode)
                .consumerMainThread(SyncQuestDataPacket::handle)
                .add();

        INSTANCE.messageBuilder(OpenAdminScreenPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenAdminScreenPacket::encode)
                .decoder(OpenAdminScreenPacket::decode)
                .consumerMainThread(OpenAdminScreenPacket::handle)
                .add();

        INSTANCE.messageBuilder(OpenLockpickPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenLockpickPacket::encode)
                .decoder(OpenLockpickPacket::decode)
                .consumerMainThread(OpenLockpickPacket::handle)
                .add();

        INSTANCE.messageBuilder(LockpickResultPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(LockpickResultPacket::encode)
                .decoder(LockpickResultPacket::decode)
                .consumerMainThread(LockpickResultPacket::handle)
                .add();

        INSTANCE.messageBuilder(OpenPhonePacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenPhonePacket::encode)
                .decoder(OpenPhonePacket::decode)
                .consumerMainThread(OpenPhonePacket::handle)
                .add();
    }

    public static void sendToPlayer(Object msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static void sendToServer(Object msg) {
        INSTANCE.sendToServer(msg);
    }

    public static void sendToAll(Object msg) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }

    public static void syncQuestData(ServerPlayer player) {
        player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
            sendToPlayer(new SyncQuestDataPacket(
                    data.getVariables(),
                    new ArrayList<>(data.getQuests().values()),
                    new ArrayList<>(data.getWaypoints().values())
            ), player);
        });
    }
}
