package com.twalse.twcinematic.client.gui;

import com.twalse.twcinematic.TwCinematic;
import com.twalse.twcinematic.quest.ClientQuestData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class QuestScoreboardOverlay {
    public static final ResourceLocation COIN_ICON = new ResourceLocation(TwCinematic.MODID, "textures/gui/coin.png");

    public static final IGuiOverlay HUD_QUEST_SCOREBOARD = (gui, guiGraphics, partialTick, width, height) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) {
            return;
        }

        Font font = mc.font;
        String objective = ClientQuestData.getCurrentObjective();
        int progress = ClientQuestData.getObjectiveProgress();
        int maxProgress = ClientQuestData.getObjectiveMax();
        int money = ClientQuestData.getMoney();

        // If no objective and no money set, don't display empty overlay
        if ((objective == null || objective.isEmpty()) && money == 0) {
            return;
        }

        int padding = 6;
        int boxWidth = 140;

        String objectiveText = (objective != null && !objective.isEmpty()) ? objective : "No Active Mission";
        String progressText = maxProgress > 0 ? progress + " / " + maxProgress : "";
        String moneyText = String.valueOf(money);

        int textWidth = font.width(objectiveText);
        if (textWidth + 20 > boxWidth) {
            boxWidth = textWidth + 20;
        }

        int boxHeight = 42;
        if (maxProgress > 0) {
            boxHeight += 12;
        }

        int x = width - boxWidth - padding;
        int y = height / 3;

        // Draw background box with translucent black and gold border
        guiGraphics.fill(x, y, x + boxWidth, y + boxHeight, 0x80000000);
        guiGraphics.fill(x - 1, y - 1, x + boxWidth + 1, y, 0xFFD4AF37); // Top border
        guiGraphics.fill(x - 1, y + boxHeight, x + boxWidth + 1, y + boxHeight + 1, 0xFFD4AF37); // Bottom border
        guiGraphics.fill(x - 1, y, x, y + boxHeight, 0xFFD4AF37); // Left border
        guiGraphics.fill(x + boxWidth, y, x + boxWidth + 1, y + boxHeight, 0xFFD4AF37); // Right border

        // Header Title
        guiGraphics.drawString(font, Component.literal("OBJECTIVE"), x + 8, y + 6, 0xFFD4AF37, false);

        // Mission Text
        guiGraphics.drawString(font, Component.literal(objectiveText), x + 8, y + 18, 0xFFFFFFFF, false);

        int currentY = y + 30;

        // Progress Text if applicable
        if (maxProgress > 0) {
            guiGraphics.drawString(font, Component.literal("Progress: " + progressText), x + 8, currentY, 0xAAAAAA, false);
            currentY += 12;
        }

        // Money Display with coin icon / custom texture support
        guiGraphics.drawString(font, Component.literal("Coins: "), x + 8, currentY, 0xFFFF55, false);
        int coinsWidth = font.width("Coins: ");

        // Try rendering coin texture if present, fallback to styled text
        try {
            guiGraphics.blit(COIN_ICON, x + 8 + coinsWidth, currentY - 1, 0, 0, 10, 10, 10, 10);
            guiGraphics.drawString(font, Component.literal(moneyText), x + 8 + coinsWidth + 12, currentY, 0xFFFFFF, false);
        } catch (Exception e) {
            guiGraphics.drawString(font, Component.literal(moneyText), x + 8 + coinsWidth, currentY, 0xFFFFFF, false);
        }
    };
}
