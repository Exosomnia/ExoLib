package com.exosomnia.exolib.events;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.mixin.interfaces.ILivingEntityMixin;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExoLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LivingEntityMixinEventsHandler {

    @SubscribeEvent
    public static void livingStopUsing(LivingEntityUseItemEvent.Stop event) {
        ((ILivingEntityMixin)event.getEntity()).setLastUsedItemStack(event.getItem());
    }

    @SubscribeEvent
    public static void livingFinishUsing(LivingEntityUseItemEvent.Finish event) {
        ((ILivingEntityMixin)event.getEntity()).setLastUsedItemStack(event.getItem());
    }
}
