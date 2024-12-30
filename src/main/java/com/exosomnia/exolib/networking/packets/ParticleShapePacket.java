package com.exosomnia.exolib.networking.packets;

import com.exosomnia.exolib.particles.shapes.ParticleShapeOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ParticleShapePacket {

    private final int shape;
    private final ResourceLocation particle;
    private final ParticleOptions particleOptions;
    private final BlockPos origin;
    private final ParticleShapeOptions shapeOptions;

    public ParticleShapePacket() {
    }

    public ParticleShapePacket(FriendlyByteBuf buffer) {

    }

    public static void encode(ParticleShapePacket packet, FriendlyByteBuf buffer) {
    }

    public static void handle(ParticleShapePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            NetworkDirection packetDirection = context.get().getDirection();
            if (packetDirection.equals(NetworkDirection.PLAY_TO_CLIENT)) {

            }
        });
        context.get().setPacketHandled(true);
    }
}
