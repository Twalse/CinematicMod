package com.twalse.twmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class SleepingBagBlock extends Block {

    public SleepingBagBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOL)
                .strength(0.8F)
                .sound(SoundType.WOOL)
                .noOcclusion());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            if (player instanceof ServerPlayer serverPlayer) {
                // 1. Set respawn point for player
                serverPlayer.setRespawnPosition(level.dimension(), pos, 0.0F, true, true);

                // 2. Send actionbar message
                serverPlayer.getServer().getCommands().performPrefixedCommand(
                        serverPlayer.createCommandSourceStack(),
                        String.format("title %s actionbar {\"text\":\"Точка возрождения сохранена\",\"color\":\"green\"}", serverPlayer.getGameProfile().getName())
                );

                // 3. Play vanilla armor equip leather sound
                level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
