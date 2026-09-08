package com.twalse.twmod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PhoneScreen extends Screen {

    public PhoneScreen() {
        super(Component.literal("Smartphone"));
    }

    @Override
    protected void init() {
        super.init();
        // TODO: Initialize smartphone frame, app icons (Messages, Map, Quests, Bank)
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, "Smartphone UI (Skeleton)", this.width / 2, this.height / 2, 0x55FF55);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
