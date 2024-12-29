package com.exosomnia.exolib.particles.utils;

import com.exosomnia.exolib.particles.shapes.ParticleShapeSphere;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleUtils {

    public static <T extends ParticleOptions> void sendParticlesShaped(T particle, Level level, Vec3 origin, double radius, int count) {
        new ParticleShapeSphere(particle, level, origin, radius, count).generate();
    }
}