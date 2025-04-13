package com.exosomnia.exolib.mixin.interfaces;

import net.minecraft.world.item.ItemStack;

public interface ILivingEntityMixin {

    ItemStack getLastUsedItemStack();
    void setLastUsedItemStack(ItemStack itemStack);
}
