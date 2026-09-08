package com.twalse.twmod.networking.message;

import com.twalse.twmod.networking.PacketHandler;
import com.twalse.twmod.quest.PlayerQuestProvider;
import com.twalse.twmod.util.TwLogger;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ExecuteContactActionPacket {
    public final String contactId;

    public ExecuteContactActionPacket(String contactId) {
        this.contactId = contactId != null ? contactId : "";
    }

    public static void encode(ExecuteContactActionPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.contactId);
    }

    public static ExecuteContactActionPacket decode(FriendlyByteBuf buf) {
        String contactId = buf.readUtf();
        return new ExecuteContactActionPacket(contactId);
    }

    public static void handle(ExecuteContactActionPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                TwLogger.info("Player {} called contact {}", player.getName().getString(), msg.contactId);

                if ("boss".equalsIgnoreCase(msg.contactId)) {
                    player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                        data.setQuest("main_job", "Ограбить банк и забрать наличные", 100);
                        PacketHandler.syncQuestData(player);
                    });
                    player.sendSystemMessage(Component.literal("§e[Босс]:§f Новое задание получено! Проверь контракты в телефоне."));
                } else if ("dealer".equalsIgnoreCase(msg.contactId)) {
                    player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                        data.addVariable("wanted", 1);
                        PacketHandler.syncQuestData(player);
                    });
                    player.sendSystemMessage(Component.literal("§c[Барыга]:§f Полиция уже на хвосте! Уровень розыска повышен."));
                } else {
                    player.sendSystemMessage(Component.literal("§7[Абонент недоступен или занят]"));
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
