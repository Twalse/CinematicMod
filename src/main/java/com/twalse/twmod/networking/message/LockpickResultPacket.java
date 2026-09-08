package com.twalse.twmod.networking.message;

import com.twalse.twmod.util.TwLogger;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LockpickResultPacket {
    public final boolean success;

    public LockpickResultPacket(boolean success) {
        this.success = success;
    }

    public static void encode(LockpickResultPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.success);
    }

    public static LockpickResultPacket decode(FriendlyByteBuf buf) {
        boolean success = buf.readBoolean();
        return new LockpickResultPacket(success);
    }

    public static void handle(LockpickResultPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                if (msg.success) {
                    TwLogger.info("Player {} successfully lockpicked the lock!", player.getName().getString());
                    player.sendSystemMessage(Component.literal("§aВзлом успешен!"));
                } else {
                    TwLogger.info("Player {} failed lockpicking (pick broke).", player.getName().getString());
                    player.sendSystemMessage(Component.literal("§cОтмычка сломалась..."));
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
