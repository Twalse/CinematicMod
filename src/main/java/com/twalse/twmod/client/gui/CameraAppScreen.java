package com.twalse.twmod.client.gui;

import com.twalse.twmod.util.TwLogger;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.File;

public class CameraAppScreen extends Screen {
    private final Screen parent;

    public CameraAppScreen(Screen parent) {
        super(Component.literal("Camera"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int bottomY = this.height - 35;

        // Take Snapshot Button
        this.addRenderableWidget(Button.builder(Component.literal("📸 Сделать фото"), btn -> takePhoto())
                .bounds(centerX - 60, bottomY, 120, 24).build());
    }

    private void takePhoto() {
        if (this.minecraft == null) return;

        try {
            File photosDir = new File(this.minecraft.gameDirectory, "twmod_photos");
            if (!photosDir.exists()) {
                photosDir.mkdirs();
            }

            // Hide GUI screen briefly and capture screenshot into twmod_photos
            this.minecraft.setScreen(null);

            Screenshot.grab(
                    this.minecraft.gameDirectory,
                    "twmod_photos/" + System.currentTimeMillis() + ".png",
                    this.minecraft.getMainRenderTarget(),
                    msg -> {
                        TwLogger.info("Camera screenshot saved: {}", msg.getString());
                        if (this.minecraft.player != null) {
                            this.minecraft.player.sendSystemMessage(Component.literal("§a[Фото]: Снимки сохранены в twmod_photos!"));
                        }
                    }
            );
        } catch (Exception e) {
            TwLogger.error("Failed to take camera screenshot", e);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Viewfinder reticle overlay (no phone bezel covering screen)
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int size = 20;

        guiGraphics.fill(centerX - size, centerY - 1, centerX + size, centerY + 1, 0x80FFFFFF);
        guiGraphics.fill(centerX - 1, centerY - size, centerX + 1, centerY + size, 0x80FFFFFF);

        // Frame corners
        int margin = 30;
        int w = this.width - margin;
        int h = this.height - margin;

        guiGraphics.fill(margin, margin, margin + 15, margin + 2, 0xFFFFFFFF);
        guiGraphics.fill(margin, margin, margin + 2, margin + 15, 0xFFFFFFFF);

        guiGraphics.fill(w - 15, margin, w, margin + 2, 0xFFFFFFFF);
        guiGraphics.fill(w - 2, margin, w, margin + 15, 0xFFFFFFFF);

        guiGraphics.fill(margin, h - 2, margin + 15, h, 0xFFFFFFFF);
        guiGraphics.fill(margin, h - 15, margin + 2, h, 0xFFFFFFFF);

        guiGraphics.fill(w - 15, h - 2, w, h, 0xFFFFFFFF);
        guiGraphics.fill(w - 2, h - 15, w, h, 0xFFFFFFFF);

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
