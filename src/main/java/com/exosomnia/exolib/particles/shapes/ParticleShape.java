package com.exosomnia.exolib.particles.shapes;

import com.exosomnia.exolib.networking.PacketHandler;
import com.exosomnia.exolib.networking.packets.ParticleShapePacket;
import com.mojang.serialization.Codec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class ParticleShape {

    public final ParticleOptions particle;
    public final Vec3 origin;
    public final ParticleShapeOptions options;

    public Shapes shapeType;

    ParticleShape(ParticleOptions particle, Vec3 origin, ParticleShapeOptions options) {
        this.particle = particle;
        this.origin = origin;
        this.options = options;
    }

    public abstract Codec<? extends ParticleShapeOptions> getCodec();
    public abstract void playOnClient(ClientLevel level);

    public void sendToAll(ServerLevel level) {
        sendToPlayers(level, level.players());
    }
    public void sendToPlayers(ServerLevel level, List<ServerPlayer> players) {
        ParticleShapePacket packet = new ParticleShapePacket(this);
        for (ServerPlayer player : players) {
            if (player.level() == level) { PacketHandler.sendToPlayer(packet, player); }
        }
    }

    public enum Shapes {
        SPHERE,
        DOME,
        RING,
        LINE
    }
}
