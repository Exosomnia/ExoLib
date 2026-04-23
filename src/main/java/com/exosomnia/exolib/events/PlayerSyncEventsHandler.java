package com.exosomnia.exolib.events;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.utils.EntityTagUtils;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;


@EventBusSubscriber(modid = ExoLib.MODID)
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
