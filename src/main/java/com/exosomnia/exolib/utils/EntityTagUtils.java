package com.exosomnia.exolib.utils;

import com.exosomnia.exolib.networking.PacketHandler;
import com.exosomnia.exolib.networking.packets.TagUpdatePacket;
import net.minecraft.server.level.ServerPlayer;

public class EntityTagUtils {

    /***
     * Syncs any tags on the ServerPlayer with the player's client.
     * @param player The player to sync tags on
     */
    public static void syncTags(ServerPlayer player) {
        for (String tag : player.getTags()) {
            PacketHandler.sendToPlayer(new TagUpdatePacket(tag, true), player);
        }
    }
}
