package com.twalse.twmod.client.gui;

import com.twalse.twmod.networking.PacketHandler;
import com.twalse.twmod.quest.ClientQuestData;
import com.twalse.twmod.quest.QuestData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class QuestAdminScreen extends Screen {
    private EditBox varKeyBox;
    private EditBox varValBox;
    private EditBox questIdBox;
    private EditBox questDescBox;
    private EditBox questMaxBox;

    public QuestAdminScreen() {
        super(Component.literal("TwMod Quest & Variable Admin"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 35;

        // Variable Management Controls
        this.varKeyBox = new EditBox(this.font, centerX - 150, startY, 100, 18, Component.literal("Var Key"));
        this.varKeyBox.setHint(Component.literal("Var Key (e.g. scrap)"));
        this.addRenderableWidget(this.varKeyBox);

        this.varValBox = new EditBox(this.font, centerX - 45, startY, 60, 18, Component.literal("Var Val"));
        this.varValBox.setHint(Component.literal("Value"));
        this.addRenderableWidget(this.varValBox);

        this.addRenderableWidget(Button.builder(Component.literal("Set Var"), btn -> {
            String key = varKeyBox.getValue().trim();
            String valStr = varValBox.getValue().trim();
            if (!key.isEmpty()) {
                try {
                    int val = Integer.parseInt(valStr);
                    if (this.minecraft != null && this.minecraft.player != null) {
                        this.minecraft.player.connection.sendCommand("tw var set @s " + key + " " + val);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }).bounds(centerX + 20, startY - 1, 60, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("+1"), btn -> {
            String key = varKeyBox.getValue().trim();
            if (!key.isEmpty() && this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.connection.sendCommand("tw var add @s " + key + " 1");
            }
        }).bounds(centerX + 85, startY - 1, 35, 20).build());

        // Quest Management Controls
        int questY = startY + 35;
        this.questIdBox = new EditBox(this.font, centerX - 150, questY, 70, 18, Component.literal("Quest ID"));
        this.questIdBox.setHint(Component.literal("Quest ID"));
        this.addRenderableWidget(this.questIdBox);

        this.questDescBox = new EditBox(this.font, centerX - 75, questY, 110, 18, Component.literal("Quest Desc"));
        this.questDescBox.setHint(Component.literal("Description"));
        this.addRenderableWidget(this.questDescBox);

        this.questMaxBox = new EditBox(this.font, centerX + 40, questY, 40, 18, Component.literal("Max"));
        this.questMaxBox.setHint(Component.literal("Max"));
        this.addRenderableWidget(this.questMaxBox);

        this.addRenderableWidget(Button.builder(Component.literal("Set Quest"), btn -> {
            String id = questIdBox.getValue().trim();
            String desc = questDescBox.getValue().trim();
            String maxStr = questMaxBox.getValue().trim();
            if (!id.isEmpty()) {
                int max = 0;
                try { max = Integer.parseInt(maxStr); } catch (Exception ignored) {}
                if (this.minecraft != null && this.minecraft.player != null) {
                    this.minecraft.player.connection.sendCommand("tw quest set @s " + id + " \"" + desc + "\" " + max);
                }
            }
        }).bounds(centerX + 85, questY - 1, 65, 20).build());

        // Done Button
        this.addRenderableWidget(Button.builder(Component.literal("Close"), btn -> this.onClose())
                .bounds(centerX - 50, this.height - 28, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);

        int centerX = this.width / 2;
        guiGraphics.drawString(this.font, "Variables & Stats:", centerX - 150, 22, 0xA0A0A0);
        guiGraphics.drawString(font, "Quests & Objectives:", centerX - 150, 57, 0xA0A0A0);

        // Render current client data lists
        int listY = 95;
        guiGraphics.drawString(this.font, "Active Variables:", centerX - 150, listY, 0xD4AF37);
        listY += 12;
        for (Map.Entry<String, Integer> entry : ClientQuestData.getVariables().entrySet()) {
            if (listY > this.height - 50) break;
            guiGraphics.drawString(this.font, "• " + entry.getKey() + " = " + entry.getValue(), centerX - 140, listY, 0xFFFFFF);
            listY += 10;
        }

        int questListY = 95;
        guiGraphics.drawString(this.font, "Active Quests:", centerX + 10, questListY, 0xD4AF37);
        questListY += 12;
        for (QuestData q : ClientQuestData.getQuests()) {
            if (questListY > this.height - 50) break;
            guiGraphics.drawString(this.font, "• " + q.getId() + ": " + q.getDescription() + " (" + q.getCurrentProgress() + "/" + q.getMaxProgress() + ")", centerX + 20, questListY, 0xFFFFFF);
            questListY += 10;
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
