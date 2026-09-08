package com.twalse.twmod.client.gui;

import com.twalse.twmod.TwMod;
import com.twalse.twmod.config.HudClientConfig;
import com.twalse.twmod.quest.ClientQuestData;
import com.twalse.twmod.quest.QuestData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.List;
import java.util.Map;

public class QuestScoreboardOverlay {
    public static final ResourceLocation COIN_ICON = new ResourceLocation(TwMod.MODID, "textures/gui/coin.png");
    public static final ResourceLocation DEFAULT_ICON = new ResourceLocation(TwMod.MODID, "textures/gui/icon.png");

    public static final IGuiOverlay HUD_QUEST_SCOREBOARD = (gui, guiGraphics, partialTick, width, height) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) {
            return;
        }

        HudClientConfig config = HudClientConfig.get();
        Map<String, Integer> variables = ClientQuestData.getVariables();
        List<QuestData> quests = ClientQuestData.getQuests();

        if (variables.isEmpty() && quests.isEmpty()) {
            return;
        }

        Font font = mc.font;
        int padding = 6;
        int boxWidth = 150;

        for (QuestData quest : quests) {
            String line = quest.getDescription();
            int w = font.width(line) + 20;
            if (w > boxWidth) {
                boxWidth = w;
            }
        }

        for (Map.Entry<String, Integer> entry : variables.entrySet()) {
            String line = entry.getKey() + ": " + entry.getValue();
            int w = font.width(line) + 30;
            if (w > boxWidth) {
                boxWidth = w;
            }
        }

        int boxHeight = padding * 2;
        if (!quests.isEmpty()) {
            boxHeight += 12;
            boxHeight += quests.size() * 22;
        }
        if (!variables.isEmpty()) {
            boxHeight += 12;
            boxHeight += variables.size() * 16;
        }

        guiGraphics.pose().pushPose();

        int startX = config.hudX;
        int startY = config.hudY;

        guiGraphics.pose().translate(startX, startY, 0);
        guiGraphics.pose().scale(config.hudScale, config.hudScale, 1.0f);

        int alpha = Math.min(255, Math.max(0, (int) (config.bgOpacity * 255)));
        int bgColor = (alpha << 24);
        int borderColor = (alpha > 0 ? 0xFFD4AF37 : 0);

        guiGraphics.fill(0, 0, boxWidth, boxHeight, bgColor);
        if (alpha > 0) {
            guiGraphics.fill(-1, -1, boxWidth + 1, 0, borderColor);
            guiGraphics.fill(-1, boxHeight, boxWidth + 1, boxHeight + 1, borderColor);
            guiGraphics.fill(-1, 0, 0, boxHeight, borderColor);
            guiGraphics.fill(boxWidth, 0, boxWidth + 1, boxHeight, borderColor);
        }

        int currentY = padding;
        int textColor = config.textColor | 0xFF000000;

        if (!quests.isEmpty()) {
            Component questHeader = Component.literal("TASKS").withStyle(Style.EMPTY.withColor(0xFFD4AF37).withBold(true));
            guiGraphics.drawString(font, questHeader, padding, currentY, 0xFFD4AF37, false);
            currentY += 12;

            for (QuestData quest : quests) {
                Component descComp = Component.literal(quest.getDescription()).withStyle(Style.EMPTY.withColor(textColor));
                guiGraphics.drawString(font, descComp, padding, currentY, textColor, false);
                currentY += 10;

                if (quest.getMaxProgress() > 0) {
                    String progressStr = quest.getCurrentProgress() + " / " + quest.getMaxProgress();
                    guiGraphics.drawString(font, Component.literal(progressStr), padding, currentY, 0xAAAAAA, false);

                    int barWidth = boxWidth - (padding * 2);
                    int barHeight = 2;
                    int barY = currentY + 9;
                    float pct = Math.min(1.0f, (float) quest.getCurrentProgress() / (float) quest.getMaxProgress());
                    int fillWidth = (int) (barWidth * pct);

                    guiGraphics.fill(padding, barY, padding + barWidth, barY + barHeight, 0x80555555);
                    guiGraphics.fill(padding, barY, padding + fillWidth, barY + barHeight, 0xFF55FF55);

                    currentY += 12;
                } else {
                    currentY += 2;
                }
            }
        }

        if (!variables.isEmpty()) {
            Component varHeader = Component.literal("STATS").withStyle(Style.EMPTY.withColor(0xFFD4AF37).withBold(true));
            guiGraphics.drawString(font, varHeader, padding, currentY, 0xFFD4AF37, false);
            currentY += 12;

            for (Map.Entry<String, Integer> entry : variables.entrySet()) {
                String varId = entry.getKey();
                int value = entry.getValue();

                ResourceLocation icon = varId.equalsIgnoreCase("money") || varId.equalsIgnoreCase("coin") || varId.equalsIgnoreCase("coins") ? COIN_ICON : DEFAULT_ICON;

                try {
                    guiGraphics.blit(icon, padding, currentY - 1, 0, 0, 10, 10, 10, 10);
                } catch (Exception ignored) {
                }

                String labelText = varId.toUpperCase() + ": " + value;
                Component varComp = Component.literal(labelText).withStyle(Style.EMPTY.withColor(textColor));
                guiGraphics.drawString(font, varComp, padding + 14, currentY, textColor, false);

                currentY += 16;
            }
        }

        guiGraphics.pose().popPose();
    };
}
