package com.twalse.twmod.networking.message;

import com.twalse.twmod.client.gui.LockpickScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenLockpickPacket {
    public final int pinsCount;
    public final int pickHealth;

    public OpenLockpickPacket(int pinsCount, int pickHealth) {
        this.pinsCount = pinsCount;
        this.pickHealth = pickHealth;
    }

    public static void encode(OpenLockpickPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.pinsCount);
        buf.writeInt(msg.pickHealth);
    }

    public static OpenLockpickPacket decode(FriendlyByteBuf buf) {
        int pinsCount = buf.readInt();
        int pickHealth = buf.readInt();
        return new OpenLockpickPacket(pinsCount, pickHealth);
    }

    public static void handle(OpenLockpickPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft.getInstance().setScreen(new LockpickScreen(msg.pinsCount, msg.pickHealth));
            });
        });
        ctx.setPacketHandled(true);
    }
}
