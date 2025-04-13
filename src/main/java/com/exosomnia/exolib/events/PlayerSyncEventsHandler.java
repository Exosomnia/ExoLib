package com.exosomnia.exolib.events;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.utils.EntityTagUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExoLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerSyncEventsHandler {

    @SubscribeEvent
    public static void loggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        EntityTagUtils.syncTags((ServerPlayer)event.getEntity());
    }

    @SubscribeEvent
    public static void changeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        EntityTagUtils.syncTags((ServerPlayer)event.getEntity());
    }
}
