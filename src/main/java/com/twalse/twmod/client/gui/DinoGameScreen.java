package com.twalse.twmod.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class DinoGameScreen extends Screen {
    private final Screen parent;
    public static final int PHONE_WIDTH = PhoneScreen.PHONE_WIDTH;
    public static final int PHONE_HEIGHT = PhoneScreen.PHONE_HEIGHT;

    // Physics & Game state
    private float dinoY = 0.0f; // Height above ground (0 = on ground)
    private float velocityY = 0.0f;
    private boolean isJumping = false;
    private int score = 0;
    private boolean gameOver = false;

    private final List<Float> cactiX = new ArrayList<>();
    private int spawnTimer = 0;
    private final Random random = new Random();

    public DinoGameScreen(Screen parent) {
        super(Component.literal("Dino Game"));
        this.parent = parent;
        resetGame();
    }

    private void resetGame() {
        this.dinoY = 0.0f;
        this.velocityY = 0.0f;
        this.isJumping = false;
        this.score = 0;
        this.gameOver = false;
        this.cactiX.clear();
        this.spawnTimer = 0;
    }

    private void jump() {
        if (!isJumping && !gameOver) {
            isJumping = true;
            velocityY = 6.0f;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (gameOver) return;

        score++;

        // Physics
        if (isJumping) {
            dinoY += velocityY;
            velocityY -= 0.5f;

            if (dinoY <= 0.0f) {
                dinoY = 0.0f;
                velocityY = 0.0f;
                isJumping = false;
            }
        }

        // Spawn cacti
        spawnTimer++;
        if (spawnTimer >= 35 + random.nextInt(25)) {
            cactiX.add(90.0f);
            spawnTimer = 0;
        }

        // Move cacti and check collision
        float dinoX = 15.0f;
        float dinoWidth = 12.0f;

        Iterator<Float> iterator = cactiX.iterator();
        while (iterator.hasNext()) {
            float cx = iterator.next() - 2.5f;
            if (cx < -15.0f) {
                iterator.remove();
            } else {
                int idx = cactiX.indexOf(cx + 2.5f);
                if (idx >= 0) {
                    cactiX.set(idx, cx);
                }

                float cactusWidth = 8.0f;
                float cactusHeight = 16.0f;

                boolean xOverlap = dinoX < cx + cactusWidth && dinoX + dinoWidth > cx;
                boolean yOverlap = dinoY < cactusHeight;

                if (xOverlap && yOverlap) {
                    gameOver = true;
                    break;
                }
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_SPACE) {
            jump();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
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

        // Restart button click if Game Over
        if (gameOver) {
            if (mouseX >= centerX - 30 && mouseX <= centerX + 30 && mouseY >= phoneY + 120 && mouseY <= phoneY + 138) {
                resetGame();
                return true;
            }
        } else if (mouseX >= phoneX && mouseX <= phoneX + PHONE_WIDTH && mouseY >= phoneY && mouseY <= phoneY + PHONE_HEIGHT) {
            jump();
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

        // Background: phone_bg_dark.png
        guiGraphics.blit(PhoneScreen.BG_DARK, phoneX, phoneY, 0.0F, 0.0F, PHONE_WIDTH, PHONE_HEIGHT, PHONE_WIDTH, PHONE_HEIGHT);

        // Header
        guiGraphics.drawCenteredString(this.font, "🦖 DINO RUN", centerX, phoneY + 18, 0xFFD4AF37);
        guiGraphics.drawCenteredString(this.font, "Score: " + score, centerX, phoneY + 30, 0xFFFFFFFF);

        // Ground
        int groundY = phoneY + 150;
        guiGraphics.fill(phoneX + 5, groundY, phoneX + PHONE_WIDTH - 5, groundY + 1, 0xFF888888);

        // Dino
        int dinoRenderX = phoneX + 15;
        int dinoRenderY = (int) (groundY - 14 - dinoY);
        guiGraphics.fill(dinoRenderX, dinoRenderY, dinoRenderX + 12, dinoRenderY + 14, 0xFFDC3545);

        // Cacti
        for (float cx : cactiX) {
            int cactusRenderX = phoneX + (int) cx;
            if (cactusRenderX >= phoneX + 4 && cactusRenderX <= phoneX + PHONE_WIDTH - 12) {
                guiGraphics.fill(cactusRenderX, groundY - 16, cactusRenderX + 8, groundY, 0xFF28A745);
            }
        }

        // Game Over
        if (gameOver) {
            guiGraphics.drawCenteredString(this.font, "GAME OVER", centerX, phoneY + 90, 0xFFFF5555);

            boolean btnHovered = mouseX >= centerX - 30 && mouseX <= centerX + 30 && mouseY >= phoneY + 120 && mouseY <= phoneY + 138;
            guiGraphics.fill(centerX - 30, phoneY + 120, centerX + 30, phoneY + 138, btnHovered ? 0xFF00A0E6 : 0xFF0088CC);
            guiGraphics.drawCenteredString(this.font, "🔄 Снова", centerX, phoneY + 125, 0xFFFFFFFF);
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
