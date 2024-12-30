package com.exosomnia.exolib.particles.shapes;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class ParticleShapeSphere extends ParticleShape {

    final double GOLDEN_ANGLE = 2.39996322972865332; //pi * (3 - sqrt(5))

    public ParticleShapeSphere(ParticleOptions particle, BlockPos origin, ParticleShapeOptions.Sphere options) {
        super(particle, origin, options);
    }

    @Override
    public void playOnClient(ClientLevel level) {
        for (int i = 0; i < count; i++) {
            double theta = GOLDEN_ANGLE * i;
            double y = 1.0 - (i / (double)(count - 1)) * 2.0;
            double radiusAtY = Math.sqrt(1.0 - y * y);
            double x = Math.cos(theta) * radiusAtY;
            double z = Math.sin(theta) * radiusAtY;

            level.addParticle(particle,
                    origin.getX() + x * radius,
                    origin.getY() + y * radius,
                    origin.getZ() + z * radius,
                    0, 0, 0);
        }
    }

    @Override
    public void sendToPlayers(ServerLevel level, List<ServerPlayer> players) {

    }
}
