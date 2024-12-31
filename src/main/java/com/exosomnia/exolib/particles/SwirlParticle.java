package com.exosomnia.exolib.particles;

import com.exosomnia.exolib.particles.options.RGBSParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class SwirlParticle extends TextureSheetParticle {
    protected SwirlParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z);
        this.lifetime = 50;

        this.xd = xd + (Math.random() % 0.01) - 0.005;
        this.yd = yd + (Math.random() % 0.01) - 0.005;
        this.zd = zd + (Math.random() % 0.01) - 0.005;
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha -= .02;
        this.roll += 0.15;
        this.oRoll += 0.15;
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
            SwirlParticle swirl = new SwirlParticle(level, x, y, z, vx, vy, vz);
            swirl.pickSprite(this.sprites);
            swirl.setColor(options.red, options.green, options.blue);
            swirl.quadSize = options.scale;
            return swirl;
        }
    }
}
