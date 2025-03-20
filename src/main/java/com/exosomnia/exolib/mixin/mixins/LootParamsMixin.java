package com.exosomnia.exolib.mixin.mixins;

import com.exosomnia.exolib.mixin.interfaces.ILootParamsMixin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LootParams.class)
public abstract class LootParamsMixin implements ILootParamsMixin {
    private final static ResourceLocation CHEST_CAUSE = ResourceLocation.bySeparator("minecraft:chest", ':');

    private ResourceLocation cause;
    private Container container;

    public ResourceLocation getCause() { return cause; }
    public void setCause(ResourceLocation cause) { this.cause = cause; }

    public boolean shouldLootModify() {
        if (cause.equals(CHEST_CAUSE)) { return container != null; }
        return true;
    }

    @Nullable
    public Container getContainer() { return this.container; }
    public void setContainer(Container container) { this.container = container; }
}
