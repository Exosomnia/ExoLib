package com.exosomnia.exolib.capabilities.persistentplayerdata;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.UnknownNullability;

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
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        wrappers.forEach(wrapper -> wrapper.serialize(this));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.tag = tag;
    }

    @Override
    public void addWrapper(PersistentPlayerDataWrapper wrapper) {
        wrappers.add(wrapper);
    }

    @Override
    public void removeWrapper(PersistentPlayerDataWrapper wrapper) {
        wrappers.remove(wrapper);
    }

    @Override
    public List<PersistentPlayerDataWrapper> getWrappers() {
        return wrappers;
    }
}
