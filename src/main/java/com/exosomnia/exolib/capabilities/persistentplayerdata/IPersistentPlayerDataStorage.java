package com.exosomnia.exolib.capabilities.persistentplayerdata;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.List;

public interface IPersistentPlayerDataStorage extends INBTSerializable<CompoundTag> {

    void addWrapper(PersistentPlayerDataWrapper wrapper);

    void removeWrapper(PersistentPlayerDataWrapper wrapper);

    List<PersistentPlayerDataWrapper> getWrappers();

    void set(CompoundTag tag);

    CompoundTag get();

    void clear();
}
