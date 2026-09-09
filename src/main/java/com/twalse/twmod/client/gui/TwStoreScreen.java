package com.twalse.twmod.client.gui;

import com.twalse.twmod.quest.ClientQuestData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TwStoreScreen extends Screen {
    public static final ResourceLocation BG_LIGHT = new ResourceLocation("twmod", "textures/gui/phone_bg_light.png");
    private final Screen parent;

    public static final int PHONE_WIDTH = PhoneScreen.PHONE_WIDTH;
    public static final int PHONE_HEIGHT = PhoneScreen.PHONE_HEIGHT;

    public TwStoreScreen(Screen parent) {
        super(Component.literal("TwStore"));
        this.parent = parent;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int phoneX = centerX - PHONE_WIDTH / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        // Bottom Home Indicator click (return to desktop)
        if (mouseY >= phoneY + PHONE_HEIGHT - 18 && mouseY <= phoneY + PHONE_HEIGHT - 2 &&
            mouseX >= phoneX && mouseX <= phoneX + PHONE_WIDTH) {
            this.minecraft.setScreen(this.parent);
            return true;
        }

        // Custom App Store Items
        int listX = phoneX + 8;
        int itemW = PHONE_WIDTH - 16;
        int startY = phoneY + 36;
        int itemH = 24;

        if (mouseX >= listX && mouseX <= listX + itemW) {
            if (mouseY >= startY && mouseY <= startY + itemH) {
                // Install Dino
                if (this.minecraft != null && this.minecraft.player != null) {
                    this.minecraft.player.connection.sendCommand("tw phone install @s dino");
                }
                return true;
            } else if (mouseY >= startY + 30 && mouseY <= startY + 30 + itemH) {
                // Install TwGramm
                if (this.minecraft != null && this.minecraft.player != null) {
                    this.minecraft.player.connection.sendCommand("tw phone install @s twgramm");
                }
                return true;
            } else if (mouseY >= startY + 60 && mouseY <= startY + 60 + itemH) {
                // Install Camera Suite
                if (this.minecraft != null && this.minecraft.player != null) {
                    this.minecraft.player.connection.sendCommand("tw phone install @s camera");
                    this.minecraft.player.connection.sendCommand("tw phone install @s gallery");
                }
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

        // Background: phone_bg_light.png
        guiGraphics.blit(BG_LIGHT, phoneX, phoneY, 0.0F, 0.0F, PHONE_WIDTH, PHONE_HEIGHT, PHONE_WIDTH, PHONE_HEIGHT);

        // Header (Dark text for light wallpaper)
        guiGraphics.drawCenteredString(this.font, "App Store", centerX, phoneY + 18, 0xFF1D1D1F);

        // App Store List Items
        int listX = phoneX + 8;
        int itemW = PHONE_WIDTH - 16;
        int startY = phoneY + 36;
        int itemH = 24;

        renderStoreItem(guiGraphics, mouseX, mouseY, listX, startY, itemW, itemH, "Dino Game", ClientQuestData.isAppInstalled("dino"));
        renderStoreItem(guiGraphics, mouseX, mouseY, listX, startY + 30, itemW, itemH, "TwGramm", ClientQuestData.isAppInstalled("twgramm"));
        renderStoreItem(guiGraphics, mouseX, mouseY, listX, startY + 60, itemW, itemH, "Camera Suite", ClientQuestData.isAppInstalled("camera"));

        // iPhone 17 Home Indicator Bar (Dark for light wallpaper)
        int navBarX = centerX - 16;
        int navBarY = phoneY + PHONE_HEIGHT - 10;
        guiGraphics.fill(navBarX, navBarY, navBarX + 32, navBarY + 3, 0xFF333333);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderStoreItem(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y, int w, int h, String name, boolean installed) {
        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        int bgColor = hovered ? 0xFFE0E0E6 : 0xFFF2F2F7;

        guiGraphics.fill(x, y, x + w, y + h, bgColor);

        guiGraphics.drawString(this.font, name, x + 6, y + 8, 0xFF1C1C1E, false);

        String btnText = installed ? "✓" : "GET";
        int btnTextColor = installed ? 0xFF34C759 : 0xFF007AFF;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + w - 24, y + 8, 0);
        guiGraphics.drawString(this.font, btnText, 0, 0, btnTextColor, false);
        guiGraphics.pose().popPose();
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
