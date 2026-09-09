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
    public static final int PHONE_WIDTH = PhoneScreen.PHONE_WIDTH;
    public static final int PHONE_HEIGHT = PhoneScreen.PHONE_HEIGHT;

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

        // Bottom Home Indicator click
        if (mouseY >= phoneY + PHONE_HEIGHT - 18 && mouseY <= phoneY + PHONE_HEIGHT - 2 &&
            mouseX >= phoneX && mouseX <= phoneX + PHONE_WIDTH) {
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

        // Background: phone_bg_dark.png
        guiGraphics.blit(PhoneScreen.BG_DARK, phoneX, phoneY, 0.0F, 0.0F, PHONE_WIDTH, PHONE_HEIGHT, PHONE_WIDTH, PHONE_HEIGHT);

        guiGraphics.drawCenteredString(this.font, "🖼️ Галерея", centerX, phoneY + 18, 0xFFFFFFFF);

        int listY = phoneY + 36;
        if (photoList.isEmpty()) {
            guiGraphics.drawCenteredString(this.font, "Нет снимков", centerX, phoneY + 90, 0x888888);
        } else {
            for (PhotoMeta photo : photoList) {
                if (listY > phoneY + PHONE_HEIGHT - 30) break;

                guiGraphics.fill(phoneX + 6, listY, phoneX + PHONE_WIDTH - 6, listY + 28, 0xFF2C2C2E);
                guiGraphics.drawString(this.font, "📷 " + photo.dateStr(), phoneX + 10, listY + 4, 0xFFFFFF, false);

                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(phoneX + 10, listY + 16, 0);
                guiGraphics.pose().scale(0.75f, 0.75f, 1.0f);
                guiGraphics.drawString(this.font, "XYZ: " + photo.coordStr(), 0, 0, 0xAAAAAA, false);
                guiGraphics.pose().popPose();

                listY += 32;
            }
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
