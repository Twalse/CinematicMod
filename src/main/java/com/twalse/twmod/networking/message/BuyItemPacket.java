package com.twalse.twmod.networking.message;

import com.twalse.twmod.item.ModItems;
import com.twalse.twmod.networking.PacketHandler;
import com.twalse.twmod.quest.PlayerQuestProvider;
import com.twalse.twmod.util.TwLogger;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkEvent;

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

                    if ("heavy_cargo".equalsIgnoreCase(msg.itemId)) {
                        price = 100;
                        rewardStack = new ItemStack(ModItems.HEAVY_CARGO.get());
                    } else if ("medkit".equalsIgnoreCase(msg.itemId)) {
                        price = 50;
                        rewardStack = new ItemStack(Items.GOLDEN_APPLE);
                    } else if ("lockpick_kit".equalsIgnoreCase(msg.itemId)) {
                        price = 30;
                        rewardStack = new ItemStack(Items.IRON_INGOT, 2);
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
