package com.exosomnia.exolib.networking;

import com.exosomnia.exolib.networking.packets.ParticleShapePacket;
import com.exosomnia.exolib.networking.packets.SynchronizeConfigPacket;
import com.exosomnia.exolib.networking.packets.TagUpdatePacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {

    public static void register(PayloadRegistrar registrar) {
        registrar.playToClient(
                TagUpdatePacket.TYPE,
                TagUpdatePacket.STREAM_CODEC,
                TagUpdatePacket::handle
        );

        registrar.playToClient(
                ParticleShapePacket.TYPE,
                ParticleShapePacket.STREAM_CODEC,
                ParticleShapePacket::handle
        );

        registrar.playToClient(
                SynchronizeConfigPacket.TYPE,
                SynchronizeConfigPacket.STREAM_CODEC,
                SynchronizeConfigPacket::handle
        );
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendToServer(CustomPacketPayload packet) {
        PacketDistributor.sendToServer(packet);
    }
}
