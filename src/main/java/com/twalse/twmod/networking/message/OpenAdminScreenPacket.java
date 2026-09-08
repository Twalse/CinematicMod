package com.twalse.twmod.networking.message;

import com.twalse.twmod.client.gui.QuestAdminScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenAdminScreenPacket {

    public OpenAdminScreenPacket() {}

    public static void encode(OpenAdminScreenPacket msg, FriendlyByteBuf buf) {}

    public static OpenAdminScreenPacket decode(FriendlyByteBuf buf) {
        return new OpenAdminScreenPacket();
    }

    public static void handle(OpenAdminScreenPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft.getInstance().setScreen(new QuestAdminScreen());
            });
        });
        ctx.setPacketHandled(true);
    }
}
