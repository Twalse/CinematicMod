package com.twalse.twmod.quest;

import net.minecraft.nbt.CompoundTag;

public class QuestData {
    private String id;
    private String description;
    private int currentProgress;
    private int maxProgress;

    public QuestData(String id, String description, int currentProgress, int maxProgress) {
        this.id = id;
        this.description = description != null ? description : "";
        this.currentProgress = Math.max(0, currentProgress);
        this.maxProgress = Math.max(0, maxProgress);
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = Math.max(0, currentProgress);
    }

    public void addProgress(int amount) {
        this.currentProgress = Math.max(0, this.currentProgress + amount);
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = Math.max(0, maxProgress);
    }

    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        tag.putString("description", description);
        tag.putInt("currentProgress", currentProgress);
        tag.putInt("maxProgress", maxProgress);
        return tag;
    }

    public static QuestData loadNBT(CompoundTag tag) {
        String id = tag.getString("id");
        String desc = tag.getString("description");
        int cur = tag.getInt("currentProgress");
        int max = tag.getInt("maxProgress");
        return new QuestData(id, desc, cur, max);
    }
}
