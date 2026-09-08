package com.twalse.twmod.client.gui;

import com.twalse.twmod.quest.ClientQuestData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class PhoneScreen extends Screen {

    private static final int PHONE_WIDTH = 140;
    private static final int PHONE_HEIGHT = 250;

    public record AppEntry(String id, String name, String iconSymbol, int color, java.util.function.Consumer<PhoneScreen> action) {}

    private final List<AppEntry> availableApps = new ArrayList<>();

    public PhoneScreen() {
        super(Component.literal("Smartphone"));

        // Register available apps in TwOS
        availableApps.add(new AppEntry("contacts", "Контакты", "📞", 0xFF28A745, p -> p.minecraft.setScreen(new ContactsAppScreen(p))));
        availableApps.add(new AppEntry("market", "Маркет", "💳", 0xFF6F42C1, p -> p.minecraft.setScreen(new MarketAppScreen(p))));
        availableApps.add(new AppEntry("twstore", "TwStore", "🛍️", 0xFF007BFF, p -> p.minecraft.setScreen(new TwStoreScreen(p))));

        availableApps.add(new AppEntry("dino", "Dino", "🦖", 0xFFDC3545, p -> p.minecraft.setScreen(new DinoGameScreen(p))));
        availableApps.add(new AppEntry("twgramm", "TwGramm", "✈️", 0xFF17A2B8, p -> p.minecraft.setScreen(new TwGrammScreen(p))));
        availableApps.add(new AppEntry("camera", "Камера", "📷", 0xFFFFC107, p -> p.minecraft.setScreen(new CameraAppScreen(p))));
        availableApps.add(new AppEntry("gallery", "Галерея", "🖼️", 0xFFFD7E14, p -> p.minecraft.setScreen(new GalleryAppScreen(p))));
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
        int gridStartX = phoneX + 12;
        int gridStartY = phoneY + 35;
        int iconSize = 32;
        int gapX = 8;
        int gapY = 12;

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

        // 1. Phone Outer Frame (Bezel)
        guiGraphics.fill(phoneX - 6, phoneY - 10, phoneX + PHONE_WIDTH + 6, phoneY + PHONE_HEIGHT + 10, 0xFF1C1C1E);
        guiGraphics.fill(phoneX - 4, phoneY - 8, phoneX + PHONE_WIDTH + 4, phoneY + PHONE_HEIGHT + 8, 0xFF2C2C2E);

        // 2. Phone Screen Wallpaper Display (Dark Blue Gradient Wallpaper)
        guiGraphics.fill(phoneX, phoneY, phoneX + PHONE_WIDTH, phoneY + PHONE_HEIGHT, 0xFF0A0F1D);
        guiGraphics.fill(phoneX, phoneY + 100, phoneX + PHONE_WIDTH, phoneY + PHONE_HEIGHT, 0xFF141E30);

        // 3. Notch & Camera
        guiGraphics.fill(centerX - 18, phoneY - 5, centerX + 18, phoneY - 2, 0xFF000000);

        // 4. Status Bar
        guiGraphics.drawString(this.font, "12:00", phoneX + 8, phoneY + 6, 0xDDDDDD, false);
        guiGraphics.drawString(this.font, "5G ⚡", phoneX + PHONE_WIDTH - 30, phoneY + 6, 0xDDDDDD, false);

        // 5. Desktop App Icons Grid
        List<AppEntry> installed = getInstalledAppEntries();
        int gridStartX = phoneX + 12;
        int gridStartY = phoneY + 30;
        int iconSize = 32;
        int gapX = 8;
        int gapY = 12;

        for (int i = 0; i < installed.size(); i++) {
            AppEntry app = installed.get(i);
            int col = i % 3;
            int row = i / 3;

            int ix = gridStartX + col * (iconSize + gapX);
            int iy = gridStartY + row * (iconSize + gapY);

            boolean hovered = mouseX >= ix && mouseX <= ix + iconSize && mouseY >= iy && mouseY <= iy + iconSize;
            int bgColor = hovered ? 0xFFFFFFFF : app.color();

            // App Icon Box
            guiGraphics.fill(ix, iy, ix + iconSize, iy + iconSize, bgColor);
            guiGraphics.drawCenteredString(this.font, app.iconSymbol(), ix + iconSize / 2, iy + 10, 0xFFFFFF);

            // App Label
            guiGraphics.drawCenteredString(this.font, app.name(), ix + iconSize / 2, iy + iconSize + 2, 0xEEEEEE);
        }

        // 6. Bottom Dock Bar
        int dockY = phoneY + PHONE_HEIGHT - 38;
        guiGraphics.fill(phoneX + 8, dockY, phoneX + PHONE_WIDTH - 8, dockY + 1, 0x44FFFFFF);

        // 7. Bottom Home Button
        int homeBtnX = centerX - 15;
        int homeBtnY = phoneY + PHONE_HEIGHT - 20;
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
