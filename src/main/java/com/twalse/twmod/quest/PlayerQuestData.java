package com.twalse.twmod.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerQuestData {
    private final Map<String, Integer> variables = new LinkedHashMap<>();
    private final Map<String, QuestData> quests = new LinkedHashMap<>();

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

    public void copyFrom(PlayerQuestData source) {
        this.variables.clear();
        this.variables.putAll(source.variables);
        this.quests.clear();
        for (Map.Entry<String, QuestData> entry : source.quests.entrySet()) {
            QuestData q = entry.getValue();
            this.quests.put(entry.getKey(), new QuestData(q.getId(), q.getDescription(), q.getCurrentProgress(), q.getMaxProgress()));
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
    }
}
