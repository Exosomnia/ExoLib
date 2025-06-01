package com.exosomnia.exolib.config;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface SynchronizableConfig {

    FriendlyByteBuf writeToBuffer(FriendlyByteBuf buffer);

    SynchronizableConfig readFromBuffer(FriendlyByteBuf buffer);

    void readFromFile();

    ResourceLocation getResourceLocation();
}
