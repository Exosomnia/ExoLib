package com.exosomnia.exolib.particles;

import com.exosomnia.exolib.particles.options.RGBSParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class AreaParticle extends TextureSheetParticle {
    protected AreaParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z);
        this.lifetime = 30;

        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha -= .025F;
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
            AreaParticle area = new AreaParticle(level, x, y, z, vx, vy, vz);
            area.pickSprite(this.sprite);
            area.setColor(options.red, options.green, options.blue);
            area.quadSize = options.scale;
            return area;
        }
    }
}
