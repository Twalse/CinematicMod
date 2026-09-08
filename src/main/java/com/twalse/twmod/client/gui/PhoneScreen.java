package com.twalse.twmod.client.gui;

import com.twalse.twmod.quest.ClientQuestData;
import com.twalse.twmod.quest.QuestData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

public class PhoneScreen extends Screen {

    public enum AppState {
        HOME,
        QUESTS,
        BANK
    }

    private AppState currentState = AppState.HOME;

    // Phone dimensions
    private static final int PHONE_WIDTH = 140;
    private static final int PHONE_HEIGHT = 250;

    private Button questsAppButton;
    private Button bankAppButton;

    public PhoneScreen() {
        super(Component.literal("Smartphone"));
    }

    @Override
    protected void init() {
        super.init();
        updateWidgets();
    }

    private void updateWidgets() {
        this.clearWidgets();

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int phoneX = centerX - PHONE_WIDTH / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        if (currentState == AppState.HOME) {
            // App 1: Contracts / Quests
            this.questsAppButton = Button.builder(Component.literal("📜 Контракты"), btn -> {
                this.currentState = AppState.QUESTS;
                updateWidgets();
            }).bounds(phoneX + 15, phoneY + 40, 110, 30).build();

            // App 2: Darknet Bank
            this.bankAppButton = Button.builder(Component.literal("💳 Даркнет-Банк"), btn -> {
                this.currentState = AppState.BANK;
                updateWidgets();
            }).bounds(phoneX + 15, phoneY + 80, 110, 30).build();

            this.addRenderableWidget(this.questsAppButton);
            this.addRenderableWidget(this.bankAppButton);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int phoneX = centerX - PHONE_WIDTH / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        // Check Home Button click at bottom center of phone
        int homeBtnX = centerX - 15;
        int homeBtnY = phoneY + PHONE_HEIGHT - 22;
        if (mouseX >= homeBtnX && mouseX <= homeBtnX + 30 && mouseY >= homeBtnY && mouseY <= homeBtnY + 12) {
            this.currentState = AppState.HOME;
            updateWidgets();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int phoneX = centerX - PHONE_WIDTH / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        // 1. Phone Outer Frame (Gray/Black Bezel)
        guiGraphics.fill(phoneX - 6, phoneY - 10, phoneX + PHONE_WIDTH + 6, phoneY + PHONE_HEIGHT + 10, 0xFF1C1C1E);
        guiGraphics.fill(phoneX - 4, phoneY - 8, phoneX + PHONE_WIDTH + 4, phoneY + PHONE_HEIGHT + 8, 0xFF2C2C2E);

        // 2. Phone Screen Display (Dark Background)
        guiGraphics.fill(phoneX, phoneY, phoneX + PHONE_WIDTH, phoneY + PHONE_HEIGHT, 0xFF0D0D11);

        // 3. Top Speaker / Camera Notch
        guiGraphics.fill(centerX - 18, phoneY - 5, centerX + 18, phoneY - 2, 0xFF000000);

        // 4. Status Bar
        guiGraphics.drawString(this.font, "12:00", phoneX + 8, phoneY + 6, 0x888888, false);
        guiGraphics.drawString(this.font, "5G ⚡", phoneX + PHONE_WIDTH - 30, phoneY + 6, 0x888888, false);
        guiGraphics.fill(phoneX + 6, phoneY + 18, phoneX + PHONE_WIDTH - 6, phoneY + 19, 0xFF333333);

        // 5. Render Active App Content
        switch (currentState) {
            case HOME -> renderHomeApp(guiGraphics, phoneX, phoneY);
            case QUESTS -> renderQuestsApp(guiGraphics, phoneX, phoneY);
            case BANK -> renderBankApp(guiGraphics, phoneX, phoneY);
        }

        // 6. Bottom Home Button
        int homeBtnX = centerX - 15;
        int homeBtnY = phoneY + PHONE_HEIGHT - 20;
        boolean homeHovered = mouseX >= homeBtnX && mouseX <= homeBtnX + 30 && mouseY >= homeBtnY && mouseY <= homeBtnY + 12;
        int homeColor = homeHovered ? 0xFFD4AF37 : 0xFF555555;

        guiGraphics.fill(homeBtnX, homeBtnY, homeBtnX + 30, homeBtnY + 10, homeColor);
        guiGraphics.drawCenteredString(this.font, "—", centerX, homeBtnY + 1, 0xFFFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderHomeApp(GuiGraphics guiGraphics, int phoneX, int phoneY) {
        int centerX = phoneX + PHONE_WIDTH / 2;
        guiGraphics.drawCenteredString(this.font, "TwPhone OS", centerX, phoneY + 24, 0xFFD4AF37);
    }

    private void renderQuestsApp(GuiGraphics guiGraphics, int phoneX, int phoneY) {
        int centerX = phoneX + PHONE_WIDTH / 2;
        guiGraphics.drawCenteredString(this.font, "АКТИВНЫЕ КОНТРАКТЫ", centerX, phoneY + 24, 0xFFD4AF37);

        List<QuestData> quests = ClientQuestData.getQuests();
        int contentY = phoneY + 40;

        if (quests.isEmpty()) {
            guiGraphics.drawCenteredString(this.font, "Нет активных контрактов", centerX, phoneY + 100, 0x777777);
        } else {
            for (QuestData quest : quests) {
                if (contentY > phoneY + PHONE_HEIGHT - 35) break;

                // Quest Card Box
                guiGraphics.fill(phoneX + 8, contentY, phoneX + PHONE_WIDTH - 8, contentY + 36, 0xFF181820);
                guiGraphics.fill(phoneX + 8, contentY, phoneX + 10, contentY + 36, 0xFFD4AF37);

                guiGraphics.drawString(this.font, quest.getDescription(), phoneX + 14, contentY + 6, 0xFFFFFF, false);

                if (quest.getMaxProgress() > 0) {
                    String pStr = quest.getCurrentProgress() + " / " + quest.getMaxProgress();
                    guiGraphics.drawString(this.font, pStr, phoneX + 14, contentY + 18, 0xAAAAAA, false);
                }

                contentY += 42;
            }
        }
    }

    private void renderBankApp(GuiGraphics guiGraphics, int phoneX, int phoneY) {
        int centerX = phoneX + PHONE_WIDTH / 2;
        guiGraphics.drawCenteredString(this.font, "ДАРКНЕТ-БАНК", centerX, phoneY + 24, 0xFFD4AF37);

        Map<String, Integer> vars = ClientQuestData.getVariables();
        int money = vars.getOrDefault("money", vars.getOrDefault("coins", 0));
        int wanted = vars.getOrDefault("wanted", 0);

        // Balance Card
        int cardY = phoneY + 45;
        guiGraphics.fill(phoneX + 10, cardY, phoneX + PHONE_WIDTH - 10, cardY + 50, 0xFF1C1C28);
        guiGraphics.fill(phoneX + 10, cardY, phoneX + PHONE_WIDTH - 10, cardY + 2, 0xFFD4AF37);

        guiGraphics.drawCenteredString(this.font, "Текущий баланс:", centerX, cardY + 8, 0xAAAAAA);
        guiGraphics.drawCenteredString(this.font, money + " 🪙", centerX, cardY + 24, 0xFFFF55);

        // Wanted Status Card
        int wantedY = cardY + 65;
        guiGraphics.fill(phoneX + 10, wantedY, phoneX + PHONE_WIDTH - 10, wantedY + 45, 0xFF1C1C28);

        guiGraphics.drawCenteredString(this.font, "Статус розыска:", centerX, wantedY + 8, 0xAAAAAA);
        if (wanted > 0) {
            guiGraphics.drawCenteredString(this.font, "Уровень " + wanted + " ★", centerX, wantedY + 24, 0xFF5555);
        } else {
            guiGraphics.drawCenteredString(this.font, "Чист ✓", centerX, wantedY + 24, 0x55FF55);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
