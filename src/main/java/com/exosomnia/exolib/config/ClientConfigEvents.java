package com.exosomnia.exolib.config;

import com.exosomnia.exolib.ExoLib;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = ExoLib.MODID, value = Dist.CLIENT)
public class ClientConfigEvents {

    @SubscribeEvent
    public static void clientConfigSynchronize(ClientPlayerNetworkEvent.LoggingOut event) {
        for (SynchronizableConfig config : ExoLib.CONFIG_SYNCHRONIZER.getConfigs()) {
            config.readFromFile();
        }
    }
}
