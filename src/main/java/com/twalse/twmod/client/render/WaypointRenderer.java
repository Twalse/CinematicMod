package com.twalse.twmod.client.render;

import com.twalse.twmod.TwMod;
import com.twalse.twmod.quest.ClientQuestData;
import com.twalse.twmod.quest.WaypointData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.List;

@Mod.EventBusSubscriber(modid = TwMod.MODID, value = Dist.CLIENT)
public class WaypointRenderer {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        List<WaypointData> waypoints = ClientQuestData.getWaypoints();
        if (waypoints.isEmpty()) {
            return;
        }

        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        Font font = mc.font;
        PoseStack poseStack = event.getPoseStack();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        for (WaypointData waypoint : waypoints) {
            Vec3 targetPos = new Vec3(waypoint.getX(), waypoint.getY(), waypoint.getZ());
            double distance = cameraPos.distanceTo(targetPos);

            poseStack.pushPose();
            poseStack.translate(
                    targetPos.x - cameraPos.x,
                    targetPos.y - cameraPos.y,
                    targetPos.z - cameraPos.z
            );

            // Rotate PoseStack to face camera (Billboard)
            poseStack.mulPose(camera.rotation());

            int color = waypoint.getColor();
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            // 1. Render Diamond Beam Marker
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            Matrix4f matrix = poseStack.last().pose();

            float size = 0.3f;
            float topY = 0.8f;
            float midY = 0.4f;
            float botY = 0.0f;

            buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

            // Upper Diamond Top Pyramids
            addTriangle(buffer, matrix, 0, topY, 0, -size, midY, 0, 0, midY, size, r, g, b, 200);
            addTriangle(buffer, matrix, 0, topY, 0, 0, midY, size, size, midY, 0, r, g, b, 200);
            addTriangle(buffer, matrix, 0, topY, 0, size, midY, 0, 0, midY, -size, r, g, b, 200);
            addTriangle(buffer, matrix, 0, topY, 0, 0, midY, -size, -size, midY, 0, r, g, b, 200);

            // Lower Diamond Bottom Pyramids
            addTriangle(buffer, matrix, 0, botY, 0, 0, midY, size, -size, midY, 0, r, g, b, 200);
            addTriangle(buffer, matrix, 0, botY, 0, size, midY, 0, 0, midY, size, r, g, b, 200);
            addTriangle(buffer, matrix, 0, botY, 0, 0, midY, -size, size, midY, 0, r, g, b, 200);
            addTriangle(buffer, matrix, 0, botY, 0, -size, midY, 0, 0, midY, -size, r, g, b, 200);

            tesselator.end();

            // 2. Render Text Label above Diamond
            String distText = String.format("%s [%dm]", waypoint.getName(), (int) distance);
            Component label = Component.literal(distText);
            float labelScale = 0.025f;

            poseStack.translate(0, 1.1, 0);
            poseStack.scale(-labelScale, -labelScale, labelScale);

            float labelWidth = font.width(label);
            Matrix4f fontMatrix = poseStack.last().pose();

            // Background for text label
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            buffer.vertex(fontMatrix, -labelWidth / 2f - 2, -2, 0).color(0, 0, 0, 150).endVertex();
            buffer.vertex(fontMatrix, -labelWidth / 2f - 2, 9, 0).color(0, 0, 0, 150).endVertex();
            buffer.vertex(fontMatrix, labelWidth / 2f + 2, 9, 0).color(0, 0, 0, 150).endVertex();
            buffer.vertex(fontMatrix, labelWidth / 2f + 2, -2, 0).color(0, 0, 0, 150).endVertex();
            tesselator.end();

            font.drawInBatch(label, -labelWidth / 2f, 0, color | 0xFF000000, false, fontMatrix, mc.renderBuffers().bufferSource(), Font.DisplayMode.SEE_THROUGH, 0, 0xF000F0);
            mc.renderBuffers().bufferSource().endBatch();

            poseStack.popPose();
        }

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private static void addTriangle(BufferBuilder buffer, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, int r, int g, int b, int a) {
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x3, y3, z3).color(r, g, b, a).endVertex();
    }
}
