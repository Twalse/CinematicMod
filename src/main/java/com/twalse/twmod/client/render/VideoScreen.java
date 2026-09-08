package com.twalse.twmod.client.render;

import com.twalse.twmod.TwMod;
import com.twalse.twmod.util.Video;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoScreen extends Screen {
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

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
            TwMod.LOGGER.info("Starting cinematic video playback: {} (Volume: {})", this.video.getName(), this.volume);
            this.mediaPlayer = new VideoPlayer(Minecraft.getInstance());
            this.mediaPlayer.setVolume(this.volume);
            this.mediaPlayer.start(this.video.getMediaUri());
        } catch (Exception e) {
            TwMod.LOGGER.error("Failed to initialize WaterMedia VideoPlayer for video: {}", this.video.getName(), e);
            this.closeAndCleanup();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.mediaPlayer == null) {
            return;
        }

        if (this.mediaPlayer.isEnded()) {
            TwMod.LOGGER.info("Cinematic video ended naturally: {}", this.video.getName());
            this.closeAndCleanup();
        } else if (this.mediaPlayer.isBroken()) {
            TwMod.LOGGER.error("Cinematic video player entered broken state: {}", this.video.getName());
            this.closeAndCleanup();
        } else if (this.mediaPlayer.isStopped() && !this.mediaPlayer.isReady() && !this.mediaPlayer.isWaiting() && !this.mediaPlayer.isLoading()) {
            TwMod.LOGGER.info("Cinematic video player stopped: {}", this.video.getName());
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
        if (this.cleanedUp.compareAndSet(false, true)) {
            TwMod.LOGGER.info("Closing cinematic screen and scheduling player release for: {}", this.video.getName());
            final VideoPlayer playerToRelease = this.mediaPlayer;
            this.mediaPlayer = null;

            if (this.minecraft != null) {
                this.minecraft.options.hideGui = this.wasHudHidden;
                this.minecraft.mouseHandler.grabMouse();
            }

            Minecraft.getInstance().tell(() -> {
                if (Minecraft.getInstance().screen == this) {
                    Minecraft.getInstance().setScreen(null);
                }
            });

            if (playerToRelease != null) {
                EXECUTOR.schedule(() -> {
                    try {
                        playerToRelease.stop();
                        playerToRelease.release();
                        TwMod.LOGGER.info("Successfully released VideoPlayer for: {}", this.video.getName());
                    } catch (Exception e) {
                        TwMod.LOGGER.error("Error releasing video player for: {}", this.video.getName(), e);
                    }
                }, 1, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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
        TwMod.LOGGER.info("VideoScreen onClose called for: {}", this.video.getName());
        this.closeAndCleanup();
        super.onClose();
    }

    @Override
    public void removed() {
        TwMod.LOGGER.info("VideoScreen removed called for: {}", this.video.getName());
        this.closeAndCleanup();
        super.removed();
    }
}
