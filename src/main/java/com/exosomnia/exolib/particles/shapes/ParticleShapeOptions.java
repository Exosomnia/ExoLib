package com.exosomnia.exolib.particles.shapes;

import net.minecraft.world.phys.Vec3;

public abstract class ParticleShapeOptions {

    public static class Sphere extends ParticleShapeOptions {
        public final float radius;
        public final int count;

        public Sphere(float radius, int count) {
            this.radius = radius;
            this.count = count;
        }
    }

    public static class Dome extends ParticleShapeOptions {
        public final float radius;
        public final int count;

        public Dome(float radius, int count) {
            this.radius = radius;
            this.count = count;
        }
    }

    public static class Ring extends ParticleShapeOptions {
        public final float radius;
        public final int count;

        public Ring(float radius, int count) {
            this.radius = radius;
            this.count = count;
        }
    }

    public static class Line extends ParticleShapeOptions {
        public final Vec3 destination;
        public final int count;

        public Line(Vec3 destination, int count) {
            this.destination = destination;
            this.count = count;
        }
    }
}
