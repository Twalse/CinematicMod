package com.twalse.twmod.networking.message;

import com.twalse.twmod.block.GeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StartGeneratorPacket {
    public final BlockPos pos;

    public StartGeneratorPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(StartGeneratorPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static StartGeneratorPacket decode(FriendlyByteBuf buf) {
        return new StartGeneratorPacket(buf.readBlockPos());
    }

    public static void handle(StartGeneratorPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null && player.level().isLoaded(msg.pos)) {
                BlockEntity be = player.level().getBlockEntity(msg.pos);
                if (be instanceof GeneratorBlockEntity generatorBE) {
                    if (generatorBE.startGenerator()) {
                        player.closeContainer();
                    }
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
