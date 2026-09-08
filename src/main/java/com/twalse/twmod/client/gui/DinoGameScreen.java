package com.twalse.twmod.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class DinoGameScreen extends Screen {
    private final Screen parent;
    private static final int PHONE_WIDTH = 150;
    private static final int PHONE_HEIGHT = 260;

    // Physics & Game state
    private float dinoY = 0.0f; // Height above ground (0 = on ground)
    private float velocityY = 0.0f;
    private boolean isJumping = false;
    private int score = 0;
    private boolean gameOver = false;

    private final List<Float> cactiX = new ArrayList<>();
    private int spawnTimer = 0;
    private final Random random = new Random();

    private Button restartButton;

    public DinoGameScreen(Screen parent) {
        super(Component.literal("Dino Game"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        this.restartButton = Button.builder(Component.literal("🔄 Рестарт"), btn -> resetGame())
                .bounds(centerX - 40, phoneY + 140, 80, 20).build();
        this.restartButton.visible = false;
        this.addRenderableWidget(this.restartButton);

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
        if (this.restartButton != null) {
            this.restartButton.visible = false;
        }
    }

    private void jump() {
        if (!isJumping && !gameOver) {
            isJumping = true;
            velocityY = 6.0f; // Initial upward velocity
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (gameOver) return;

        score++;

        // Apply jump physics
        if (isJumping) {
            dinoY += velocityY;
            velocityY -= 0.5f; // Gravity

            if (dinoY <= 0.0f) {
                dinoY = 0.0f;
                velocityY = 0.0f;
                isJumping = false;
            }
        }

        // Spawn cacti
        spawnTimer++;
        if (spawnTimer >= 40 + random.nextInt(30)) {
            cactiX.add(130.0f); // Spawn at right side of phone screen
            spawnTimer = 0;
        }

        // Move cacti and collision check
        float dinoX = 20.0f; // Dino fixed X offset
        float dinoWidth = 14.0f;
        float dinoHeight = 16.0f;

        Iterator<Float> iterator = cactiX.iterator();
        while (iterator.hasNext()) {
            float cx = iterator.next() - 3.0f; // Speed
            if (cx < -20.0f) {
                iterator.remove();
            } else {
                // Update position in list
                int idx = cactiX.indexOf(cx + 3.0f);
                if (idx >= 0) {
                    cactiX.set(idx, cx);
                }

                // Collision Box check
                float cactusWidth = 10.0f;
                float cactusHeight = 18.0f;

                boolean xOverlap = dinoX < cx + cactusWidth && dinoX + dinoWidth > cx;
                boolean yOverlap = dinoY < cactusHeight; // Dino ground collision

                if (xOverlap && yOverlap) {
                    gameOver = true;
                    if (this.restartButton != null) {
                        this.restartButton.visible = true;
                    }
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

        // Check Home Button click
        int homeBtnX = centerX - 15;
        int homeBtnY = phoneY + PHONE_HEIGHT - 22;
        if (mouseX >= homeBtnX && mouseX <= homeBtnX + 30 && mouseY >= homeBtnY && mouseY <= homeBtnY + 12) {
            this.minecraft.setScreen(this.parent);
            return true;
        }

        // Click on phone screen area triggers jump
        if (!gameOver && mouseX >= phoneX && mouseX <= phoneX + PHONE_WIDTH && mouseY >= phoneY && mouseY <= phoneY + PHONE_HEIGHT) {
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

        // Phone Frame
        guiGraphics.fill(phoneX - 6, phoneY - 10, phoneX + PHONE_WIDTH + 6, phoneY + PHONE_HEIGHT + 10, 0xFF1C1C1E);
        guiGraphics.fill(phoneX - 4, phoneY - 8, phoneX + PHONE_WIDTH + 4, phoneY + PHONE_HEIGHT + 8, 0xFF2C2C2E);
        guiGraphics.fill(phoneX, phoneY, phoneX + PHONE_WIDTH, phoneY + PHONE_HEIGHT, 0xFF0D0D11);

        // Header Title & Score
        guiGraphics.drawCenteredString(this.font, "🦖 DINO RUN", centerX, phoneY + 18, 0xFFD4AF37);
        guiGraphics.drawCenteredString(this.font, "Score: " + score, centerX, phoneY + 32, 0xFFFFFFFF);

        // Ground Line
        int groundY = phoneY + 180;
        guiGraphics.fill(phoneX + 5, groundY, phoneX + PHONE_WIDTH - 5, groundY + 2, 0xFF888888);

        // Render Dino (Red Box or Icon)
        int dinoRenderX = phoneX + 20;
        int dinoRenderY = (int) (groundY - 16 - dinoY);
        guiGraphics.fill(dinoRenderX, dinoRenderY, dinoRenderX + 14, dinoRenderY + 16, 0xFFDC3545);
        guiGraphics.fill(dinoRenderX + 10, dinoRenderY + 2, dinoRenderX + 13, dinoRenderY + 5, 0xFFFFFFFF); // Eye

        // Render Cacti (Green Boxes)
        for (float cx : cactiX) {
            int cactusRenderX = phoneX + (int) cx;
            if (cactusRenderX >= phoneX + 5 && cactusRenderX <= phoneX + PHONE_WIDTH - 15) {
                guiGraphics.fill(cactusRenderX, groundY - 18, cactusRenderX + 10, groundY, 0xFF28A745);
            }
        }

        // Game Over Overlay
        if (gameOver) {
            guiGraphics.drawCenteredString(this.font, "GAME OVER", centerX, phoneY + 100, 0xFFFF5555);
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
