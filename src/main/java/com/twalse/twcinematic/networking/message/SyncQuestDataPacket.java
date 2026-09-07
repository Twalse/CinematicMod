package com.twalse.twcinematic.networking.message;

import com.twalse.twcinematic.quest.ClientQuestData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncQuestDataPacket {
    public final int money;
    public final String currentObjective;
    public final int objectiveProgress;
    public final int objectiveMax;

    public SyncQuestDataPacket(int money, String currentObjective, int objectiveProgress, int objectiveMax) {
        this.money = money;
        this.currentObjective = currentObjective != null ? currentObjective : "";
        this.objectiveProgress = objectiveProgress;
        this.objectiveMax = objectiveMax;
    }

    public static void encode(SyncQuestDataPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.money);
        buf.writeUtf(msg.currentObjective);
        buf.writeInt(msg.objectiveProgress);
        buf.writeInt(msg.objectiveMax);
    }

    public static SyncQuestDataPacket decode(FriendlyByteBuf buf) {
        int money = buf.readInt();
        String objective = buf.readUtf();
        int progress = buf.readInt();
        int max = buf.readInt();
        return new SyncQuestDataPacket(money, objective, progress, max);
    }

    public static void handle(SyncQuestDataPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientQuestData.set(msg.money, msg.currentObjective, msg.objectiveProgress, msg.objectiveMax);
            });
        });
        ctx.setPacketHandled(true);
    }
}
