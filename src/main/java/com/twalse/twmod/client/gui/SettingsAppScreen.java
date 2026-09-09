package com.twalse.twmod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SettingsAppScreen extends Screen {
    private final Screen parent;
    private static final int PHONE_WIDTH = 120;
    private static final int PHONE_HEIGHT = 220;

    public static boolean airplaneMode = false;
    private Button airplaneBtn;

    public SettingsAppScreen(Screen parent) {
        super(Component.literal("Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int phoneX = centerX - PHONE_WIDTH / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        int buttonY = phoneY + 50;

        // Toggle Airplane Mode
        this.airplaneBtn = Button.builder(Component.literal(airplaneMode ? "✈️ Режим полета: Вкл" : "✈️ Режим полета: Выкл"), btn -> {
            airplaneMode = !airplaneMode;
            btn.setMessage(Component.literal(airplaneMode ? "✈️ Режим полета: Вкл" : "✈️ Режим полета: Выкл"));
        }).bounds(phoneX + 8, buttonY, 104, 22).build();
        this.addRenderableWidget(this.airplaneBtn);

        // Clear Cache Button
        this.addRenderableWidget(Button.builder(Component.literal("🧹 Очистить кэш"), btn -> {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.sendSystemMessage(Component.literal("§aКэш TwOS очищен!"));
            }
            btn.setMessage(Component.literal("✓ Очищено"));
        }).bounds(phoneX + 8, buttonY + 30, 104, 22).build());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int phoneX = centerX - PHONE_WIDTH / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        // Home button
        int homeBtnX = centerX - 12;
        int homeBtnY = phoneY + PHONE_HEIGHT - 18;
        if (mouseX >= homeBtnX && mouseX <= homeBtnX + 24 && mouseY >= homeBtnY && mouseY <= homeBtnY + 10) {
            this.minecraft.setScreen(this.parent);
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

        // Phone Frame
        guiGraphics.fill(phoneX - 5, phoneY - 8, phoneX + PHONE_WIDTH + 5, phoneY + PHONE_HEIGHT + 8, 0xFF1C1C1E);
        guiGraphics.fill(phoneX - 3, phoneY - 6, phoneX + PHONE_WIDTH + 3, phoneY + PHONE_HEIGHT + 6, 0xFF2C2C2E);
        guiGraphics.fill(phoneX, phoneY, phoneX + PHONE_WIDTH, phoneY + PHONE_HEIGHT, 0xFF0D0D11);

        guiGraphics.drawCenteredString(this.font, "⚙️ НАСТРОЙКИ", centerX, phoneY + 18, 0xFFD4AF37);

        // Home Button
        int homeBtnX = centerX - 12;
        int homeBtnY = phoneY + PHONE_HEIGHT - 16;
        boolean homeHovered = mouseX >= homeBtnX && mouseX <= homeBtnX + 24 && mouseY >= homeBtnY && mouseY <= homeBtnY + 10;
        int homeColor = homeHovered ? 0xFFD4AF37 : 0xFF555555;

        guiGraphics.fill(homeBtnX, homeBtnY, homeBtnX + 24, homeBtnY + 8, homeColor);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
