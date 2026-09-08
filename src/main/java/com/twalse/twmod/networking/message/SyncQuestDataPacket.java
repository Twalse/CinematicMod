package com.twalse.twmod.networking.message;

import com.twalse.twmod.quest.ClientQuestData;
import com.twalse.twmod.quest.QuestData;
import com.twalse.twmod.quest.WaypointData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SyncQuestDataPacket {
    public final Map<String, Integer> variables;
    public final List<QuestData> quests;
    public final List<WaypointData> waypoints;

    public SyncQuestDataPacket(Map<String, Integer> variables, List<QuestData> quests, List<WaypointData> waypoints) {
        this.variables = variables != null ? variables : new LinkedHashMap<>();
        this.quests = quests != null ? quests : new ArrayList<>();
        this.waypoints = waypoints != null ? waypoints : new ArrayList<>();
    }

    public static void encode(SyncQuestDataPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.variables.size());
        for (Map.Entry<String, Integer> entry : msg.variables.entrySet()) {
            buf.writeUtf(entry.getKey());
            buf.writeInt(entry.getValue());
        }

        buf.writeInt(msg.quests.size());
        for (QuestData quest : msg.quests) {
            buf.writeUtf(quest.getId());
            buf.writeUtf(quest.getDescription());
            buf.writeInt(quest.getCurrentProgress());
            buf.writeInt(quest.getMaxProgress());
        }

        buf.writeInt(msg.waypoints.size());
        for (WaypointData waypoint : msg.waypoints) {
            buf.writeUtf(waypoint.getId());
            buf.writeDouble(waypoint.getX());
            buf.writeDouble(waypoint.getY());
            buf.writeDouble(waypoint.getZ());
            buf.writeInt(waypoint.getColor());
            buf.writeUtf(waypoint.getName());
        }
    }

    public static SyncQuestDataPacket decode(FriendlyByteBuf buf) {
        int varSize = buf.readInt();
        Map<String, Integer> vars = new LinkedHashMap<>();
        for (int i = 0; i < varSize; i++) {
            String k = buf.readUtf();
            int v = buf.readInt();
            vars.put(k, v);
        }

        int questSize = buf.readInt();
        List<QuestData> qList = new ArrayList<>();
        for (int i = 0; i < questSize; i++) {
            String id = buf.readUtf();
            String desc = buf.readUtf();
            int cur = buf.readInt();
            int max = buf.readInt();
            qList.add(new QuestData(id, desc, cur, max));
        }

        int waypointsSize = buf.readInt();
        List<WaypointData> wList = new ArrayList<>();
        for (int i = 0; i < waypointsSize; i++) {
            String id = buf.readUtf();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            int color = buf.readInt();
            String name = buf.readUtf();
            wList.add(new WaypointData(id, x, y, z, color, name));
        }

        return new SyncQuestDataPacket(vars, qList, wList);
    }

    public static void handle(SyncQuestDataPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientQuestData.set(msg.variables, msg.quests, msg.waypoints);
            });
        });
        ctx.setPacketHandled(true);
    }
}
