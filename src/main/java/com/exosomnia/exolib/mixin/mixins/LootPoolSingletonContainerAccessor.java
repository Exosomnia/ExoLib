package com.exosomnia.exolib.mixin.mixins;

import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootPoolSingletonContainer.class)
public interface LootPoolSingletonContainerAccessor {

    @Mutable
    @Accessor("weight")
    void setWeight(int weight);

    @Accessor("weight")
    int getWeight();

    @Mutable
    @Accessor("quality")
    void setQuality(int quality);

    @Accessor("quality")
    int getQuality();
}
