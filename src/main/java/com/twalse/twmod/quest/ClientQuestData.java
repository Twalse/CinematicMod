package com.twalse.twmod.quest;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientQuestData {
    private static Map<String, Integer> variables = new LinkedHashMap<>();
    private static List<QuestData> quests = new ArrayList<>();
    private static List<WaypointData> waypoints = new ArrayList<>();

    public static void set(Map<String, Integer> vars, List<QuestData> qList, List<WaypointData> wList) {
        variables = vars != null ? new LinkedHashMap<>(vars) : new LinkedHashMap<>();
        quests = qList != null ? new ArrayList<>(qList) : new ArrayList<>();
        waypoints = wList != null ? new ArrayList<>(wList) : new ArrayList<>();
    }

    public static Map<String, Integer> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public static List<QuestData> getQuests() {
        return Collections.unmodifiableList(quests);
    }

    public static List<WaypointData> getWaypoints() {
        return Collections.unmodifiableList(waypoints);
    }
}
