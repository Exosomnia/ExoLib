package com.exosomnia.exolib.capabilities.persistentplayerdata;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class PersistentPlayerDataProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<IPersistentPlayerDataStorage> PLAYER_DATA = CapabilityManager.get(new CapabilityToken<>(){});
    private final LazyOptional<IPersistentPlayerDataStorage> instance = LazyOptional.of(() -> new PersistentPlayerDataStorage(new CompoundTag()));

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == PLAYER_DATA ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() { return instance.resolve().get().serializeNBT(); }

    @Override
    public void deserializeNBT(CompoundTag nbt) { instance.resolve().get().deserializeNBT(nbt); }
}
