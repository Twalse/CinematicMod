package com.twalse.twmod.networking.message;

import com.twalse.twmod.item.ModItems;
import com.twalse.twmod.networking.PacketHandler;
import com.twalse.twmod.quest.PlayerQuestProvider;
import com.twalse.twmod.util.TwLogger;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class BuyItemPacket {
    public final String itemId;

    public BuyItemPacket(String itemId) {
        this.itemId = itemId != null ? itemId : "";
    }

    public static void encode(BuyItemPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.itemId);
    }

    public static BuyItemPacket decode(FriendlyByteBuf buf) {
        String itemId = buf.readUtf();
        return new BuyItemPacket(itemId);
    }

    public static void handle(BuyItemPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                    int currentMoney = data.getVariable("money");
                    int price = 0;
                    ItemStack rewardStack = ItemStack.EMPTY;

                    if ("lockpick".equalsIgnoreCase(msg.itemId)) {
                        price = 50;
                        rewardStack = new ItemStack(ModItems.LOCKPICK.get());
                    } else if ("medkit".equalsIgnoreCase(msg.itemId)) {
                        price = 200;
                        rewardStack = new ItemStack(ModItems.MEDKIT.get());
                    } else if ("syringe".equalsIgnoreCase(msg.itemId)) {
                        price = 150;
                        rewardStack = new ItemStack(ModItems.SYRINGE.get());
                    } else if ("tacz_9mm".equalsIgnoreCase(msg.itemId)) {
                        price = 100;
                        Item ammoItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("tacz", "ammo"));
                        if (ammoItem != null) {
                            rewardStack = new ItemStack(ammoItem, 30);
                            rewardStack.getOrCreateTag().putString("AmmoId", "tacz:9mm");
                        } else {
                            // Fallback if TacZ is not present
                            rewardStack = new ItemStack(ModItems.LOCKPICK.get(), 2);
                        }
                    } else if ("heavy_cargo".equalsIgnoreCase(msg.itemId)) {
                        price = 300;
                        rewardStack = new ItemStack(ModItems.HEAVY_CARGO.get());
                    }

                    if (price > 0 && !rewardStack.isEmpty()) {
                        if (currentMoney >= price) {
                            data.setVariable("money", currentMoney - price);
                            player.getInventory().add(rewardStack);
                            PacketHandler.syncQuestData(player);
                            TwLogger.info("Player {} bought item {} for {} money", player.getName().getString(), msg.itemId, price);
                            player.sendSystemMessage(Component.literal("§aУспешная покупка: " + rewardStack.getHoverName().getString()));
                        } else {
                            player.sendSystemMessage(Component.literal("§cНедостаточно средств!"));
                        }
                    }
                });
            }
        });
        ctx.setPacketHandled(true);
    }
}
