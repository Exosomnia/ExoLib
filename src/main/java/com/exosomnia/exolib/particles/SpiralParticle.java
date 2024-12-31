package com.exosomnia.exolib.particles;

import com.exosomnia.exolib.particles.options.RGBSParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class SpiralParticle extends TextureSheetParticle {
    protected SpiralParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z);
        this.lifetime = 40;

        this.xd = xd + (Math.random() % 0.01) - 0.005;
        this.yd = yd + (Math.random() % 0.01) - 0.005;
        this.zd = zd + (Math.random() % 0.01) - 0.005;
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha -= .025F;
        this.roll += 0.075F;
        this.oRoll += 0.075F;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<RGBSParticleOptions> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(RGBSParticleOptions options, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
            SpiralParticle spiral = new SpiralParticle(level, x, y, z, vx, vy, vz);
            spiral.pickSprite(this.sprite);
            spiral.setColor(options.red, options.green, options.blue);
            spiral.quadSize = options.scale;
            return spiral;
        }
    }
}
