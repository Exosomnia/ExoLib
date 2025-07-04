package com.exosomnia.exolib.events;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.capabilities.persistentplayerdata.PersistentPlayerDataProvider;
import com.exosomnia.exolib.capabilities.persistentplayerdata.PersistentPlayerDataWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExoLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerCapabilitiesEventHandler {

    @SubscribeEvent
    public static void clone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(PersistentPlayerDataProvider.PLAYER_DATA).ifPresent(playerData -> {
            event.getEntity().getCapability(PersistentPlayerDataProvider.PLAYER_DATA).ifPresent(newPlayerData -> {
                newPlayerData.set(playerData.get());
                for (PersistentPlayerDataWrapper wrapper : playerData.getWrappers()) {
                    newPlayerData.addWrapper(wrapper);
                }
            });
        });
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(ExoLib.MODID, "persistent_playerdata"), new PersistentPlayerDataProvider());
        }
    }
}
