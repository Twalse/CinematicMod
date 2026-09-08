package com.twalse.twmod.networking.message;

import com.twalse.twmod.client.gui.PhoneScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenPhonePacket {

    public OpenPhonePacket() {}

    public static void encode(OpenPhonePacket msg, FriendlyByteBuf buf) {}

    public static OpenPhonePacket decode(FriendlyByteBuf buf) {
        return new OpenPhonePacket();
    }

    public static void handle(OpenPhonePacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft.getInstance().setScreen(new PhoneScreen());
            });
        });
        ctx.setPacketHandled(true);
    }
}
