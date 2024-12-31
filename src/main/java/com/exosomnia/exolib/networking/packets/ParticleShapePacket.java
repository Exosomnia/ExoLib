package com.exosomnia.exolib.networking.packets;

import com.exosomnia.exolib.particles.shapes.*;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ParticleShapePacket {

    private final ParticleShape shape;

    public ParticleShapePacket(ParticleShape shape) {
        this.shape = shape;
    }

    public ParticleShapePacket(FriendlyByteBuf buffer) {
        ResourceLocation particleType = buffer.readResourceLocation();
        Codec<? extends ParticleOptions> particleCodec = ForgeRegistries.PARTICLE_TYPES.getValue(particleType).codec();
        ParticleOptions particleOptions = buffer.readJsonWithCodec(particleCodec);

        Vec3 origin = buffer.readJsonWithCodec(Vec3.CODEC);

        ParticleShapeOptions shapeOptions;
        switch(ParticleShape.Shapes.valueOf(buffer.readUtf())) {
            case SPHERE:
                shapeOptions = buffer.readJsonWithCodec(ParticleShapeSphere.OPTIONS_CODEC);
                shape = new ParticleShapeSphere(particleOptions, origin, (ParticleShapeOptions.Sphere) shapeOptions);
                break;
            case DOME:
                shapeOptions = buffer.readJsonWithCodec(ParticleShapeDome.OPTIONS_CODEC);
                shape = new ParticleShapeDome(particleOptions, origin, (ParticleShapeOptions.Dome) shapeOptions);
                break;
            case RING:
                shapeOptions = buffer.readJsonWithCodec(ParticleShapeRing.OPTIONS_CODEC);
                shape = new ParticleShapeRing(particleOptions, origin, (ParticleShapeOptions.Ring) shapeOptions);
                break;
            case LINE:
                shapeOptions = buffer.readJsonWithCodec(ParticleShapeLine.OPTIONS_CODEC);
                shape = new ParticleShapeLine(particleOptions, origin, (ParticleShapeOptions.Line) shapeOptions);
                break;
            default:
                shape = null;
                break;
        }
    }

    public static void encode(ParticleShapePacket packet, FriendlyByteBuf buffer) {
        ParticleType<?> options = packet.shape.particle.getType();
        ResourceLocation resource = ForgeRegistries.PARTICLE_TYPES.getKey(options);
        Codec<ParticleOptions> codec = (Codec<ParticleOptions>) options.codec();

        buffer.writeResourceLocation(resource);
        buffer.writeJsonWithCodec(codec, packet.shape.particle);
        buffer.writeJsonWithCodec(Vec3.CODEC, packet.shape.origin);
        buffer.writeUtf(packet.shape.shapeType.toString());
        buffer.writeJsonWithCodec((Codec<ParticleShapeOptions>) packet.shape.getCodec(), packet.shape.options);
    }

    public static void handle(ParticleShapePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            NetworkDirection packetDirection = context.get().getDirection();
            if (packetDirection.equals(NetworkDirection.PLAY_TO_CLIENT)) {
                packet.shape.playOnClient(Minecraft.getInstance().level);
            }
        });
        context.get().setPacketHandled(true);
    }
}
