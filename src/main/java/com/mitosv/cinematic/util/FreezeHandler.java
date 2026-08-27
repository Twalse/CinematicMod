package com.mitosv.cinematic.util;

import com.mitosv.cinematic.Cinematic;
import com.mitosv.cinematic.client.render.VideoScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cinematic.MODID, value = Dist.CLIENT)
public class FreezeHandler {

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        if (Minecraft.getInstance().screen instanceof VideoScreen) {
            // Freeze movement (walking, jumping, sneaking)
            event.getInput().forwardImpulse = 0;
            event.getInput().leftImpulse = 0;
            event.getInput().up = false;
            event.getInput().down = false;
            event.getInput().left = false;
            event.getInput().right = false;
            event.getInput().jumping = false;
            event.getInput().shiftKeyDown = false;
        }
    }

    @SubscribeEvent
    public static void onInteractLeft(PlayerInteractEvent.LeftClickEmpty event) {
        if (Minecraft.getInstance().screen instanceof VideoScreen) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onInteractRight(PlayerInteractEvent.RightClickEmpty event) {
        if (Minecraft.getInstance().screen instanceof VideoScreen) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onOpeningScreen(ScreenEvent.Opening event) {
        // Prevent opening any other screen/inventory while VideoScreen is active
        if (Minecraft.getInstance().screen instanceof VideoScreen && !(event.getNewScreen() instanceof VideoScreen)) {
            if (event.getNewScreen() != null) {
                event.setCanceled(true);
            }
        }
    }
}
