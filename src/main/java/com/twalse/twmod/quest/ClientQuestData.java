package com.twalse.twmod.quest;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ClientQuestData {
    private static Map<String, Integer> variables = new LinkedHashMap<>();
    private static List<QuestData> quests = new ArrayList<>();
    private static List<WaypointData> waypoints = new ArrayList<>();
    private static Set<String> installedApps = new HashSet<>();
    private static Map<String, List<String>> twGrammMessages = new LinkedHashMap<>();

    static {
        installedApps.add("contacts");
        installedApps.add("market");
        installedApps.add("twstore");
    }

    public static void set(Map<String, Integer> vars, List<QuestData> qList, List<WaypointData> wList, Set<String> apps, Map<String, List<String>> msgs) {
        variables = vars != null ? new LinkedHashMap<>(vars) : new LinkedHashMap<>();
        quests = qList != null ? new ArrayList<>(qList) : new ArrayList<>();
        waypoints = wList != null ? new ArrayList<>(wList) : new ArrayList<>();
        installedApps = apps != null ? new HashSet<>(apps) : new HashSet<>();
        twGrammMessages = msgs != null ? new LinkedHashMap<>(msgs) : new LinkedHashMap<>();
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

    public static Set<String> getInstalledApps() {
        return Collections.unmodifiableSet(installedApps);
    }

    public static boolean isAppInstalled(String appId) {
        return installedApps.contains(appId.toLowerCase());
    }

    public static Map<String, List<String>> getTwGrammMessages() {
        return Collections.unmodifiableMap(twGrammMessages);
    }

    public static List<String> getMessagesForContact(String contactId) {
        if (contactId == null) return Collections.emptyList();
        return twGrammMessages.getOrDefault(contactId.toLowerCase(), Collections.emptyList());
    }
}
