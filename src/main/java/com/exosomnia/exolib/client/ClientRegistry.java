package com.exosomnia.exolib.client;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.particles.AreaParticle;
import com.exosomnia.exolib.particles.GridParticle;
import com.exosomnia.exolib.particles.SpiralParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExoLib.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistry {

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ExoLib.AREA_PARTICLE.get(), AreaParticle.Provider::new);
        event.registerSpriteSet(ExoLib.GRID_PARTICLE.get(), GridParticle.Provider::new);
        event.registerSpriteSet(ExoLib.SPIRAL_PARTICLE.get(), SpiralParticle.Provider::new);
    }
}
