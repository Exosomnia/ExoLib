package com.exosomnia.exolib.particles.shapes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ParticleShapeLine extends ParticleShape {

    public final static Codec<ParticleShapeOptions.Line> OPTIONS_CODEC = RecordCodecBuilder.create(sphereInstance ->
        sphereInstance.group(
            Codec.DOUBLE.listOf().xmap(
                    doubles -> new Vec3(doubles.get(0), doubles.get(1), doubles.get(2)),
                    options -> List.of(options.x, options.y, options.z)
            ).fieldOf("destination").forGetter(options -> options.destination),
            Codec.INT.fieldOf("count").forGetter(options -> options.count))
            .apply(sphereInstance, ParticleShapeOptions.Line::new)
    );

    public ParticleShapeLine(ParticleOptions particle, Vec3 origin, ParticleShapeOptions.Line options) {
        super(particle, origin, options);
        this.shapeType = Shapes.LINE;
    }

    @Override
    public Codec<? extends ParticleShapeOptions> getCodec() {
        return OPTIONS_CODEC;
    }

    @Override
    public void playOnClient(ClientLevel level) {
        ParticleShapeOptions.Line options = (ParticleShapeOptions.Line)this.options;
        double step = 1.0/options.count;
        int count = options.count;
        Vec3 stepAmount = options.destination.subtract(origin).multiply(step, step, step);

        for (int i = 0; i < count; i++) {
            double x = stepAmount.x * i;
            double y = stepAmount.y * i;
            double z = stepAmount.z * i;

            level.addParticle(particle,
                    origin.x + x,
                    origin.y + y,
                    origin.z + z,
                    0, 0, 0);
        }
    }
}
