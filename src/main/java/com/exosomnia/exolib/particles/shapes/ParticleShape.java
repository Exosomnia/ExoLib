package com.exosomnia.exolib.particles.shapes;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class ParticleShape {

    protected final ParticleOptions particle;
    protected final BlockPos origin;
    protected final ParticleShapeOptions options;

    public ParticleShape(ParticleOptions particle, BlockPos origin, ParticleShapeOptions options) {
        this.particle = particle;
        this.origin = origin;
        this.options = options;
    }

    public abstract void playOnClient(ClientLevel level);

    public void sendToAll(ServerLevel level) {
        sendToPlayers(level, level.players());
    }
    public abstract void sendToPlayers(ServerLevel level, List<ServerPlayer> players);
}
