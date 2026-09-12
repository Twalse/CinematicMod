package com.twalse.twmod.networking.message;

import com.twalse.twmod.client.gui.LockpickScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenLockpickPacket {
    public final int pinsCount;
    public final int pickHealth;
    public final BlockPos doorPos;

    public OpenLockpickPacket(int pinsCount, int pickHealth) {
        this(pinsCount, pickHealth, null);
    }

    public OpenLockpickPacket(int pinsCount, int pickHealth, BlockPos doorPos) {
        this.pinsCount = pinsCount;
        this.pickHealth = pickHealth;
        this.doorPos = doorPos;
    }

    public static void encode(OpenLockpickPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.pinsCount);
        buf.writeInt(msg.pickHealth);
        boolean hasPos = msg.doorPos != null;
        buf.writeBoolean(hasPos);
        if (hasPos) {
            buf.writeBlockPos(msg.doorPos);
        }
    }

    public static OpenLockpickPacket decode(FriendlyByteBuf buf) {
        int pinsCount = buf.readInt();
        int pickHealth = buf.readInt();
        boolean hasPos = buf.readBoolean();
        BlockPos pos = hasPos ? buf.readBlockPos() : null;
        return new OpenLockpickPacket(pinsCount, pickHealth, pos);
    }

    public static void handle(OpenLockpickPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft.getInstance().setScreen(new LockpickScreen(msg.pinsCount, msg.pickHealth, msg.doorPos));
            });
        });
        ctx.setPacketHandled(true);
    }
}
