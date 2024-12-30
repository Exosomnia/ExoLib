package com.exosomnia.exolib.particles.utils;

import com.exosomnia.exolib.particles.shapes.ParticleShapeSphere;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;

public class ParticleUtils {

    public static <T extends ParticleOptions> void sendParticlesShaped(T particle, BlockPos origin, ServerLevel level, double radius, int count) {
        new ParticleShapeSphere(particle, origin, radius, count).sendToAll(level);
    }
}