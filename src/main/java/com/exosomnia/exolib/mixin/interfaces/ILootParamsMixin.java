package com.exosomnia.exolib.mixin.interfaces;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;

public interface ILootParamsMixin {

    ResourceLocation getCause();
    void setCause(ResourceLocation cause);

    boolean shouldLootModify();

    Container getContainer();
    void setContainer(Container container);
}
