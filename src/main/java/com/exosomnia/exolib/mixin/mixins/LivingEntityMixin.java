package com.exosomnia.exolib.mixin.mixins;

import com.exosomnia.exolib.mixin.interfaces.ILivingEntityMixin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntityMixin {

    private ItemStack lastUsedItemStack;

    @Override
    public ItemStack getLastUsedItemStack() {
        return lastUsedItemStack;
    }

    @Override
    public void setLastUsedItemStack(ItemStack itemStack) {
        lastUsedItemStack = itemStack;
    }
}
