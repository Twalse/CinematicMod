package com.twalse.twmod.client.gui;

import com.twalse.twmod.quest.ClientQuestData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TwStoreScreen extends Screen {
    private final Screen parent;
    private static final int PHONE_WIDTH = 140;
    private static final int PHONE_HEIGHT = 250;

    public TwStoreScreen(Screen parent) {
        super(Component.literal("TwStore"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int phoneX = centerX - PHONE_WIDTH / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        int buttonY = phoneY + 45;

        // Install Dino Game
        this.addRenderableWidget(Button.builder(Component.literal(ClientQuestData.isAppInstalled("dino") ? "✓ Dino" : "📥 Dino Game"), btn -> {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.connection.sendCommand("tw var add @s install_dino 1");
            }
            btn.setMessage(Component.literal("✓ Dino"));
        }).bounds(phoneX + 12, buttonY, 116, 24).build());

        // Install TwGramm
        this.addRenderableWidget(Button.builder(Component.literal(ClientQuestData.isAppInstalled("twgramm") ? "✓ TwGramm" : "📥 TwGramm"), btn -> {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.connection.sendCommand("tw var add @s install_twgramm 1");
            }
            btn.setMessage(Component.literal("✓ TwGramm"));
        }).bounds(phoneX + 12, buttonY + 30, 116, 24).build());

        // Install Camera & Gallery
        this.addRenderableWidget(Button.builder(Component.literal(ClientQuestData.isAppInstalled("camera") ? "✓ Camera" : "📥 Camera Suite"), btn -> {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.connection.sendCommand("tw var add @s install_camera 1");
            }
            btn.setMessage(Component.literal("✓ Camera"));
        }).bounds(phoneX + 12, buttonY + 60, 116, 24).build());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int phoneX = centerX - PHONE_WIDTH / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        int homeBtnX = centerX - 15;
        int homeBtnY = phoneY + PHONE_HEIGHT - 22;
        if (mouseX >= homeBtnX && mouseX <= homeBtnX + 30 && mouseY >= homeBtnY && mouseY <= homeBtnY + 12) {
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

        guiGraphics.fill(phoneX - 6, phoneY - 10, phoneX + PHONE_WIDTH + 6, phoneY + PHONE_HEIGHT + 10, 0xFF1C1C1E);
        guiGraphics.fill(phoneX - 4, phoneY - 8, phoneX + PHONE_WIDTH + 4, phoneY + PHONE_HEIGHT + 8, 0xFF2C2C2E);
        guiGraphics.fill(phoneX, phoneY, phoneX + PHONE_WIDTH, phoneY + PHONE_HEIGHT, 0xFF0D0D11);

        guiGraphics.drawCenteredString(this.font, "TwStore", centerX, phoneY + 18, 0xFFD4AF37);

        int homeBtnX = centerX - 15;
        int homeBtnY = phoneY + PHONE_HEIGHT - 20;
        boolean homeHovered = mouseX >= homeBtnX && mouseX <= homeBtnX + 30 && mouseY >= homeBtnY && mouseY <= homeBtnY + 12;
        int homeColor = homeHovered ? 0xFFD4AF37 : 0xFF555555;

        guiGraphics.fill(homeBtnX, homeBtnY, homeBtnX + 30, homeBtnY + 10, homeColor);
        guiGraphics.drawCenteredString(this.font, "—", centerX, homeBtnY + 1, 0xFFFFFFFF);

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
