package com.twalse.twmod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GalleryAppScreen extends Screen {
    private final Screen parent;
    private static final int PHONE_WIDTH = 120;
    private static final int PHONE_HEIGHT = 220;

    private final List<PhotoMeta> photoList = new ArrayList<>();

    public record PhotoMeta(String filename, String dateStr, String coordStr) {}

    public GalleryAppScreen(Screen parent) {
        super(Component.literal("Gallery"));
        this.parent = parent;
        scanPhotos();
    }

    private void scanPhotos() {
        photoList.clear();
        Minecraft mc = Minecraft.getInstance();
        File photosDir = new File(mc.gameDirectory, "twmod_photos");
        if (photosDir.exists() && photosDir.isDirectory()) {
            File[] files = photosDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
            if (files != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM HH:mm");
                for (File file : files) {
                    String dateStr = sdf.format(new Date(file.lastModified()));
                    String coordStr = mc.player != null ? String.format("%.0f, %.0f, %.0f", mc.player.getX(), mc.player.getY(), mc.player.getZ()) : "0, 0, 0";
                    photoList.add(new PhotoMeta(file.getName(), dateStr, coordStr));
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int phoneX = centerX - PHONE_WIDTH / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        // Home button
        int homeBtnX = centerX - 12;
        int homeBtnY = phoneY + PHONE_HEIGHT - 18;
        if (mouseX >= homeBtnX && mouseX <= homeBtnX + 24 && mouseY >= homeBtnY && mouseY <= homeBtnY + 10) {
            this.minecraft.setScreen(this.parent);
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
        guiGraphics.fill(phoneX - 5, phoneY - 8, phoneX + PHONE_WIDTH + 5, phoneY + PHONE_HEIGHT + 8, 0xFF1C1C1E);
        guiGraphics.fill(phoneX - 3, phoneY - 6, phoneX + PHONE_WIDTH + 3, phoneY + PHONE_HEIGHT + 6, 0xFF2C2C2E);
        guiGraphics.fill(phoneX, phoneY, phoneX + PHONE_WIDTH, phoneY + PHONE_HEIGHT, 0xFF0D0D11);

        guiGraphics.drawCenteredString(this.font, "🖼️ ГАЛЕРЕЯ", centerX, phoneY + 18, 0xFFD4AF37);

        int listY = phoneY + 36;
        if (photoList.isEmpty()) {
            guiGraphics.drawCenteredString(this.font, "Нет снимков", centerX, phoneY + 90, 0x777777);
        } else {
            for (PhotoMeta photo : photoList) {
                if (listY > phoneY + PHONE_HEIGHT - 30) break;

                guiGraphics.fill(phoneX + 6, listY, phoneX + PHONE_WIDTH - 6, listY + 28, 0xFF181822);
                guiGraphics.drawString(this.font, "📷 " + photo.dateStr(), phoneX + 10, listY + 4, 0xFFFFFF, false);

                // Small scale for coords line
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(phoneX + 10, listY + 16, 0);
                guiGraphics.pose().scale(0.75f, 0.75f, 1.0f);
                guiGraphics.drawString(this.font, "XYZ: " + photo.coordStr(), 0, 0, 0xAAAAAA, false);
                guiGraphics.pose().popPose();

                listY += 32;
            }
        }

        // Home Button
        int homeBtnX = centerX - 12;
        int homeBtnY = phoneY + PHONE_HEIGHT - 16;
        boolean homeHovered = mouseX >= homeBtnX && mouseX <= homeBtnX + 24 && mouseY >= homeBtnY && mouseY <= homeBtnY + 10;
        int homeColor = homeHovered ? 0xFFD4AF37 : 0xFF555555;

        guiGraphics.fill(homeBtnX, homeBtnY, homeBtnX + 24, homeBtnY + 8, homeColor);

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
