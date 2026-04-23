package com.exosomnia.exolib.events;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.capabilities.persistentplayerdata.IPersistentPlayerDataStorage;
import com.exosomnia.exolib.capabilities.persistentplayerdata.PersistentPlayerDataWrapper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;


@EventBusSubscriber(modid = ExoLib.MODID)
public class PlayerCapabilitiesEventHandler {

    @SubscribeEvent
    public static void clone(PlayerEvent.Clone event) {
        IPersistentPlayerDataStorage oldData = event.getOriginal().getCapability(ExoLib.REGISTRY.PERSISTENT_PLAYER_DATA);
        IPersistentPlayerDataStorage newData = event.getEntity().getCapability(ExoLib.REGISTRY.PERSISTENT_PLAYER_DATA);
        newData.set(oldData.get());
        for (PersistentPlayerDataWrapper wrapper : oldData.getWrappers()) {
            newData.addWrapper(wrapper);
        }
    }
}
