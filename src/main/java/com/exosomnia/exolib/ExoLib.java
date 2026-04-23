package com.exosomnia.exolib;

import com.exosomnia.exolib.config.ConfigSynchronizer;
import com.exosomnia.exolib.scheduler.ScheduleManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExoLib.MODID)
public class ExoLib
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "exolib";
    public static final Registry REGISTRY = new Registry();
    public static final ScheduleManager SERVER_SCHEDULE_MANAGER = new ScheduleManager();
    public static ConfigSynchronizer CONFIG_SYNCHRONIZER = new ConfigSynchronizer();

    public ExoLib(IEventBus modEventBus, ModContainer modContainer)
    {
        REGISTRY.registerObjects(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(CONFIG_SYNCHRONIZER);
    }

    @SubscribeEvent
    public void schedulerTick(ServerTickEvent.Pre event) {
        SERVER_SCHEDULE_MANAGER.tick();
    }
}
