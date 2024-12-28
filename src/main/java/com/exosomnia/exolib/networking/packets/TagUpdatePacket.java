package com.exosomnia.exolib.networking.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TagUpdatePacket {

    private String tag = "";
    private boolean present = false;

    public TagUpdatePacket(String tag, boolean present) {
        this.tag = tag;
        this.present = present;
    }

    public TagUpdatePacket(FriendlyByteBuf buffer) {
        tag = buffer.readUtf();
        present = buffer.readBoolean();
    }

    public static void encode(TagUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.tag);
        buffer.writeBoolean(packet.present);
    }

    public static void handle(TagUpdatePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            NetworkDirection packetDirection = context.get().getDirection();
            if (packetDirection.equals(NetworkDirection.PLAY_TO_CLIENT)) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (packet.present) { player.getTags().add(packet.tag); }
                else { player.getTags().remove(packet.tag); }
            }
        });
        context.get().setPacketHandled(true);
    }
}
