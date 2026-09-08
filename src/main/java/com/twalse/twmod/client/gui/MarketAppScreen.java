package com.twalse.twmod.client.gui;

import com.twalse.twmod.networking.PacketHandler;
import com.twalse.twmod.networking.message.BuyItemPacket;
import com.twalse.twmod.quest.ClientQuestData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class MarketAppScreen extends Screen {
    private final Screen parent;
    private static final int PHONE_WIDTH = 140;
    private static final int PHONE_HEIGHT = 250;

    public MarketAppScreen(Screen parent) {
        super(Component.literal("Market"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int phoneX = centerX - PHONE_WIDTH / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        int buttonY = phoneY + 65;

        // Item 1: Heavy Cargo (100)
        this.addRenderableWidget(Button.builder(Component.literal("📦 Груз (100🪙)"), btn -> {
            PacketHandler.sendToServer(new BuyItemPacket("heavy_cargo"));
        }).bounds(phoneX + 12, buttonY, 116, 24).build());

        // Item 2: Medkit (50)
        this.addRenderableWidget(Button.builder(Component.literal("🍎 Аптечка (50🪙)"), btn -> {
            PacketHandler.sendToServer(new BuyItemPacket("medkit"));
        }).bounds(phoneX + 12, buttonY + 30, 116, 24).build());

        // Item 3: Lockpick Kit (30)
        this.addRenderableWidget(Button.builder(Component.literal("🛠️ Набор (30🪙)"), btn -> {
            PacketHandler.sendToServer(new BuyItemPacket("lockpick_kit"));
        }).bounds(phoneX + 12, buttonY + 60, 116, 24).build());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int phoneX = centerX - PHONE_WIDTH / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        // Check Home Button click
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

        // Phone Frame & Display
        guiGraphics.fill(phoneX - 6, phoneY - 10, phoneX + PHONE_WIDTH + 6, phoneY + PHONE_HEIGHT + 10, 0xFF1C1C1E);
        guiGraphics.fill(phoneX - 4, phoneY - 8, phoneX + PHONE_WIDTH + 4, phoneY + PHONE_HEIGHT + 8, 0xFF2C2C2E);
        guiGraphics.fill(phoneX, phoneY, phoneX + PHONE_WIDTH, phoneY + PHONE_HEIGHT, 0xFF0D0D11);

        // Header
        guiGraphics.drawCenteredString(this.font, "ДАРКНЕТ МАРКЕТ", centerX, phoneY + 18, 0xFFD4AF37);

        // Display current money balance
        Map<String, Integer> vars = ClientQuestData.getVariables();
        int money = vars.getOrDefault("money", vars.getOrDefault("coins", 0));
        guiGraphics.drawCenteredString(this.font, "Баланс: " + money + " 🪙", centerX, phoneY + 38, 0xFFFF55);

        // Bottom Home Button
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
