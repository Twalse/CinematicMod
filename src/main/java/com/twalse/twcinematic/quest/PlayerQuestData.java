package com.twalse.twcinematic.quest;

import net.minecraft.nbt.CompoundTag;

public class PlayerQuestData {
    private int money = 0;
    private String currentObjective = "";
    private int objectiveProgress = 0;
    private int objectiveMax = 0;

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = Math.max(0, money);
    }

    public void addMoney(int amount) {
        this.money = Math.max(0, this.money + amount);
    }

    public String getCurrentObjective() {
        return currentObjective;
    }

    public void setCurrentObjective(String currentObjective) {
        this.currentObjective = currentObjective != null ? currentObjective : "";
    }

    public int getObjectiveProgress() {
        return objectiveProgress;
    }

    public void setObjectiveProgress(int objectiveProgress) {
        this.objectiveProgress = Math.max(0, objectiveProgress);
    }

    public void addObjectiveProgress(int amount) {
        this.objectiveProgress = Math.max(0, this.objectiveProgress + amount);
    }

    public int getObjectiveMax() {
        return objectiveMax;
    }

    public void setObjectiveMax(int objectiveMax) {
        this.objectiveMax = Math.max(0, objectiveMax);
    }

    public void copyFrom(PlayerQuestData source) {
        this.money = source.money;
        this.currentObjective = source.currentObjective;
        this.objectiveProgress = source.objectiveProgress;
        this.objectiveMax = source.objectiveMax;
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putInt("money", money);
        tag.putString("currentObjective", currentObjective);
        tag.putInt("objectiveProgress", objectiveProgress);
        tag.putInt("objectiveMax", objectiveMax);
    }

    public void loadNBTData(CompoundTag tag) {
        this.money = tag.getInt("money");
        this.currentObjective = tag.getString("currentObjective");
        this.objectiveProgress = tag.getInt("objectiveProgress");
        this.objectiveMax = tag.getInt("objectiveMax");
    }
}
