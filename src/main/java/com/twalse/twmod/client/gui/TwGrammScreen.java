package com.twalse.twmod.client.gui;

import com.twalse.twmod.quest.ClientQuestData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwGrammScreen extends Screen {
    private final Screen parent;
    private static final int PHONE_WIDTH = 150;
    private static final int PHONE_HEIGHT = 260;

    private String selectedChat = "boss";

    // Chat local response messages store
    private final Map<String, List<String>> localResponses = new HashMap<>();

    public TwGrammScreen(Screen parent) {
        super(Component.literal("TwGramm"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int phoneX = centerX - PHONE_WIDTH / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        int sendY = phoneY + PHONE_HEIGHT - 55;

        // Send Data response button
        this.addRenderableWidget(Button.builder(Component.literal("📩 Отправить данные"), btn -> {
            localResponses.computeIfAbsent(selectedChat, k -> new ArrayList<>()).add("Вы: Данные отправлены.");
        }).bounds(phoneX + 45, sendY, 98, 20).build());
    }

    private List<String> getCombinedMessages(String contactId) {
        List<String> result = new ArrayList<>();

        // Default initial story messages
        if ("boss".equalsIgnoreCase(contactId)) {
            result.add("Босс: Мне нужны фотографии товара.");
            result.add("Босс: Жду координаты места.");
        } else if ("dealer".equalsIgnoreCase(contactId)) {
            result.add("Барыга: Есть отличная партия отмычек.");
            result.add("Барыга: Загляни в маркет.");
        }

        // Add dynamically received server messages from capability
        List<String> dynamicMsgs = ClientQuestData.getMessagesForContact(contactId);
        if (dynamicMsgs != null) {
            result.addAll(dynamicMsgs);
        }

        // Add local responses sent during session
        List<String> userSent = localResponses.get(contactId);
        if (userSent != null) {
            result.addAll(userSent);
        }

        return result;
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

        // Left sidebar chat contact clicks
        int chatListX = phoneX + 4;
        int chatListWidth = 38;

        if (mouseX >= chatListX && mouseX <= chatListX + chatListWidth) {
            if (mouseY >= phoneY + 35 && mouseY <= phoneY + 65) {
                this.selectedChat = "boss";
                return true;
            } else if (mouseY >= phoneY + 70 && mouseY <= phoneY + 100) {
                this.selectedChat = "dealer";
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

        // Phone Frame
        guiGraphics.fill(phoneX - 6, phoneY - 10, phoneX + PHONE_WIDTH + 6, phoneY + PHONE_HEIGHT + 10, 0xFF1C1C1E);
        guiGraphics.fill(phoneX - 4, phoneY - 8, phoneX + PHONE_WIDTH + 4, phoneY + PHONE_HEIGHT + 8, 0xFF2C2C2E);
        guiGraphics.fill(phoneX, phoneY, phoneX + PHONE_WIDTH, phoneY + PHONE_HEIGHT, 0xFF0D0D11);

        // Header Title
        guiGraphics.drawCenteredString(this.font, "✈️ TwGramm", centerX, phoneY + 16, 0xFF17A2B8);

        // Left Contacts Sidebar
        int sidebarX = phoneX + 4;
        int sidebarY = phoneY + 32;
        int sidebarW = 38;
        int sidebarH = PHONE_HEIGHT - 62;

        guiGraphics.fill(sidebarX, sidebarY, sidebarX + sidebarW, sidebarY + sidebarH, 0xFF181820);

        // Boss Chat Contact Icon
        int bossColor = "boss".equals(selectedChat) ? 0xFFD4AF37 : 0xFF28A745;
        guiGraphics.fill(sidebarX + 4, sidebarY + 6, sidebarX + sidebarW - 4, sidebarY + 28, bossColor);
        guiGraphics.drawCenteredString(this.font, "Босс", sidebarX + sidebarW / 2, sidebarY + 13, 0xFFFFFFFF);

        // Dealer Chat Contact Icon
        int dealerColor = "dealer".equals(selectedChat) ? 0xFFD4AF37 : 0xFF6F42C1;
        guiGraphics.fill(sidebarX + 4, sidebarY + 34, sidebarX + sidebarW - 4, sidebarY + 56, dealerColor);
        guiGraphics.drawCenteredString(this.font, "Барыга", sidebarX + sidebarW / 2, sidebarY + 41, 0xFFFFFFFF);

        // Right Chat Message Window
        int chatX = sidebarX + sidebarW + 4;
        int chatY = sidebarY;
        int chatW = PHONE_WIDTH - sidebarW - 12;
        int chatH = sidebarH - 28;

        guiGraphics.fill(chatX, chatY, chatX + chatW, chatY + chatH, 0xFF121218);

        // Chat Header Name
        String chatName = "boss".equals(selectedChat) ? "Босс" : "Барыга";
        guiGraphics.drawString(this.font, "💬 " + chatName, chatX + 4, chatY + 4, 0xFFD4AF37, false);
        guiGraphics.fill(chatX + 2, chatY + 14, chatX + chatW - 2, chatY + 15, 0xFF333344);

        // Message History List
        List<String> messages = getCombinedMessages(selectedChat);
        int msgY = chatY + 20;

        for (String msg : messages) {
            if (msgY > chatY + chatH - 12) break;

            boolean isPlayer = msg.startsWith("Вы:");
            int msgTextColor = isPlayer ? 0xFF55FF55 : 0xFFDDDDDD;

            // Render message text scaled down (0.75f) to fit phone column width
            guiGraphics.pose().pushPose();
            float scale = 0.75f;
            guiGraphics.pose().translate(chatX + 4, msgY, 0);
            guiGraphics.pose().scale(scale, scale, 1.0f);

            guiGraphics.drawString(this.font, msg, 0, 0, msgTextColor, false);
            guiGraphics.pose().popPose();

            msgY += 14;
        }

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
