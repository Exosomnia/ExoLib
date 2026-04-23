package com.exosomnia.exolib.networking.packets;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.config.SynchronizableConfig;
import com.exosomnia.exolib.particles.shapes.*;
import com.mojang.serialization.Codec;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ParticleShapePacket(byte[] shapeData) implements CustomPacketPayload {

    public static ParticleShapePacket of(ParticleShape shape) {
        FriendlyByteBuf shapeDataBuffer = new FriendlyByteBuf(Unpooled.buffer());

        ParticleType<?> options = shape.particle.getType();
        ResourceLocation resource = BuiltInRegistries.PARTICLE_TYPE.getKey(options);
        Codec<ParticleOptions> codec = (Codec<ParticleOptions>)options.codec().codec();

        shapeDataBuffer.writeResourceLocation(resource);
        shapeDataBuffer.writeJsonWithCodec(codec, shape.particle);
        shapeDataBuffer.writeJsonWithCodec(Vec3.CODEC, shape.origin);
        shapeDataBuffer.writeUtf(shape.shapeType.toString());
        shapeDataBuffer.writeJsonWithCodec((Codec<ParticleShapeOptions>) shape.getCodec(), shape.options);

        byte[] shapeData = shapeDataBuffer.array();
        shapeDataBuffer.release();
        return new ParticleShapePacket(shapeData);
    }

    public static final CustomPacketPayload.Type<ParticleShapePacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ExoLib.MODID, "particle_shape"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleShapePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BYTE_ARRAY,
                    ParticleShapePacket::shapeData,
                    ParticleShapePacket::new
            );

    @Override
    public CustomPacketPayload.Type<ParticleShapePacket> type() {
        return TYPE;
    }

    public static void handle(ParticleShapePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                FriendlyByteBuf configDataBuffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(packet.shapeData));
                parseBuffer(configDataBuffer).playOnClient(Minecraft.getInstance().level);
                configDataBuffer.release();
            }
        });
    }

    private static ParticleShape parseBuffer(FriendlyByteBuf buffer) {
        ResourceLocation particleType = buffer.readResourceLocation();
        Codec<? extends ParticleOptions> particleCodec = BuiltInRegistries.PARTICLE_TYPE.get(particleType).codec().codec();
        ParticleOptions particleOptions = buffer.readJsonWithCodec(particleCodec);

        Vec3 origin = buffer.readJsonWithCodec(Vec3.CODEC);

        ParticleShapeOptions shapeOptions;
        switch(ParticleShape.Shapes.valueOf(buffer.readUtf())) {
            case SPHERE:
                shapeOptions = buffer.readJsonWithCodec(ParticleShapeSphere.OPTIONS_CODEC);
                return new ParticleShapeSphere(particleOptions, origin, (ParticleShapeOptions.Sphere) shapeOptions);
            case DOME:
                shapeOptions = buffer.readJsonWithCodec(ParticleShapeDome.OPTIONS_CODEC);
                return new ParticleShapeDome(particleOptions, origin, (ParticleShapeOptions.Dome) shapeOptions);
            case RING:
                shapeOptions = buffer.readJsonWithCodec(ParticleShapeRing.OPTIONS_CODEC);
                return new ParticleShapeRing(particleOptions, origin, (ParticleShapeOptions.Ring) shapeOptions);
            case LINE:
                shapeOptions = buffer.readJsonWithCodec(ParticleShapeLine.OPTIONS_CODEC);
                return new ParticleShapeLine(particleOptions, origin, (ParticleShapeOptions.Line) shapeOptions);
            default:
                return null;
        }
    }
}
