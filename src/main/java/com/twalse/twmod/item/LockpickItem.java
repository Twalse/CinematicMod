package com.twalse.twmod.item;

import com.twalse.twmod.networking.PacketHandler;
import com.twalse.twmod.networking.message.OpenLockpickPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class LockpickItem extends Item {

    public LockpickItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide && context.getPlayer() instanceof ServerPlayer serverPlayer) {
            BlockPos pos = context.getClickedPos();
            BlockState state = context.getLevel().getBlockState(pos);

            if (state.getBlock() instanceof DoorBlock) {
                // If clicked lower or upper half of iron door, normalize pos to lower half
                if (state.hasProperty(DoorBlock.HALF) && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
                    pos = pos.below();
                    state = context.getLevel().getBlockState(pos);
                }

                // Check if door is currently closed
                if (state.hasProperty(DoorBlock.OPEN) && !state.getValue(DoorBlock.OPEN)) {
                    PacketHandler.sendToPlayer(new OpenLockpickPacket(5, 5, pos), serverPlayer);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
