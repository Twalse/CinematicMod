package com.twalse.twcinematic.client.gui;

import com.twalse.twcinematic.TwCinematic;
import com.twalse.twcinematic.config.CinematicConfig;
import com.twalse.twcinematic.util.FileManager;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.File;

public class ConfigScreen extends Screen {
    private final Screen lastScreen;
    private EditBox pathEditBox;

    public ConfigScreen(Screen lastScreen) {
        super(Component.literal("TwCinematic Settings"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 4;

        this.pathEditBox = new EditBox(this.font, centerX - 150, startY + 30, 300, 20, Component.literal("Video Folder Path"));
        this.pathEditBox.setMaxLength(512);
        this.pathEditBox.setValue(CinematicConfig.VIDEO_FOLDER_PATH.get());
        this.addRenderableWidget(this.pathEditBox);

        // Save Button
        this.addRenderableWidget(Button.builder(Component.literal("Save Path"), button -> {
            String newPath = this.pathEditBox.getValue().trim();
            if (!newPath.isEmpty()) {
                CinematicConfig.VIDEO_FOLDER_PATH.set(newPath);
                CinematicConfig.CLIENT_SPEC.save();

                File folder = new File(newPath);
                if (!folder.isAbsolute()) {
                    folder = new File(net.minecraft.client.Minecraft.getInstance().gameDirectory, newPath);
                }
                if (FileManager.getInstance() != null) {
                    FileManager.getInstance().setDirectory(folder);
                }
            }
        }).bounds(centerX - 150, startY + 60, 145, 20).build());

        // Open Folder Button
        this.addRenderableWidget(Button.builder(Component.literal("Open Folder"), button -> {
            String currentPath = this.pathEditBox.getValue().trim();
            File folder = new File(currentPath);
            if (!folder.isAbsolute()) {
                folder = new File(net.minecraft.client.Minecraft.getInstance().gameDirectory, currentPath);
            }
            if (!folder.exists()) {
                folder.mkdirs();
            }
            Util.getPlatform().openUri(folder.toURI());
        }).bounds(centerX + 5, startY + 60, 145, 20).build());

        // Back / Close Button
        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> {
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(centerX - 100, startY + 100, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 4, 0xFFFFFF);
        guiGraphics.drawString(this.font, "Video Folder Path:", this.width / 2 - 150, this.height / 4 + 18, 0xA0A0A0);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }
}
