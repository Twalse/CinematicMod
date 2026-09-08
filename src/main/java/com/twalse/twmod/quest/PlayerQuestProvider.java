package com.twalse.twmod.quest;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerQuestProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<PlayerQuestData> PLAYER_QUEST = CapabilityManager.get(new CapabilityToken<>() {});

    private PlayerQuestData questData = null;
    private final LazyOptional<PlayerQuestData> optional = LazyOptional.of(this::createPlayerQuestData);

    private PlayerQuestData createPlayerQuestData() {
        if (this.questData == null) {
            this.questData = new PlayerQuestData();
        }
        return this.questData;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_QUEST) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerQuestData().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerQuestData().loadNBTData(nbt);
    }
}
