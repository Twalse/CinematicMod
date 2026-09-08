package com.twalse.twmod.client.render;

import com.twalse.twmod.TwMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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

        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();

        // TODO: Render 3D waypoints/markers in world at target X Y Z coordinates
        // Example skeleton placeholder:
        // double targetX = 100.0, targetY = 64.0, targetZ = 100.0;
        // double distance = cameraPos.distanceTo(new Vec3(targetX, targetY, targetZ));
    }
}
