package com.exosomnia.exolib.particles.shapes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;

public class ParticleShapeSphere extends ParticleShape {

    public final static Codec<ParticleShapeOptions.Sphere> OPTIONS_CODEC = RecordCodecBuilder.create(sphereInstance ->
        sphereInstance.group(
            Codec.FLOAT.fieldOf("radius").forGetter(options -> options.radius),
            Codec.INT.fieldOf("count").forGetter(options -> options.count))
            .apply(sphereInstance, ParticleShapeOptions.Sphere::new)
    );

    private final double GOLDEN_ANGLE = 2.39996322972865332; //pi * (3 - sqrt(5))

    public ParticleShapeSphere(ParticleOptions particle, Vec3 origin, ParticleShapeOptions.Sphere options) {
        super(particle, origin, options);
        this.shapeType = Shapes.SPHERE;
    }

    @Override
    public Codec<? extends ParticleShapeOptions> getCodec() {
        return OPTIONS_CODEC;
    }

    @Override
    public void playOnClient(ClientLevel level) {
        ParticleShapeOptions.Sphere options = (ParticleShapeOptions.Sphere)this.options;
        float radius = options.radius;
        int count = options.count;

        for (int i = 0; i < count; i++) {
            double theta = GOLDEN_ANGLE * i;
            double y = 1.0 - (i / (double)(count - 1)) * 2.0;
            double radiusAtY = Math.sqrt(1.0 - y * y);
            double x = Math.cos(theta) * radiusAtY;
            double z = Math.sin(theta) * radiusAtY;

            level.addParticle(particle,
                    origin.x + x * radius,
                    origin.y + y * radius,
                    origin.z + z * radius,
                    0, 0, 0);
        }
    }
}
