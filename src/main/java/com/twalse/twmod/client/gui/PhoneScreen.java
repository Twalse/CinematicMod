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
    public static final ResourceLocation BG_WALLPAPER = new ResourceLocation("twmod", "textures/gui/phone_bg_wallpaper.png");

    public static final int PHONE_WIDTH = 120;
    public static final int PHONE_HEIGHT = 220;

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
            int gridStartX = phoneX + 12;
            int gridStartY = phoneY + 34;
            int iconSize = 24;
            int gapX = 12;
            int gapY = 18;

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
            guiGraphics.blit(BG_BLACK, phoneX, phoneY, PHONE_WIDTH, PHONE_HEIGHT, 0.0F, 0.0F, 104, 214, 104, 214);
            super.render(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }

        if (state == PhoneState.BOOTING) {
            long elapsed = Util.getMillis() - bootStartTime;
            long bootDuration = 2000L;

            if (elapsed >= bootDuration) {
                state = PhoneState.ACTIVE;
            } else {
                guiGraphics.blit(BG_DARK, phoneX, phoneY, PHONE_WIDTH, PHONE_HEIGHT, 0.0F, 0.0F, 104, 214, 104, 214);

                double progress = (double) elapsed / bootDuration;
                float alpha = (float) Math.sin(progress * Math.PI);
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

        // State is ACTIVE: Render 140x250 phone_bg_wallpaper.png scaled into PHONE_WIDTH x PHONE_HEIGHT
        guiGraphics.blit(BG_WALLPAPER, phoneX, phoneY, PHONE_WIDTH, PHONE_HEIGHT, 0.0F, 0.0F, 140, 250, 140, 250);

        // Status Bar: Safely below camera notch
        Minecraft mc = Minecraft.getInstance();
        String timeStr = "12:00";
        if (mc.level != null) {
            long dayTime = (mc.level.getDayTime() + 6000) % 24000;
            long hours = dayTime / 1000;
            long minutes = (dayTime % 1000) * 60 / 1000;
            timeStr = String.format("%02d:%02d", hours, minutes);
        }

        String signalStr = SettingsAppScreen.airplaneMode ? "✈️" : "100%";

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(phoneX + 12, phoneY + 14, 0);
        guiGraphics.pose().scale(0.55f, 0.55f, 1.0f);
        guiGraphics.drawString(this.font, timeStr, 0, 0, 0xFFFFFFFF, true);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(phoneX + PHONE_WIDTH - 30, phoneY + 14, 0);
        guiGraphics.pose().scale(0.55f, 0.55f, 1.0f);
        guiGraphics.drawString(this.font, signalStr, 0, 0, 0xFFFFFFFF, true);
        guiGraphics.pose().popPose();

        // Desktop App Icons Grid (32x32 textures scaled cleanly to 24x24 using 11-param blit)
        List<AppEntry> installed = getInstalledAppEntries();
        int gridStartX = phoneX + 12;
        int gridStartY = phoneY + 34;
        int iconSize = 24;
        int gapX = 12;
        int gapY = 18;

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
                // Full 32x32 texture scaled into 24x24 screen box
                guiGraphics.blit(app.icon(), ix, iy, iconSize, iconSize, 0.0F, 0.0F, 32, 32, 32, 32);
            } catch (Exception ignored) {
            }

            // Scaled App Label - Centered dynamically under icon using font.width
            float labelScale = 0.55f;
            int textW = this.font.width(app.name());
            float textCenterX = ix + (iconSize - textW * labelScale) / 2.0f;
            float labelY = iy + iconSize + 2;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(textCenterX, labelY, 0);
            guiGraphics.pose().scale(labelScale, labelScale, 1.0f);
            guiGraphics.drawString(this.font, app.name(), 0, 0, 0xFFFFFFFF, true);
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
