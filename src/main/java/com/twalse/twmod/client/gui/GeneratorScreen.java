package com.twalse.twmod.client.gui;

import com.twalse.twmod.block.GeneratorMenu;
import com.twalse.twmod.networking.PacketHandler;
import com.twalse.twmod.networking.message.StartGeneratorPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GeneratorScreen extends AbstractContainerScreen<GeneratorMenu> {

    public GeneratorScreen(GeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int btnX = this.leftPos + 90;
        int btnY = this.topPos + 32;
        int btnW = 60;
        int btnH = 22;

        if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
            if (this.menu.getBlockPos() != null) {
                PacketHandler.sendToServer(new StartGeneratorPacket(this.menu.getBlockPos()));
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // Custom programmatic GUI panel (Dark Grey #222222 with Gold Border)
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFF222222);
        guiGraphics.fill(x - 1, y - 1, x + this.imageWidth + 1, y, 0xFFD4AF37);
        guiGraphics.fill(x - 1, y + this.imageHeight, x + this.imageWidth + 1, y + this.imageHeight + 1, 0xFFD4AF37);
        guiGraphics.fill(x - 1, y, x, y + this.imageHeight, 0xFFD4AF37);
        guiGraphics.fill(x + this.imageWidth, y, x + this.imageWidth + 1, y + this.imageHeight, 0xFFD4AF37);

        // Fuel Slot background box
        guiGraphics.fill(x + 43, y + 34, x + 61, y + 52, 0xFF333333);
        guiGraphics.fill(x + 44, y + 35, x + 60, y + 51, 0xFF111111);

        // Player Inventory slots background boxes
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int sx = x + 7 + col * 18;
                int sy = y + 83 + row * 18;
                guiGraphics.fill(sx, sy, sx + 18, sy + 18, 0xFF333333);
                guiGraphics.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF181818);
            }
        }

        // Hotbar slots background boxes
        for (int col = 0; col < 9; ++col) {
            int sx = x + 7 + col * 18;
            int sy = y + 141;
            guiGraphics.fill(sx, sy, sx + 18, sy + 18, 0xFF333333);
            guiGraphics.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF181818);
        }

        // Custom "Start" button
        int btnX = x + 90;
        int btnY = y + 32;
        int btnW = 60;
        int btnH = 22;

        boolean isActive = this.menu.isActive();

        boolean hovered = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        int btnBgColor = isActive ? 0xFF34C759 : (hovered ? 0xFF00A0E6 : 0xFF0088CC);

        guiGraphics.fill(btnX, btnY, btnX + btnW, btnY + btnH, btnBgColor);
        guiGraphics.drawCenteredString(this.font, isActive ? "РАБОТАЕТ" : "Запустить", btnX + btnW / 2, btnY + 7, 0xFFFFFFFF);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 8, 0xFFD4AF37, false);
        guiGraphics.drawString(this.font, "Бензин", 40, 22, 0xAAAAAA, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
