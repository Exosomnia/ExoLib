package com.exosomnia.exolib.particles;

import com.exosomnia.exolib.particles.options.RGBSParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class SparkleParticle extends SimpleAnimatedParticle {
    protected SparkleParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, sprites, 0.010F);
        this.lifetime = 20;

        this.xd = xd + (Math.random() % 0.005) - 0.0025;
        this.yd = yd + (Math.random() % 0.005) - 0.0025;
        this.zd = zd + (Math.random() % 0.005) - 0.0025;

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSprite(sprites.get((this.age * 2) % this.lifetime, this.lifetime));
//        this.alpha -= .025;
        this.roll += 0.075F;
        this.oRoll += 0.075F;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<RGBSParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(RGBSParticleOptions options, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
            SparkleParticle sparkle = new SparkleParticle(level, x, y, z, vx, vy, vz, this.sprites);
            sparkle.setColor(options.red, options.green, options.blue);
            sparkle.quadSize = options.scale;
            return sparkle;
        }
    }
}
