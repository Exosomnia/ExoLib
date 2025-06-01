package com.exosomnia.exolib.config;

import com.exosomnia.exolib.networking.PacketHandler;
import com.exosomnia.exolib.networking.packets.SynchronizeConfigPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collection;
import java.util.HashMap;

public class ConfigSynchronizer {

    protected HashMap<ResourceLocation, SynchronizableConfig> configs = new HashMap<>();

    public void addConfig(SynchronizableConfig config) {
        configs.put(config.getResourceLocation(), config);
    }

    public SynchronizableConfig getConfig(ResourceLocation location) {
        return configs.get(location);
    }

    public Collection<SynchronizableConfig> getConfigs() {
        return configs.values();
    }

    @SubscribeEvent
    public void serverConfigSynchronize(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer)event.getEntity();
        for (SynchronizableConfig config : configs.values()) {
            PacketHandler.sendToPlayer(new SynchronizeConfigPacket(config), player);
        }
    }
}
