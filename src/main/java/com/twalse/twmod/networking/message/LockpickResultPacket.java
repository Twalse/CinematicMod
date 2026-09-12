package com.twalse.twmod.networking.message;

import com.twalse.twmod.item.ModItems;
import com.twalse.twmod.util.TwLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LockpickResultPacket {
    public final boolean success;
    public final BlockPos doorPos;

    public LockpickResultPacket(boolean success) {
        this(success, null);
    }

    public LockpickResultPacket(boolean success, BlockPos doorPos) {
        this.success = success;
        this.doorPos = doorPos;
    }

    public static void encode(LockpickResultPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.success);
        boolean hasPos = msg.doorPos != null;
        buf.writeBoolean(hasPos);
        if (hasPos) {
            buf.writeBlockPos(msg.doorPos);
        }
    }

    public static LockpickResultPacket decode(FriendlyByteBuf buf) {
        boolean success = buf.readBoolean();
        boolean hasPos = buf.readBoolean();
        BlockPos pos = hasPos ? buf.readBlockPos() : null;
        return new LockpickResultPacket(success, pos);
    }

    public static void handle(LockpickResultPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                Level level = player.level();

                if (msg.success) {
                    TwLogger.info("Player {} successfully lockpicked!", player.getName().getString());
                    player.sendSystemMessage(Component.literal("§aВзлом успешен!"));

                    if (msg.doorPos != null) {
                        BlockState state = level.getBlockState(msg.doorPos);
                        if (state.getBlock() instanceof DoorBlock doorBlock) {
                            doorBlock.setOpen(player, level, state, msg.doorPos, true);
                            level.playSound(null, msg.doorPos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                    }
                } else {
                    TwLogger.info("Player {} failed lockpicking (pick broke).", player.getName().getString());
                    player.sendSystemMessage(Component.literal("§cОтмычка сломалась..."));

                    // Consume 1 lockpick from player inventory on failure
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        ItemStack stack = player.getInventory().getItem(i);
                        if (!stack.isEmpty() && stack.is(ModItems.LOCKPICK.get())) {
                            stack.shrink(1);
                            break;
                        }
                    }

                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
