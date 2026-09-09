package com.twalse.twmod.client.gui;

import com.twalse.twmod.quest.ClientQuestData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwGrammScreen extends Screen {
    public static final ResourceLocation BG_BLUE = new ResourceLocation("twmod", "textures/gui/phone_bg_blue.png");
    private final Screen parent;
    public static final int PHONE_WIDTH = PhoneScreen.PHONE_WIDTH;
    public static final int PHONE_HEIGHT = PhoneScreen.PHONE_HEIGHT;

    private String selectedChat = "boss";

    // Session local responses
    private final Map<String, List<String>> localResponses = new HashMap<>();

    public TwGrammScreen(Screen parent) {
        super(Component.literal("TwGramm"));
        this.parent = parent;
    }

    private List<String> getCombinedMessages(String contactId) {
        List<String> result = new ArrayList<>();

        if ("boss".equalsIgnoreCase(contactId)) {
            result.add("Босс: Мне нужны фотографии товара.");
            result.add("Босс: Жду координаты места.");
        } else if ("dealer".equalsIgnoreCase(contactId)) {
            result.add("Барыга: Есть отличная партия отмычек.");
            result.add("Барыга: Загляни в маркет.");
        }

        List<String> dynamicMsgs = ClientQuestData.getMessagesForContact(contactId);
        if (dynamicMsgs != null) {
            result.addAll(dynamicMsgs);
        }

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

        // Bottom Home Indicator click
        if (mouseY >= phoneY + PHONE_HEIGHT - 18 && mouseY <= phoneY + PHONE_HEIGHT - 2 &&
            mouseX >= phoneX && mouseX <= phoneX + PHONE_WIDTH) {
            this.minecraft.setScreen(this.parent);
            return true;
        }

        // Left sidebar contact list clicks
        int sidebarX = phoneX + 4;
        int sidebarW = 28;

        if (mouseX >= sidebarX && mouseX <= sidebarX + sidebarW) {
            if (mouseY >= phoneY + 28 && mouseY <= phoneY + 50) {
                this.selectedChat = "boss";
                return true;
            } else if (mouseY >= phoneY + 54 && mouseY <= phoneY + 76) {
                this.selectedChat = "dealer";
                return true;
            }
        }

        // Custom "Отправить данные" button click
        int sendBtnX = phoneX + 34;
        int sendBtnY = phoneY + PHONE_HEIGHT - 38;
        int sendBtnW = PHONE_WIDTH - 38;
        int sendBtnH = 16;

        if (mouseX >= sendBtnX && mouseX <= sendBtnX + sendBtnW && mouseY >= sendBtnY && mouseY <= sendBtnY + sendBtnH) {
            localResponses.computeIfAbsent(selectedChat, k -> new ArrayList<>()).add("Вы: Данные отправлены.");
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

        // Background: phone_bg_blue.png
        guiGraphics.blit(BG_BLUE, phoneX, phoneY, 0.0F, 0.0F, PHONE_WIDTH, PHONE_HEIGHT, PHONE_WIDTH, PHONE_HEIGHT);

        // Header Title
        guiGraphics.drawString(this.font, "✈️ TwGramm", phoneX + 8, phoneY + 14, 0xFF70C2F0, false);

        // Left Contacts Sidebar
        int sidebarX = phoneX + 4;
        int sidebarY = phoneY + 26;
        int sidebarW = 28;
        int sidebarH = PHONE_HEIGHT - 48;

        guiGraphics.fill(sidebarX, sidebarY, sidebarX + sidebarW, sidebarY + sidebarH, 0xFF141D26);

        // Contact 1: Boss
        int bossColor = "boss".equalsIgnoreCase(selectedChat) ? 0xFF0088CC : 0xFF242F3D;
        guiGraphics.fill(sidebarX + 2, sidebarY + 2, sidebarX + sidebarW - 2, sidebarY + 24, bossColor);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(sidebarX + 4, sidebarY + 8, 0);
        guiGraphics.pose().scale(0.65f, 0.65f, 1.0f);
        guiGraphics.drawString(this.font, "Босс", 0, 0, 0xFFFFFFFF, false);
        guiGraphics.pose().popPose();

        // Contact 2: Dealer
        int dealerColor = "dealer".equalsIgnoreCase(selectedChat) ? 0xFF0088CC : 0xFF242F3D;
        guiGraphics.fill(sidebarX + 2, sidebarY + 26, sidebarX + sidebarW - 2, sidebarY + 48, dealerColor);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(sidebarX + 4, sidebarY + 32, 0);
        guiGraphics.pose().scale(0.65f, 0.65f, 1.0f);
        guiGraphics.drawString(this.font, "Барыга", 0, 0, 0xFFFFFFFF, false);
        guiGraphics.pose().popPose();

        // Right Chat Window
        int chatX = sidebarX + sidebarW + 2;
        int chatY = sidebarY;
        int chatW = PHONE_WIDTH - sidebarW - 8;
        int chatH = PHONE_HEIGHT - 66;

        guiGraphics.fill(chatX, chatY, chatX + chatW, chatY + chatH, 0xFF0E1621);

        // Chat Header Name
        String chatName = "boss".equalsIgnoreCase(selectedChat) ? "Босс" : "Барыга";
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(chatX + 4, chatY + 4, 0);
        guiGraphics.pose().scale(0.75f, 0.75f, 1.0f);
        guiGraphics.drawString(this.font, "💬 " + chatName, 0, 0, 0xFF70C2F0, false);
        guiGraphics.pose().popPose();

        // Chat Message History
        List<String> messages = getCombinedMessages(selectedChat);
        int msgY = chatY + 16;
        float textScale = 0.60f;
        int maxTextWidth = (int) ((chatW - 8) / textScale);

        for (String msg : messages) {
            if (msgY > chatY + chatH - 12) break;

            boolean isPlayer = msg.startsWith("Вы:");
            int bubbleColor = isPlayer ? 0xFF2B5278 : 0xFF182533;
            int textColor = isPlayer ? 0xFFBEE6FF : 0xFFE0E0E0;

            List<FormattedCharSequence> wrappedLines = this.font.split(Component.literal(msg), maxTextWidth);
            int bubbleHeight = (int) (wrappedLines.size() * (9 * textScale) + 4);

            if (msgY + bubbleHeight > chatY + chatH) break;

            guiGraphics.fill(chatX + 2, msgY, chatX + chatW - 2, msgY + bubbleHeight, bubbleColor);

            int lineY = msgY + 2;
            for (FormattedCharSequence line : wrappedLines) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(chatX + 4, lineY, 0);
                guiGraphics.pose().scale(textScale, textScale, 1.0f);
                guiGraphics.drawString(this.font, line, 0, 0, textColor, false);
                guiGraphics.pose().popPose();

                lineY += (int) (9 * textScale);
            }

            msgY += bubbleHeight + 3;
        }

        // Custom "Отправить данные" Flat Button - Centered text precisely
        int sendBtnX = chatX;
        int sendBtnY = phoneY + PHONE_HEIGHT - 38;
        int sendBtnW = chatW;
        int sendBtnH = 16;

        boolean sendHovered = mouseX >= sendBtnX && mouseX <= sendBtnX + sendBtnW && mouseY >= sendBtnY && mouseY <= sendBtnY + sendBtnH;
        int sendBtnColor = sendHovered ? 0xFF0099E6 : 0xFF0088CC;

        guiGraphics.fill(sendBtnX, sendBtnY, sendBtnX + sendBtnW, sendBtnY + sendBtnH, sendBtnColor);

        String btnLabel = "📩 Отправить";
        float btnScale = 0.65f;
        int labelW = (int) (this.font.width(btnLabel) * btnScale);
        float textX = sendBtnX + (sendBtnW - labelW) / 2.0f;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(textX, sendBtnY + 4, 0);
        guiGraphics.pose().scale(btnScale, btnScale, 1.0f);
        guiGraphics.drawString(this.font, btnLabel, 0, 0, 0xFFFFFFFF, false);
        guiGraphics.pose().popPose();

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
