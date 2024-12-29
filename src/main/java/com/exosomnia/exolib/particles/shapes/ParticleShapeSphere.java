package com.exosomnia.exolib.particles.shapes;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleShapeSphere extends ParticleShape {

    final double GOLDEN_ANGLE = 2.39996322972865332; //pi * (3 - sqrt(5))

    private double radius;
    private int count;

    public ParticleShapeSphere(ParticleOptions options, Level level, Vec3 origin, double radius, int count) {
        super(options, level, origin);
        this.radius = radius;
        this.count = count;
    }

    @Override
    public void generate() {
        for (int i = 0; i < count; i++) {
            double theta = GOLDEN_ANGLE * i;
            double y = 1.0 - (i / (double)(count - 1)) * 2.0;
            double radiusAtY = Math.sqrt(1.0 - y * y);
            double x = Math.cos(theta) * radiusAtY;
            double z = Math.sin(theta) * radiusAtY;

            if (level instanceof ServerLevel) {
                ((ServerLevel) level).sendParticles(options,
                        origin.x + x * radius,
                        origin.y + y * radius,
                        origin.z + z * radius,
                        1, 0, 0, 0, 0);
            }
            else {
                level.addParticle(options,
                        origin.x + x * radius,
                        origin.y + y * radius,
                        origin.z + z * radius,
                        0, 0, 0);
            }
        }
    }
}
