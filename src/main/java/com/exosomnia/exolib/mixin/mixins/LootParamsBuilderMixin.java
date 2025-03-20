package com.exosomnia.exolib.mixin.mixins;

import com.exosomnia.exolib.mixin.interfaces.ILootParamsMixin;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootParams.Builder.class)
public abstract class LootParamsBuilderMixin {

    @Inject(method = "create", at = @At("RETURN"), cancellable = false)
    private void createInject(LootContextParamSet lootContextParamSet, CallbackInfoReturnable<LootParams> ci) {
        ((ILootParamsMixin)ci.getReturnValue()).setCause(LootContextParamSets.getKey(lootContextParamSet));
    }
}
