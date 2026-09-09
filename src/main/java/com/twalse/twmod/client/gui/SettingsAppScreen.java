package com.twalse.twmod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SettingsAppScreen extends Screen {
    private final Screen parent;
    public static final int PHONE_WIDTH = PhoneScreen.PHONE_WIDTH;
    public static final int PHONE_HEIGHT = PhoneScreen.PHONE_HEIGHT;

    public static boolean airplaneMode = false;
    private String cacheMessage = null;

    public SettingsAppScreen(Screen parent) {
        super(Component.literal("Settings"));
        this.parent = parent;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int phoneX = centerX - PHONE_WIDTH / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        // Bottom Home Indicator click
        if (mouseY >= phoneY + PHONE_HEIGHT - 18 && mouseY <= phoneY + PHONE_HEIGHT - 2 &&
            mouseX >= phoneX && mouseX <= phoneX + PHONE_WIDTH) {
            this.minecraft.setScreen(this.parent);
            return true;
        }

        int itemX = phoneX + 6;
        int itemW = PHONE_WIDTH - 12;
        int startY = phoneY + 42;
        int itemH = 26;

        if (mouseX >= itemX && mouseX <= itemX + itemW) {
            // Click Airplane Mode Item
            if (mouseY >= startY && mouseY <= startY + itemH) {
                airplaneMode = !airplaneMode;
                return true;
            }
            // Click Clear Cache Item
            else if (mouseY >= startY + 32 && mouseY <= startY + 32 + itemH) {
                if (this.minecraft != null && this.minecraft.player != null) {
                    this.minecraft.player.sendSystemMessage(Component.literal("§aКэш TwOS очищен!"));
                }
                cacheMessage = "Очищено";
                return true;
            }
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

        // Background: phone_bg_dark.png
        guiGraphics.blit(PhoneScreen.BG_DARK, phoneX, phoneY, 0.0F, 0.0F, PHONE_WIDTH, PHONE_HEIGHT, PHONE_WIDTH, PHONE_HEIGHT);

        // Header Title
        guiGraphics.drawCenteredString(this.font, "Настройки", centerX, phoneY + 18, 0xFFFFFFFF);

        int itemX = phoneX + 6;
        int itemW = PHONE_WIDTH - 12;
        int startY = phoneY + 42;
        int itemH = 26;

        // 1. iOS-style Airplane Mode Card
        boolean airHovered = mouseX >= itemX && mouseX <= itemX + itemW && mouseY >= startY && mouseY <= startY + itemH;
        guiGraphics.fill(itemX, startY, itemX + itemW, startY + itemH, airHovered ? 0xFF3A3A3C : 0xFF2C2C2E);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(itemX + 8, startY + 9, 0);
        guiGraphics.pose().scale(0.85f, 0.85f, 1.0f);
        guiGraphics.drawString(this.font, "✈️ Режим полета", 0, 0, 0xFFFFFFFF, false);
        guiGraphics.pose().popPose();

        // Green/Gray toggle indicator square
        int toggleColor = airplaneMode ? 0xFF34C759 : 0xFF8E8E93;
        guiGraphics.fill(itemX + itemW - 18, startY + 8, itemX + itemW - 8, startY + 18, toggleColor);

        // 2. iOS-style Clear Cache Card
        int cacheY = startY + 32;
        boolean cacheHovered = mouseX >= itemX && mouseX <= itemX + itemW && mouseY >= cacheY && mouseY <= cacheY + itemH;
        guiGraphics.fill(itemX, cacheY, itemX + itemW, cacheY + itemH, cacheHovered ? 0xFF3A3A3C : 0xFF2C2C2E);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(itemX + 8, cacheY + 9, 0);
        guiGraphics.pose().scale(0.85f, 0.85f, 1.0f);
        guiGraphics.drawString(this.font, "🧹 Очистить кэш", 0, 0, 0xFFFFFFFF, false);
        guiGraphics.pose().popPose();

        if (cacheMessage != null) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(itemX + itemW - 42, cacheY + 9, 0);
            guiGraphics.pose().scale(0.75f, 0.75f, 1.0f);
            guiGraphics.drawString(this.font, cacheMessage, 0, 0, 0xFF34C759, false);
            guiGraphics.pose().popPose();
        }

        // iPhone 17 Home Indicator Bar
        int navBarX = centerX - 16;
        int navBarY = phoneY + PHONE_HEIGHT - 10;
        guiGraphics.fill(navBarX, navBarY, navBarX + 32, navBarY + 3, 0xDDFFFFFF);

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
