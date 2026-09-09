package com.twalse.twmod.client.gui;

import com.twalse.twmod.networking.PacketHandler;
import com.twalse.twmod.networking.message.BuyItemPacket;
import com.twalse.twmod.quest.ClientQuestData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class MarketAppScreen extends Screen {
    private final Screen parent;
    public static final int PHONE_WIDTH = PhoneScreen.PHONE_WIDTH;
    public static final int PHONE_HEIGHT = PhoneScreen.PHONE_HEIGHT;

    public MarketAppScreen(Screen parent) {
        super(Component.literal("Market"));
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

        int listX = phoneX + 6;
        int itemW = PHONE_WIDTH - 12;
        int startY = phoneY + 44;
        int itemH = 22;

        if (mouseX >= listX && mouseX <= listX + itemW) {
            if (mouseY >= startY && mouseY <= startY + itemH) {
                PacketHandler.sendToServer(new BuyItemPacket("lockpick"));
                return true;
            } else if (mouseY >= startY + 25 && mouseY <= startY + 25 + itemH) {
                PacketHandler.sendToServer(new BuyItemPacket("medkit"));
                return true;
            } else if (mouseY >= startY + 50 && mouseY <= startY + 50 + itemH) {
                PacketHandler.sendToServer(new BuyItemPacket("syringe"));
                return true;
            } else if (mouseY >= startY + 75 && mouseY <= startY + 75 + itemH) {
                PacketHandler.sendToServer(new BuyItemPacket("tacz_9mm"));
                return true;
            } else if (mouseY >= startY + 100 && mouseY <= startY + 100 + itemH) {
                PacketHandler.sendToServer(new BuyItemPacket("heavy_cargo"));
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

        // Header
        guiGraphics.drawCenteredString(this.font, "ДАРКНЕТ МАРКЕТ", centerX, phoneY + 16, 0xFFD4AF37);

        // Balance
        Map<String, Integer> vars = ClientQuestData.getVariables();
        int money = vars.getOrDefault("money", vars.getOrDefault("coins", 0));
        guiGraphics.drawCenteredString(this.font, "Баланс: " + money + " 🪙", centerX, phoneY + 28, 0xFFFF55);

        int listX = phoneX + 6;
        int itemW = PHONE_WIDTH - 12;
        int startY = phoneY + 44;
        int itemH = 22;

        renderMarketItem(guiGraphics, mouseX, mouseY, listX, startY, itemW, itemH, "🛠️ Отмычка", "50🪙");
        renderMarketItem(guiGraphics, mouseX, mouseY, listX, startY + 25, itemW, itemH, "🩹 Аптечка", "200🪙");
        renderMarketItem(guiGraphics, mouseX, mouseY, listX, startY + 50, itemW, itemH, "💉 Шприц", "150🪙");
        renderMarketItem(guiGraphics, mouseX, mouseY, listX, startY + 75, itemW, itemH, "🔫 Патроны 9mm", "100🪙");
        renderMarketItem(guiGraphics, mouseX, mouseY, listX, startY + 100, itemW, itemH, "📦 Контрабанда", "300🪙");

        // iPhone 17 Home Indicator Bar
        int navBarX = centerX - 16;
        int navBarY = phoneY + PHONE_HEIGHT - 10;
        guiGraphics.fill(navBarX, navBarY, navBarX + 32, navBarY + 3, 0xDDFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderMarketItem(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y, int w, int h, String name, String price) {
        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        int bgColor = hovered ? 0xFF3A3A3C : 0xFF2C2C2E;

        guiGraphics.fill(x, y, x + w, y + h, bgColor);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + 4, y + 7, 0);
        guiGraphics.pose().scale(0.85f, 0.85f, 1.0f);
        guiGraphics.drawString(this.font, name, 0, 0, 0xFFFFFFFF, false);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + w - 34, y + 7, 0);
        guiGraphics.pose().scale(0.8f, 0.8f, 1.0f);
        guiGraphics.drawString(this.font, price, 0, 0, 0xFFFF55, false);
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
