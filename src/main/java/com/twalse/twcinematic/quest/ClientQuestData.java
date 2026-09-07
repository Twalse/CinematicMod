package com.twalse.twcinematic.quest;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientQuestData {
    private static int money = 0;
    private static String currentObjective = "";
    private static int objectiveProgress = 0;
    private static int objectiveMax = 0;

    public static void set(int m, String objective, int progress, int max) {
        money = m;
        currentObjective = objective;
        objectiveProgress = progress;
        objectiveMax = max;
    }

    public static int getMoney() {
        return money;
    }

    public static String getCurrentObjective() {
        return currentObjective;
    }

    public static int getObjectiveProgress() {
        return objectiveProgress;
    }

    public static int getObjectiveMax() {
        return objectiveMax;
    }
}
