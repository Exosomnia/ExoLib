package com.exosomnia.exolib.particles.shapes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;

public class ParticleShapeRing extends ParticleShape {

    public final static Codec<ParticleShapeOptions.Ring> OPTIONS_CODEC = RecordCodecBuilder.create(sphereInstance ->
        sphereInstance.group(
            Codec.FLOAT.fieldOf("radius").forGetter(options -> options.radius),
            Codec.INT.fieldOf("count").forGetter(options -> options.count))
            .apply(sphereInstance, ParticleShapeOptions.Ring::new)
    );

    private final double TAU = 6.283185307179586; //2pi

    public ParticleShapeRing(ParticleOptions particle, Vec3 origin, ParticleShapeOptions.Ring options) {
        super(particle, origin, options);
        this.shapeType = Shapes.RING;
    }

    @Override
    public Codec<? extends ParticleShapeOptions> getCodec() {
        return OPTIONS_CODEC;
    }

    @Override
    public void playOnClient(ClientLevel level) {
        ParticleShapeOptions.Ring options = (ParticleShapeOptions.Ring)this.options;
        float radius = options.radius;
        int count = options.count;

        double theta = 0;
        double thetaStep = TAU/count;

        for (int i = 0; i < count; i++) {
            theta += thetaStep;
            double x = Math.cos(theta);
            double z = Math.sin(theta);

            level.addParticle(particle,
                    origin.x + x * radius,
                    origin.y + .5,
                    origin.z + z * radius,
                    0, 0, 0);
        }
    }
}
