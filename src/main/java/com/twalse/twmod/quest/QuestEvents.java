package com.twalse.twmod.quest;

import com.twalse.twmod.TwMod;
import com.twalse.twmod.networking.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class QuestEvents {

    @Mod.EventBusSubscriber(modid = TwMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(PlayerQuestData.class);
        }
    }

    @Mod.EventBusSubscriber(modid = TwMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBusEvents {
        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                if (!event.getObject().getCapability(PlayerQuestProvider.PLAYER_QUEST).isPresent()) {
                    event.addCapability(new ResourceLocation(TwMod.MODID, "quest_data"), new PlayerQuestProvider());
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event) {
            event.getOriginal().getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(oldData -> {
                event.getEntity().getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(newData -> {
                    newData.copyFrom(oldData);
                });
            });
        }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                PacketHandler.syncQuestData(serverPlayer);
            }
        }

        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                PacketHandler.syncQuestData(serverPlayer);
            }
        }

        @SubscribeEvent
        public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                PacketHandler.syncQuestData(serverPlayer);
            }
        }
    }
}
