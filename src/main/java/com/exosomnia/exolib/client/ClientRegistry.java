package com.exosomnia.exolib.client;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.particles.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExoLib.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistry {

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ExoLib.REGISTRY.AREA_PARTICLE.get(), AreaParticle.Provider::new);
        event.registerSpriteSet(ExoLib.REGISTRY.GRID_PARTICLE.get(), GridParticle.Provider::new);
        event.registerSpriteSet(ExoLib.REGISTRY.SPIRAL_PARTICLE.get(), SpiralParticle.Provider::new);
        event.registerSpriteSet(ExoLib.REGISTRY.SPARKLE_PARTICLE.get(), SparkleParticle.Provider::new);
        event.registerSpriteSet(ExoLib.REGISTRY.SWIRL_PARTICLE.get(), SwirlParticle.Provider::new);
        event.registerSpriteSet(ExoLib.REGISTRY.TWINKLE_PARTICLE.get(), TwinkleParticle.Provider::new);
    }
}
