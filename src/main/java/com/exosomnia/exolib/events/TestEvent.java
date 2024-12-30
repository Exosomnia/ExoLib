package com.exosomnia.exolib.events;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.particles.SpiralParticleOptions;
import com.exosomnia.exolib.particles.utils.ParticleUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExoLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TestEvent {

    @SubscribeEvent
    public static void killed(LivingDeathEvent event) {
        Entity killer = event.getSource().getEntity();
        if (killer != null && !killer.level().isClientSide) {
        }
    }
}
