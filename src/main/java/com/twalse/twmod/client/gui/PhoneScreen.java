package com.twalse.twmod.client.gui;

import com.twalse.twmod.quest.ClientQuestData;
import com.twalse.twmod.util.TwLogger;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PhoneScreen extends Screen {

    public static final ResourceLocation BG_BLACK = new ResourceLocation("twmod", "textures/gui/phone_bg_black.png");
    public static final ResourceLocation BG_DARK = new ResourceLocation("twmod", "textures/gui/phone_bg_dark.png");

    public static final int PHONE_WIDTH = 104;
    public static final int PHONE_HEIGHT = 214;

    public enum PhoneState {
        OFF,
        BOOTING,
        ACTIVE
    }

    public record AppEntry(String id, String name, ResourceLocation icon, java.util.function.Consumer<PhoneScreen> action) {}

    private final List<AppEntry> availableApps = new ArrayList<>();
    private final Screen parentScreen;

    private PhoneState state = PhoneState.OFF;
    private long bootStartTime = 0L;

    public PhoneScreen() {
        this(null);
    }

    public PhoneScreen(Screen parentScreen) {
        super(Component.literal("Smartphone"));
        this.parentScreen = parentScreen;

        // Register available apps in TwOS
        availableApps.add(new AppEntry("contacts", "Контакты", new ResourceLocation("twmod", "textures/gui/icon_contacts.png"), p -> p.minecraft.setScreen(new ContactsAppScreen(p))));
        availableApps.add(new AppEntry("market", "Маркет", new ResourceLocation("twmod", "textures/gui/icon_market.png"), p -> p.minecraft.setScreen(new MarketAppScreen(p))));
        availableApps.add(new AppEntry("twstore", "TwStore", new ResourceLocation("twmod", "textures/gui/icon_store.png"), p -> p.minecraft.setScreen(new TwStoreScreen(p))));

        availableApps.add(new AppEntry("dino", "Dino", new ResourceLocation("twmod", "textures/gui/icon_dino.png"), p -> p.minecraft.setScreen(new DinoGameScreen(p))));
        availableApps.add(new AppEntry("twgramm", "TwGramm", new ResourceLocation("twmod", "textures/gui/icon_twgramm.png"), p -> p.minecraft.setScreen(new TwGrammScreen(p))));
        availableApps.add(new AppEntry("camera", "Камера", new ResourceLocation("twmod", "textures/gui/icon_camera.png"), p -> p.minecraft.setScreen(new CameraAppScreen(p))));
        availableApps.add(new AppEntry("gallery", "Галерея", new ResourceLocation("twmod", "textures/gui/icon_gallery.png"), p -> p.minecraft.setScreen(new GalleryAppScreen(p))));
        availableApps.add(new AppEntry("settings", "Настройки", new ResourceLocation("twmod", "textures/gui/icon_settings.png"), p -> p.minecraft.setScreen(new SettingsAppScreen(p))));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int phoneX = centerX - PHONE_WIDTH / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        boolean insidePhone = mouseX >= phoneX && mouseX <= phoneX + PHONE_WIDTH && mouseY >= phoneY && mouseY <= phoneY + PHONE_HEIGHT;

        if (state == PhoneState.OFF) {
            if (insidePhone && button == 0) {
                state = PhoneState.BOOTING;
                bootStartTime = Util.getMillis();
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (state == PhoneState.BOOTING) {
            return true;
        }

        // State is ACTIVE
        if (insidePhone) {
            // Check iPhone 17 Navigation bar click (bottom 18 pixels)
            int navYStart = phoneY + PHONE_HEIGHT - 18;
            if (mouseY >= navYStart && mouseY <= phoneY + PHONE_HEIGHT - 2) {
                double relX = mouseX - phoneX;
                double zoneWidth = PHONE_WIDTH / 3.0;

                if (relX < zoneWidth) {
                    // Left third: Back
                    if (parentScreen != null) {
                        this.minecraft.setScreen(parentScreen);
                    } else {
                        this.onClose();
                    }
                } else if (relX < zoneWidth * 2) {
                    // Center third: Home
                } else {
                    // Right third: Recents
                    TwLogger.info("Открытие недавних");
                }
                return true;
            }

            // Check App Icon Clicks in Grid Layout (3 Columns x 4 Rows)
            List<AppEntry> installed = getInstalledAppEntries();
            int gridStartX = phoneX + 10;
            int gridStartY = phoneY + 26;
            int iconSize = 24;
            int gapX = 5;
            int gapY = 16;

            for (int i = 0; i < installed.size(); i++) {
                int col = i % 3;
                int row = i / 3;

                int ix = gridStartX + col * (iconSize + gapX);
                int iy = gridStartY + row * (iconSize + gapY);

                if (mouseX >= ix && mouseX <= ix + iconSize && mouseY >= iy && mouseY <= iy + iconSize) {
                    installed.get(i).action().accept(this);
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private List<AppEntry> getInstalledAppEntries() {
        List<AppEntry> installed = new ArrayList<>();
        for (AppEntry app : availableApps) {
            if ("settings".equals(app.id()) || ClientQuestData.isAppInstalled(app.id())) {
                installed.add(app);
            }
        }
        return installed;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int phoneX = centerX - PHONE_WIDTH / 2;
        int phoneY = centerY - PHONE_HEIGHT / 2;

        if (state == PhoneState.OFF) {
            // Render phone_bg_black.png
            guiGraphics.blit(BG_BLACK, phoneX, phoneY, 0.0F, 0.0F, PHONE_WIDTH, PHONE_HEIGHT, PHONE_WIDTH, PHONE_HEIGHT);
            super.render(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }

        if (state == PhoneState.BOOTING) {
            long elapsed = Util.getMillis() - bootStartTime;
            long bootDuration = 2000L; // 2.0 seconds boot animation

            if (elapsed >= bootDuration) {
                state = PhoneState.ACTIVE;
            } else {
                // Render phone_bg_dark.png
                guiGraphics.blit(BG_DARK, phoneX, phoneY, 0.0F, 0.0F, PHONE_WIDTH, PHONE_HEIGHT, PHONE_WIDTH, PHONE_HEIGHT);

                // Smooth fade-in and fade-out alpha for "TwOS" text
                double progress = (double) elapsed / bootDuration; // 0.0 to 1.0
                float alpha = (float) Math.sin(progress * Math.PI); // Smooth curve 0 -> 1 -> 0
                int alphaInt = Math.min(255, Math.max(0, (int) (alpha * 255)));
                int textColor = (alphaInt << 24) | 0x00FFFFFF;

                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(centerX, centerY - 6, 0);
                guiGraphics.pose().scale(1.5f, 1.5f, 1.0f);
                guiGraphics.drawCenteredString(this.font, "TwOS", 0, 0, textColor);
                guiGraphics.pose().popPose();

                super.render(guiGraphics, mouseX, mouseY, partialTick);
                return;
            }
        }

        // State is ACTIVE: Render phone_bg_dark.png background
        guiGraphics.blit(BG_DARK, phoneX, phoneY, 0.0F, 0.0F, PHONE_WIDTH, PHONE_HEIGHT, PHONE_WIDTH, PHONE_HEIGHT);

        // Status Bar: Game Time & Signal/Airplane
        Minecraft mc = Minecraft.getInstance();
        String timeStr = "12:00";
        if (mc.level != null) {
            long dayTime = (mc.level.getDayTime() + 6000) % 24000;
            long hours = dayTime / 1000;
            long minutes = (dayTime % 1000) * 60 / 1000;
            timeStr = String.format("%02d:%02d", hours, minutes);
        }

        String signalStr = SettingsAppScreen.airplaneMode ? "✈️" : "ıll 100%";

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(phoneX + 10, phoneY + 12, 0);
        guiGraphics.pose().scale(0.55f, 0.55f, 1.0f);
        guiGraphics.drawString(this.font, timeStr, 0, 0, 0xEEEEEE, true);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(phoneX + PHONE_WIDTH - 34, phoneY + 12, 0);
        guiGraphics.pose().scale(0.55f, 0.55f, 1.0f);
        guiGraphics.drawString(this.font, signalStr, 0, 0, 0xEEEEEE, true);
        guiGraphics.pose().popPose();

        // Desktop App Icons Grid
        List<AppEntry> installed = getInstalledAppEntries();
        int gridStartX = phoneX + 10;
        int gridStartY = phoneY + 28;
        int iconSize = 24;
        int gapX = 5;
        int gapY = 16;

        for (int i = 0; i < installed.size(); i++) {
            AppEntry app = installed.get(i);
            int col = i % 3;
            int row = i / 3;

            int ix = gridStartX + col * (iconSize + gapX);
            int iy = gridStartY + row * (iconSize + gapY);

            boolean hovered = mouseX >= ix && mouseX <= ix + iconSize && mouseY >= iy && mouseY <= iy + iconSize;

            try {
                if (hovered) {
                    guiGraphics.fill(ix - 1, iy - 1, ix + iconSize + 1, iy + iconSize + 1, 0x40FFFFFF);
                }
                guiGraphics.blit(app.icon(), ix, iy, 0.0F, 0.0F, iconSize, iconSize, 32, 32);
            } catch (Exception ignored) {
            }

            // Scaled App Label
            guiGraphics.pose().pushPose();
            float labelScale = 0.55f;
            float iconCenterX = ix + iconSize / 2.0f;
            float labelY = iy + iconSize + 2;

            guiGraphics.pose().translate(iconCenterX, labelY, 0);
            guiGraphics.pose().scale(labelScale, labelScale, 1.0f);

            guiGraphics.drawCenteredString(this.font, app.name(), 0, 0, 0xFFFFFFFF);
            guiGraphics.pose().popPose();
        }

        // iPhone 17 Home Indicator Bar
        int navBarX = centerX - 16;
        int navBarY = phoneY + PHONE_HEIGHT - 10;
        guiGraphics.fill(navBarX, navBarY, navBarX + 32, navBarY + 3, 0xDDFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
