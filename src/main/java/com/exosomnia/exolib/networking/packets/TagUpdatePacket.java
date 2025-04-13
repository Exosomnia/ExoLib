package com.exosomnia.exolib.networking.packets;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class TagUpdatePacket {

    private Set<String> tags;

    public TagUpdatePacket(Set<String> tags) {
        this.tags = tags;
    }

    public TagUpdatePacket(FriendlyByteBuf buffer) {
        int tagCount = buffer.readInt();
        ImmutableSet.Builder<String> builder = new ImmutableSet.Builder<>();
        for (int i = 0; i < tagCount; i++) {
            builder.add(buffer.readUtf());
        }
        tags = builder.build();
    }

    public static void encode(TagUpdatePacket packet, FriendlyByteBuf buffer) {
        int tagCount = packet.tags.size();
        buffer.writeInt(tagCount);
        packet.tags.forEach(buffer::writeUtf);
    }

    public static void handle(TagUpdatePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            NetworkDirection packetDirection = context.get().getDirection();
            if (packetDirection.equals(NetworkDirection.PLAY_TO_CLIENT)) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) return;

                Set<String> tags = player.getTags();
                tags.clear();
                tags.addAll(packet.tags);
            }
        });
        context.get().setPacketHandled(true);
    }
}
