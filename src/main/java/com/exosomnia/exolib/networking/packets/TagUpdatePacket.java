package com.exosomnia.exolib.networking.packets;

import com.exosomnia.exolib.ExoLib;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;

public record TagUpdatePacket(Set<String> tags) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TagUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ExoLib.MODID, "tag_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TagUpdatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.STRING_UTF8),
                    TagUpdatePacket::tags,
                    TagUpdatePacket::new
            );

    @Override
    public CustomPacketPayload.Type<TagUpdatePacket> type() {
        return TYPE;
    }

    public static void handle(TagUpdatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) return;

                Set<String> tags = player.getTags();
                tags.clear();
                tags.addAll(packet.tags);
            }
        });
    }
}
