package com.twalse.twmod.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import com.twalse.twmod.util.TwLogger;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.File;

public class CameraAppScreen extends Screen {
    private final Screen parent;

    public CameraAppScreen(Screen parent) {
        super(Component.literal("Camera"));
        this.parent = parent;
    }

    private void takePhoto() {
        if (this.minecraft == null) return;

        try {
            File photosDir = new File(this.minecraft.gameDirectory, "twmod_photos");
            if (!photosDir.exists()) {
                photosDir.mkdirs();
            }

            NativeImage nativeImage = Screenshot.takeScreenshot(this.minecraft.getMainRenderTarget());
            File photoFile = new File(photosDir, System.currentTimeMillis() + ".png");
            nativeImage.writeToFile(photoFile);
            nativeImage.close();

            TwLogger.info("Camera screenshot saved: {}", photoFile.getAbsolutePath());
            if (this.minecraft.player != null) {
                this.minecraft.player.sendSystemMessage(Component.literal("§a[Фото]: Снимок сохранен в twmod_photos!"));
            }
        } catch (Exception e) {
            TwLogger.error("Failed to take camera screenshot", e);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int bottomY = this.height - 35;

        // Custom shutter button click
        if (mouseX >= centerX - 60 && mouseX <= centerX + 60 && mouseY >= bottomY && mouseY <= bottomY + 24) {
            takePhoto();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Viewfinder reticle overlay
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

        // Custom Shutter Button
        int bottomY = this.height - 35;
        boolean btnHovered = mouseX >= centerX - 60 && mouseX <= centerX + 60 && mouseY >= bottomY && mouseY <= bottomY + 24;
        int btnColor = btnHovered ? 0xFFE0E0E0 : 0xFFFFFFFF;

        guiGraphics.fill(centerX - 60, bottomY, centerX + 60, bottomY + 24, btnColor);
        guiGraphics.drawCenteredString(this.font, "📸 Сделать фото", centerX, bottomY + 8, 0xFF000000);

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
