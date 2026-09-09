package com.twalse.twmod.client.gui;

import com.twalse.twmod.networking.PacketHandler;
import com.twalse.twmod.networking.message.ExecuteContactActionPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ContactsAppScreen extends Screen {
    private final Screen parent;
    public static final int PHONE_WIDTH = PhoneScreen.PHONE_WIDTH;
    public static final int PHONE_HEIGHT = PhoneScreen.PHONE_HEIGHT;

    public ContactsAppScreen(Screen parent) {
        super(Component.literal("Contacts"));
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

        int listX = phoneX + 8;
        int itemW = PHONE_WIDTH - 16;
        int startY = phoneY + 36;
        int itemH = 24;

        if (mouseX >= listX && mouseX <= listX + itemW) {
            if (mouseY >= startY && mouseY <= startY + itemH) {
                PacketHandler.sendToServer(new ExecuteContactActionPacket("boss"));
                return true;
            } else if (mouseY >= startY + 28 && mouseY <= startY + 28 + itemH) {
                PacketHandler.sendToServer(new ExecuteContactActionPacket("dealer"));
                return true;
            } else if (mouseY >= startY + 56 && mouseY <= startY + 56 + itemH) {
                PacketHandler.sendToServer(new ExecuteContactActionPacket("informant"));
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
        guiGraphics.drawCenteredString(this.font, "Контакты", centerX, phoneY + 18, 0xFFE0E0E0);

        int listX = phoneX + 8;
        int itemW = PHONE_WIDTH - 16;
        int startY = phoneY + 36;
        int itemH = 24;

        renderContactCard(guiGraphics, mouseX, mouseY, listX, startY, itemW, itemH, "📞 Босс");
        renderContactCard(guiGraphics, mouseX, mouseY, listX, startY + 28, itemW, itemH, "📞 Барыга");
        renderContactCard(guiGraphics, mouseX, mouseY, listX, startY + 56, itemW, itemH, "📞 Информатор");

        // iPhone 17 Home Indicator Bar
        int navBarX = centerX - 16;
        int navBarY = phoneY + PHONE_HEIGHT - 10;
        guiGraphics.fill(navBarX, navBarY, navBarX + 32, navBarY + 3, 0xDDFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderContactCard(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y, int w, int h, String name) {
        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        int bgColor = hovered ? 0xFF3A3A3C : 0xFF2C2C2E;

        guiGraphics.fill(x, y, x + w, y + h, bgColor);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + 6, y + 8, 0);
        guiGraphics.pose().scale(0.75f, 0.75f, 1.0f);
        guiGraphics.drawString(this.font, name, 0, 0, 0xFFFFFFFF, false);
        guiGraphics.pose().popPose();

        // Subtle 1px divider
        guiGraphics.fill(x + 2, y + h + 1, x + w - 2, y + h + 2, 0xFF1C1C1E);
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
