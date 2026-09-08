package com.twalse.twmod.client.gui;

import com.twalse.twmod.TwMod;
import com.twalse.twmod.quest.ClientQuestData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PhoneScreen extends Screen {

    public static final ResourceLocation PHONE_BG = new ResourceLocation(TwMod.MODID, "textures/gui/phone_bg.png");

    private static final int PHONE_WIDTH = 150;
    private static final int PHONE_HEIGHT = 260;

    public record AppEntry(String id, String name, ResourceLocation icon, java.util.function.Consumer<PhoneScreen> action) {}

    private final List<AppEntry> availableApps = new ArrayList<>();

    public PhoneScreen() {
        super(Component.literal("Smartphone"));

        // Register available apps in TwOS
        availableApps.add(new AppEntry("contacts", "Контакты", new ResourceLocation(TwMod.MODID, "textures/gui/app_contacts.png"), p -> p.minecraft.setScreen(new ContactsAppScreen(p))));
        availableApps.add(new AppEntry("market", "Маркет", new ResourceLocation(TwMod.MODID, "textures/gui/app_market.png"), p -> p.minecraft.setScreen(new MarketAppScreen(p))));
        availableApps.add(new AppEntry("twstore", "TwStore", new ResourceLocation(TwMod.MODID, "textures/gui/app_twstore.png"), p -> p.minecraft.setScreen(new TwStoreScreen(p))));

        availableApps.add(new AppEntry("dino", "Dino", new ResourceLocation(TwMod.MODID, "textures/gui/app_dino.png"), p -> p.minecraft.setScreen(new DinoGameScreen(p))));
        availableApps.add(new AppEntry("twgramm", "TwGramm", new ResourceLocation(TwMod.MODID, "textures/gui/app_twgramm.png"), p -> p.minecraft.setScreen(new TwGrammScreen(p))));
        availableApps.add(new AppEntry("camera", "Камера", new ResourceLocation(TwMod.MODID, "textures/gui/app_camera.png"), p -> p.minecraft.setScreen(new CameraAppScreen(p))));
        availableApps.add(new AppEntry("gallery", "Галерея", new ResourceLocation(TwMod.MODID, "textures/gui/app_gallery.png"), p -> p.minecraft.setScreen(new GalleryAppScreen(p))));
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
            return true;
        }

        // Check App Icon Clicks in Grid Layout (3 Columns x 4 Rows)
        List<AppEntry> installed = getInstalledAppEntries();
        int gridStartX = phoneX + 16;
        int gridStartY = phoneY + 35;
        int iconSize = 32;
        int gapX = 14;
        int gapY = 20;

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

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private List<AppEntry> getInstalledAppEntries() {
        List<AppEntry> installed = new ArrayList<>();
        for (AppEntry app : availableApps) {
            if (ClientQuestData.isAppInstalled(app.id())) {
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

        // 1. Render Phone Frame & Wallpaper Texture Background
        try {
            guiGraphics.blit(PHONE_BG, phoneX - 5, phoneY - 5, 0, 0, PHONE_WIDTH + 10, PHONE_HEIGHT + 10, PHONE_WIDTH + 10, PHONE_HEIGHT + 10);
        } catch (Exception e) {
            guiGraphics.fill(phoneX, phoneY, phoneX + PHONE_WIDTH, phoneY + PHONE_HEIGHT, 0xFF0B1021);
        }

        // 2. Camera Notch
        guiGraphics.fill(centerX - 18, phoneY - 3, centerX + 18, phoneY - 1, 0xFF000000);

        // 3. Status Bar
        guiGraphics.drawString(this.font, "12:00", phoneX + 8, phoneY + 6, 0xDDDDDD, false);
        guiGraphics.drawString(this.font, "5G ⚡", phoneX + PHONE_WIDTH - 30, phoneY + 6, 0xDDDDDD, false);

        // 4. Desktop App Icons Grid (Transparent PNG icons without color boxes)
        List<AppEntry> installed = getInstalledAppEntries();
        int gridStartX = phoneX + 16;
        int gridStartY = phoneY + 35;
        int iconSize = 32;
        int gapX = 14;
        int gapY = 20;

        for (int i = 0; i < installed.size(); i++) {
            AppEntry app = installed.get(i);
            int col = i % 3;
            int row = i / 3;

            int ix = gridStartX + col * (iconSize + gapX);
            int iy = gridStartY + row * (iconSize + gapY);

            boolean hovered = mouseX >= ix && mouseX <= ix + iconSize && mouseY >= iy && mouseY <= iy + iconSize;

            // Render App Icon Texture directly
            try {
                if (hovered) {
                    guiGraphics.fill(ix - 2, iy - 2, ix + iconSize + 2, iy + iconSize + 2, 0x40FFFFFF);
                }
                guiGraphics.blit(app.icon(), ix, iy, 0, 0, iconSize, iconSize, iconSize, iconSize);
            } catch (Exception ignored) {
            }

            // Scaled App Label (0.75f) with shadow, centered under icon
            guiGraphics.pose().pushPose();
            float labelScale = 0.75f;
            float iconCenterX = ix + iconSize / 2.0f;
            float labelY = iy + iconSize + 3;

            guiGraphics.pose().translate(iconCenterX, labelY, 0);
            guiGraphics.pose().scale(labelScale, labelScale, 1.0f);

            guiGraphics.drawCenteredString(this.font, app.name(), 0, 0, 0xFFFFFFFF);
            guiGraphics.pose().popPose();
        }

        // 5. Bottom Home Button
        int homeBtnX = centerX - 15;
        int homeBtnY = phoneY + PHONE_HEIGHT - 18;
        boolean homeHovered = mouseX >= homeBtnX && mouseX <= homeBtnX + 30 && mouseY >= homeBtnY && mouseY <= homeBtnY + 12;
        int homeColor = homeHovered ? 0xFFD4AF37 : 0xFF555555;

        guiGraphics.fill(homeBtnX, homeBtnY, homeBtnX + 30, homeBtnY + 10, homeColor);
        guiGraphics.drawCenteredString(this.font, "—", centerX, homeBtnY + 1, 0xFFFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
