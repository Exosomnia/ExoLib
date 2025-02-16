package com.exosomnia.exolib.capabilities.persistentplayerdata;

import net.minecraft.nbt.CompoundTag;

public class PersistentPlayerDataStorage implements IPersistentPlayerDataStorage {

    private CompoundTag tag;

    public PersistentPlayerDataStorage(CompoundTag tag) {
        this.tag = tag;
    }

    public void set(CompoundTag tag) { this.tag = tag; }
    public CompoundTag get() { return this.tag; }
    public void clear() { this.tag = new CompoundTag(); }

    @Override
    public CompoundTag serializeNBT() { return tag; }

    @Override
    public void deserializeNBT(CompoundTag nbt) { this.tag = nbt; }
}
