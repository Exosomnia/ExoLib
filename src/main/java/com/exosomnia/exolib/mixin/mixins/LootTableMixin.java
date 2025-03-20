package com.exosomnia.exolib.mixin.mixins;

import com.exosomnia.exolib.mixin.interfaces.ILootParamsMixin;
import net.minecraft.world.Container;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootTable.class)
public abstract class LootTableMixin {

    @Inject(method = "fill", at = @At("HEAD"))
    private void injectContainerParams(Container container, LootParams lootParams, long seed, CallbackInfo ci) {
        ((ILootParamsMixin)lootParams).setContainer(container);
    }
}
