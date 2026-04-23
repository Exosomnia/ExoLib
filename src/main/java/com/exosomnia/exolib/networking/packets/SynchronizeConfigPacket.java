package com.exosomnia.exolib.networking.packets;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.config.SynchronizableConfig;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SynchronizeConfigPacket(ResourceLocation resourceLocation, byte[] configData) implements CustomPacketPayload {

    public static SynchronizeConfigPacket of(SynchronizableConfig config) {
        FriendlyByteBuf configDataBuffer = new FriendlyByteBuf(Unpooled.buffer());
        config.writeToBuffer(configDataBuffer);
        byte[] configData = configDataBuffer.array();
        configDataBuffer.release();
        return new SynchronizeConfigPacket(config.getResourceLocation(), configData);
    }

    public static final CustomPacketPayload.Type<SynchronizeConfigPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ExoLib.MODID, "synchronize_config"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizeConfigPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC,
                    SynchronizeConfigPacket::resourceLocation,
                    ByteBufCodecs.BYTE_ARRAY,
                    SynchronizeConfigPacket::configData,
                    SynchronizeConfigPacket::new
            );

    @Override
    public CustomPacketPayload.Type<SynchronizeConfigPacket> type() {
        return TYPE;
    }

    public static void handle(SynchronizeConfigPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                SynchronizableConfig config = ExoLib.CONFIG_SYNCHRONIZER.getConfig(packet.resourceLocation);
                if (config == null) return;

                FriendlyByteBuf configDataBuffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(packet.configData));
                config.readFromBuffer(configDataBuffer);
                configDataBuffer.release();
            }
        });
    }
}
