package com.twalse.twmod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LockpickScreen extends Screen {

    public LockpickScreen() {
        super(Component.literal("Lockpicking"));
    }

    @Override
    protected void init() {
        super.init();
        // TODO: Initialize lockpick pins, tension wrench, and interactable UI components
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, "Lockpick Minigame (Skeleton)", this.width / 2, this.height / 2, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
