package com.exosomnia.exolib.capabilities.persistentplayerdata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPersistentPlayerDataStorage extends INBTSerializable<CompoundTag> {

    void addWrapper(PersistentPlayerDataWrapper wrapper);
    void removeWrapper(PersistentPlayerDataWrapper wrapper);
    void set(CompoundTag tag);
    CompoundTag get();
    void clear();
}
