package com.exosomnia.exolib.events;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.mixin.interfaces.ILivingEntityMixin;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;


@EventBusSubscriber(modid = ExoLib.MODID)
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
