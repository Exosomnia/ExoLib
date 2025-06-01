package com.exosomnia.exolib.networking.packets;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.config.SynchronizableConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SynchronizeConfigPacket {

    SynchronizableConfig config;
    FriendlyByteBuf configData;

    public SynchronizeConfigPacket(SynchronizableConfig config) {
        this.config = config;
    }

    public SynchronizeConfigPacket(FriendlyByteBuf buffer) {
        this.config = ExoLib.CONFIG_SYNCHRONIZER.getConfig(buffer.readResourceLocation());
        this.configData = buffer;
    }

    public static void encode(SynchronizeConfigPacket packet, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(packet.config.getResourceLocation());
        packet.config.writeToBuffer(buffer);
    }

    public static void handle(SynchronizeConfigPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            NetworkDirection packetDirection = context.get().getDirection();
            if (packetDirection.equals(NetworkDirection.PLAY_TO_CLIENT)) {
                packet.config.readFromBuffer(packet.configData);
            }
        });
        context.get().setPacketHandled(true);
    }
}
