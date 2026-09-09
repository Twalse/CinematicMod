package com.twalse.twmod.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerQuestData {
    private final Map<String, Integer> variables = new LinkedHashMap<>();
    private final Map<String, QuestData> quests = new LinkedHashMap<>();
    private final Map<String, WaypointData> waypoints = new LinkedHashMap<>();
    private final Set<String> installedApps = new HashSet<>();
    private final Map<String, List<String>> twGrammMessages = new LinkedHashMap<>();

    public PlayerQuestData() {
        // Default installed apps
        installedApps.add("contacts");
        installedApps.add("market");
        installedApps.add("twstore");
    }

    public Set<String> getInstalledApps() {
        return Collections.unmodifiableSet(installedApps);
    }

    public boolean isAppInstalled(String appId) {
        return installedApps.contains(appId.toLowerCase());
    }

    public void installApp(String appId) {
        if (appId != null && !appId.isEmpty()) {
            installedApps.add(appId.toLowerCase());
        }
    }

    public void uninstallApp(String appId) {
        if (appId != null) {
            installedApps.remove(appId.toLowerCase());
        }
    }

    public Map<String, Integer> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public int getVariable(String varId) {
        return variables.getOrDefault(varId, 0);
    }

    public void setVariable(String varId, int value) {
        if (varId != null && !varId.isEmpty()) {
            variables.put(varId, value);
        }
    }

    public void addVariable(String varId, int amount) {
        if (varId != null && !varId.isEmpty()) {
            int current = variables.getOrDefault(varId, 0);
            variables.put(varId, current + amount);
        }
    }

    public void removeVariable(String varId) {
        if (varId != null) {
            variables.remove(varId);
        }
    }

    public Map<String, QuestData> getQuests() {
        return Collections.unmodifiableMap(quests);
    }

    public QuestData getQuest(String questId) {
        return quests.get(questId);
    }

    public void setQuest(String questId, String description, int maxProgress) {
        if (questId != null && !questId.isEmpty()) {
            QuestData quest = quests.get(questId);
            if (quest == null) {
                quests.put(questId, new QuestData(questId, description, 0, maxProgress));
            } else {
                quest.setDescription(description);
                quest.setMaxProgress(maxProgress);
            }
        }
    }

    public void addQuestProgress(String questId, int amount) {
        QuestData quest = quests.get(questId);
        if (quest != null) {
            quest.addProgress(amount);
        }
    }

    public void removeQuest(String questId) {
        quests.remove(questId);
    }

    public Map<String, WaypointData> getWaypoints() {
        return Collections.unmodifiableMap(waypoints);
    }

    public WaypointData getWaypoint(String waypointId) {
        return waypoints.get(waypointId);
    }

    public void addWaypoint(WaypointData waypoint) {
        if (waypoint != null && waypoint.getId() != null && !waypoint.getId().isEmpty()) {
            waypoints.put(waypoint.getId(), waypoint);
        }
    }

    public void removeWaypoint(String waypointId) {
        if (waypointId != null) {
            waypoints.remove(waypointId);
        }
    }

    public Map<String, List<String>> getTwGrammMessages() {
        return Collections.unmodifiableMap(twGrammMessages);
    }

    public List<String> getMessagesForContact(String contactId) {
        return twGrammMessages.getOrDefault(contactId.toLowerCase(), Collections.emptyList());
    }

    public void addTwGrammMessage(String contactId, String message) {
        if (contactId != null && !contactId.isEmpty() && message != null) {
            twGrammMessages.computeIfAbsent(contactId.toLowerCase(), k -> new ArrayList<>()).add(message);
        }
    }

    public void copyFrom(PlayerQuestData source) {
        this.variables.clear();
        this.variables.putAll(source.variables);

        this.quests.clear();
        for (Map.Entry<String, QuestData> entry : source.quests.entrySet()) {
            QuestData q = entry.getValue();
            this.quests.put(entry.getKey(), new QuestData(q.getId(), q.getDescription(), q.getCurrentProgress(), q.getMaxProgress()));
        }

        this.waypoints.clear();
        for (Map.Entry<String, WaypointData> entry : source.waypoints.entrySet()) {
            WaypointData w = entry.getValue();
            this.waypoints.put(entry.getKey(), new WaypointData(w.getId(), w.getX(), w.getY(), w.getZ(), w.getColor(), w.getName()));
        }

        this.installedApps.clear();
        this.installedApps.addAll(source.installedApps);

        this.twGrammMessages.clear();
        for (Map.Entry<String, List<String>> entry : source.twGrammMessages.entrySet()) {
            this.twGrammMessages.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }

    public void saveNBTData(CompoundTag tag) {
        CompoundTag varTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : variables.entrySet()) {
            varTag.putInt(entry.getKey(), entry.getValue());
        }
        tag.put("Variables", varTag);

        ListTag questList = new ListTag();
        for (QuestData quest : quests.values()) {
            questList.add(quest.saveNBT());
        }
        tag.put("Quests", questList);

        ListTag waypointList = new ListTag();
        for (WaypointData waypoint : waypoints.values()) {
            waypointList.add(waypoint.saveNBT());
        }
        tag.put("Waypoints", waypointList);

        ListTag appList = new ListTag();
        for (String app : installedApps) {
            appList.add(StringTag.valueOf(app));
        }
        tag.put("InstalledApps", appList);

        CompoundTag msgCompound = new CompoundTag();
        for (Map.Entry<String, List<String>> entry : twGrammMessages.entrySet()) {
            ListTag listTag = new ListTag();
            for (String msg : entry.getValue()) {
                listTag.add(StringTag.valueOf(msg));
            }
            msgCompound.put(entry.getKey(), listTag);
        }
        tag.put("TwGrammMessages", msgCompound);
    }

    public void loadNBTData(CompoundTag tag) {
        variables.clear();
        if (tag.contains("Variables", Tag.TAG_COMPOUND)) {
            CompoundTag varTag = tag.getCompound("Variables");
            for (String key : varTag.getAllKeys()) {
                variables.put(key, varTag.getInt(key));
            }
        }

        quests.clear();
        if (tag.contains("Quests", Tag.TAG_LIST)) {
            ListTag questList = tag.getList("Quests", Tag.TAG_COMPOUND);
            for (int i = 0; i < questList.size(); i++) {
                CompoundTag qTag = questList.getCompound(i);
                QuestData q = QuestData.loadNBT(qTag);
                quests.put(q.getId(), q);
            }
        }

        waypoints.clear();
        if (tag.contains("Waypoints", Tag.TAG_LIST)) {
            ListTag waypointList = tag.getList("Waypoints", Tag.TAG_COMPOUND);
            for (int i = 0; i < waypointList.size(); i++) {
                CompoundTag wTag = waypointList.getCompound(i);
                WaypointData w = WaypointData.loadNBT(wTag);
                waypoints.put(w.getId(), w);
            }
        }

        installedApps.clear();
        if (tag.contains("InstalledApps", Tag.TAG_LIST)) {
            ListTag appList = tag.getList("InstalledApps", Tag.TAG_STRING);
            for (int i = 0; i < appList.size(); i++) {
                installedApps.add(appList.getString(i));
            }
        } else {
            installedApps.add("contacts");
            installedApps.add("market");
            installedApps.add("twstore");
        }

        twGrammMessages.clear();
        if (tag.contains("TwGrammMessages", Tag.TAG_COMPOUND)) {
            CompoundTag msgCompound = tag.getCompound("TwGrammMessages");
            for (String contactId : msgCompound.getAllKeys()) {
                ListTag listTag = msgCompound.getList(contactId, Tag.TAG_STRING);
                List<String> list = new ArrayList<>();
                for (int i = 0; i < listTag.size(); i++) {
                    list.add(listTag.getString(i));
                }
                twGrammMessages.put(contactId.toLowerCase(), list);
            }
        }
    }
}
