package com.exosomnia.exolib;

import com.exosomnia.exolib.scheduler.ScheduleManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExoLib.MODID)
public class ExoLib
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "exolib";
    public static final Registry REGISTRY = new Registry();
    public static final ScheduleManager SERVER_SCHEDULE_MANAGER = new ScheduleManager();

    public ExoLib()
    {
        REGISTRY.registerCommon();
        REGISTRY.registerObjects(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void schedulerTick(TickEvent.ServerTickEvent event) {
        if(event.phase.equals(TickEvent.Phase.END)) SERVER_SCHEDULE_MANAGER.tick();
    }
}
