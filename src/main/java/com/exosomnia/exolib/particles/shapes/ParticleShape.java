package com.exosomnia.exolib.particles.shapes;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class ParticleShape {

    protected final Level level;
    protected final Vec3 origin;
    protected final ParticleOptions options;

    public ParticleShape(ParticleOptions options, Level level, Vec3 origin) {
        this.level = level;
        this.origin = origin;
        this.options = options;
    }

    public abstract void generate();
}
