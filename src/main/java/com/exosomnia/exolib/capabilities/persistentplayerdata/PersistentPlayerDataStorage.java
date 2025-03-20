package com.exosomnia.exolib.capabilities.persistentplayerdata;

import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class PersistentPlayerDataStorage implements IPersistentPlayerDataStorage {

    List<PersistentPlayerDataWrapper> wrappers = new ArrayList<>();
    private CompoundTag tag;

    public PersistentPlayerDataStorage(CompoundTag tag) {
        this.tag = tag;
    }

    public void set(CompoundTag tag) { this.tag = tag; }
    public CompoundTag get() { return this.tag; }
    public void clear() { this.tag = new CompoundTag(); }

    @Override
    public CompoundTag serializeNBT() {
        wrappers.forEach(wrapper -> wrapper.serialize(this));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) { this.tag = nbt; }

    @Override
    public void addWrapper(PersistentPlayerDataWrapper wrapper) {
        wrappers.add(wrapper);
    }

    @Override
    public void removeWrapper(PersistentPlayerDataWrapper wrapper) {
        wrappers.remove(wrapper);
    }
}
