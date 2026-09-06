package com.twalse.twcinematic.networking.message;

import com.twalse.twcinematic.client.ClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SendVideoPlayer {
    public final String name;
    public final int volume;

    public SendVideoPlayer(String name, int volume) {
        this.name = name;
        this.volume = volume;
    }

    public static void encode(SendVideoPlayer msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.name);
        buf.writeInt(msg.volume);
    }

    public static SendVideoPlayer decode(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        int volume = buf.readInt();
        return new SendVideoPlayer(name, volume);
    }

    public static void handle(SendVideoPlayer msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.handlePacket(msg));
        });
        ctx.setPacketHandled(true);
    }
}
