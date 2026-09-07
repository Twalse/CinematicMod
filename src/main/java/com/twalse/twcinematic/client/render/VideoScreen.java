package com.twalse.twcinematic.client.render;

import com.twalse.twcinematic.TwCinematic;
import com.twalse.twcinematic.util.Video;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.watermedia.api.player.videolan.VideoPlayer;

import java.util.concurrent.atomic.AtomicBoolean;

public class VideoScreen extends Screen {
    private final Video video;
    private final int volume;
    private VideoPlayer mediaPlayer;
    private boolean wasHudHidden = false;
    private final AtomicBoolean cleanedUp = new AtomicBoolean(false);

    public VideoScreen(Video video, int volume) {
        super(Component.empty());
        this.video = video;
        this.volume = volume;
    }

    @Override
    protected void init() {
        super.init();
        if (this.minecraft != null) {
            this.wasHudHidden = this.minecraft.options.hideGui;
            this.minecraft.options.hideGui = true;
            this.minecraft.mouseHandler.releaseMouse();
        }

        try {
            // Initialize VideoPlayer with Minecraft as the render executor.
            // DO NOT register any VLC EventListeners/EventAdapters to avoid JNA garbage collection crashes!
            this.mediaPlayer = new VideoPlayer(Minecraft.getInstance());
            this.mediaPlayer.setVolume(this.volume);
            this.mediaPlayer.start(this.video.getMediaUri());
        } catch (Exception e) {
            TwCinematic.LOGGER.error("Failed to initialize WaterMedia VideoPlayer", e);
            this.closeAndCleanup();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.mediaPlayer == null) {
            return;
        }

        // Poll playback status directly without event listeners
        if (this.mediaPlayer.isEnded() || this.mediaPlayer.isBroken() ||
           (this.mediaPlayer.isStopped() && !this.mediaPlayer.isReady() && !this.mediaPlayer.isWaiting() && !this.mediaPlayer.isLoading())) {
            this.closeAndCleanup();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (this.mediaPlayer == null) {
            guiGraphics.fill(0, 0, this.width, this.height, 0xFF000000);
            return;
        }

        int textureId = this.mediaPlayer.texture();
        if (textureId <= 0) {
            // Render black screen while first frame loads
            guiGraphics.fill(0, 0, this.width, this.height, 0xFF000000);
            return;
        }

        int width = this.width;
        int height = this.height;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, textureId);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        Matrix4f matrix = guiGraphics.pose().last().pose();

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, 0, height, 0).uv(0.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix, width, height, 0).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix, width, 0, 0).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix, 0, 0, 0).uv(0.0F, 0.0F).endVertex();
        tesselator.end();

        RenderSystem.disableBlend();
    }

    private void closeAndCleanup() {
        if (cleanedUp.compareAndSet(false, true)) {
            if (this.mediaPlayer != null) {
                try {
                    this.mediaPlayer.pause();
                    this.mediaPlayer.stop();
                    this.mediaPlayer.release();
                } catch (Throwable ignored) {}
                this.mediaPlayer = null;
            }
            if (this.minecraft != null) {
                this.minecraft.options.hideGui = this.wasHudHidden;
                this.minecraft.mouseHandler.grabMouse();
            }
            Minecraft.getInstance().tell(() -> {
                if (Minecraft.getInstance().screen == this) {
                    Minecraft.getInstance().setScreen(null);
                }
            });
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Block all keyboard inputs (including ESC key) during cutscene playback (Unskippable)
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Block mouse click inputs
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        this.closeAndCleanup();
        super.onClose();
    }

    @Override
    public void removed() {
        this.closeAndCleanup();
        super.removed();
    }
}
