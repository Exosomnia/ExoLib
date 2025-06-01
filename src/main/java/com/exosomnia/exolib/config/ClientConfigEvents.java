package com.exosomnia.exolib.config;

import com.exosomnia.exolib.ExoLib;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExoLib.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientConfigEvents {

    @SubscribeEvent
    public static void clientConfigSynchronize(ClientPlayerNetworkEvent.LoggingOut event) {
        for (SynchronizableConfig config : ExoLib.CONFIG_SYNCHRONIZER.getConfigs()) {
            config.readFromFile();
        }
    }
}
