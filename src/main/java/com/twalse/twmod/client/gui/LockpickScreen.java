package com.twalse.twmod.client.gui;

import com.twalse.twmod.networking.PacketHandler;
import com.twalse.twmod.networking.message.LockpickResultPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.Random;

public class LockpickScreen extends Screen {
    private final int pinsCount;
    private int pickHealth;
    private final BlockPos doorPos;

    private final float[] pinHeights;
    private final float[] targetHeights;
    private final boolean[] isLocked;

    private int currentPin = 0;
    private boolean isPushing = false;
    private boolean finished = false;

    public LockpickScreen(int pinsCount, int pickHealth) {
        this(pinsCount, pickHealth, null);
    }

    public LockpickScreen(int pinsCount, int pickHealth, BlockPos doorPos) {
        super(Component.literal("Lockpicking"));
        this.pinsCount = Math.max(1, pinsCount);
        this.pickHealth = Math.max(1, pickHealth);
        this.doorPos = doorPos;

        this.pinHeights = new float[this.pinsCount];
        this.targetHeights = new float[this.pinsCount];
        this.isLocked = new boolean[this.pinsCount];

        Random random = new Random();
        for (int i = 0; i < this.pinsCount; i++) {
            this.pinHeights[i] = 0.0f;
            this.targetHeights[i] = 0.3f + random.nextFloat() * 0.5f;
            this.isLocked[i] = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (finished) return;

        if (isPushing && !isLocked[currentPin]) {
            pinHeights[currentPin] += 0.05f;

            if (pinHeights[currentPin] >= 1.0f) {
                pinHeights[currentPin] = 0.0f;
                isPushing = false;
                pickHealth--;
                checkGameOver();
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (finished) return super.keyPressed(keyCode, scanCode, modifiers);

        if (keyCode == InputConstants.KEY_A || keyCode == InputConstants.KEY_LEFT) {
            currentPin = Math.max(0, currentPin - 1);
            return true;
        } else if (keyCode == InputConstants.KEY_D || keyCode == InputConstants.KEY_RIGHT) {
            currentPin = Math.min(pinsCount - 1, currentPin + 1);
            return true;
        } else if (keyCode == InputConstants.KEY_SPACE) {
            if (!isPushing && !isLocked[currentPin]) {
                isPushing = true;
            }
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (finished) return super.keyReleased(keyCode, scanCode, modifiers);

        if (keyCode == InputConstants.KEY_SPACE) {
            if (isPushing) {
                isPushing = false;

                float target = targetHeights[currentPin];
                float current = pinHeights[currentPin];

                if (current >= target - 0.1f && current <= target + 0.1f) {
                    isLocked[currentPin] = true;
                    pinHeights[currentPin] = target;
                    checkVictory();
                } else {
                    pinHeights[currentPin] = 0.0f;
                    pickHealth--;
                    checkGameOver();
                }
            }
            return true;
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private void checkVictory() {
        boolean allLocked = true;
        for (boolean locked : isLocked) {
            if (!locked) {
                allLocked = false;
                break;
            }
        }

        if (allLocked) {
            finished = true;
            PacketHandler.sendToServer(new LockpickResultPacket(true, doorPos));
            this.onClose();
        }
    }

    private void checkGameOver() {
        if (pickHealth <= 0) {
            finished = true;
            PacketHandler.sendToServer(new LockpickResultPacket(false, doorPos));
            this.onClose();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int boxWidth = Math.max(200, pinsCount * 40 + 40);
        int boxHeight = 160;

        int boxX = centerX - boxWidth / 2;
        int boxY = centerY - boxHeight / 2;

        guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xE01A1A1A);
        guiGraphics.fill(boxX - 1, boxY - 1, boxX + boxWidth + 1, boxY, 0xFFD4AF37);
        guiGraphics.fill(boxX - 1, boxY + boxHeight, boxX + boxWidth + 1, boxY + boxHeight + 1, 0xFFD4AF37);
        guiGraphics.fill(boxX - 1, boxY, boxX, boxY + boxHeight, 0xFFD4AF37);
        guiGraphics.fill(boxX + boxWidth, boxY, boxX + boxWidth + 1, boxY + boxHeight, 0xFFD4AF37);

        guiGraphics.drawCenteredString(this.font, "Взлом замка...", centerX, boxY + 12, 0xFFD4AF37);
        String healthStr = "Отмычки: " + pickHealth;
        guiGraphics.drawString(this.font, healthStr, boxX + 15, boxY + 28, pickHealth > 1 ? 0x55FF55 : 0xFF5555);

        String controlsStr = "[A/D] Выбор  [SPACE] Подъем";
        guiGraphics.drawCenteredString(this.font, controlsStr, centerX, boxY + boxHeight - 16, 0xAAAAAA);

        int pinAreaY = boxY + 45;
        int pinAreaHeight = 70;
        int startPinX = centerX - (pinsCount * 40) / 2 + 10;

        for (int i = 0; i < pinsCount; i++) {
            int px = startPinX + i * 40;
            int py = pinAreaY;

            guiGraphics.fill(px, py, px + 20, py + pinAreaHeight, 0xFF333333);

            float target = targetHeights[i];
            int targetZoneY = py + (int) ((1.0f - target - 0.1f) * pinAreaHeight);
            int targetZoneHeight = (int) (0.2f * pinAreaHeight);
            guiGraphics.fill(px + 1, targetZoneY, px + 19, targetZoneY + targetZoneHeight, 0x8055FF55);

            float heightPct = pinHeights[i];
            int pinY = py + (int) ((1.0f - heightPct) * (pinAreaHeight - 15));
            int pinColor = isLocked[i] ? 0xFFFFD700 : (i == currentPin ? 0xFFAAAAAA : 0xFF777777);

            guiGraphics.fill(px + 3, pinY, px + 17, pinY + 15, pinColor);

            if (i == currentPin) {
                guiGraphics.drawCenteredString(this.font, "▲", px + 10, py + pinAreaHeight + 2, 0xFF55FFFF);
            }
        }

        int pickX = startPinX + currentPin * 40;
        int pickY = pinAreaY + pinAreaHeight + 14;
        guiGraphics.fill(boxX + 20, pickY, pickX + 10, pickY + 3, 0xFF55FFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
