package com.exosomnia.exolib;

import com.exosomnia.exolib.commands.SyncedTag;
import com.exosomnia.exolib.networking.PacketHandler;
import com.exosomnia.exolib.particles.AreaParticleOptions;
import com.exosomnia.exolib.particles.GridParticleOptions;
import com.exosomnia.exolib.particles.SpiralParticleOptions;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExoLib.MODID)
public class ExoLib
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "exolib";

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
    public static final RegistryObject<ParticleType<AreaParticleOptions>> AREA_PARTICLE = PARTICLES.register("area",
            () -> new ParticleType<>(false, AreaParticleOptions.DUMMY_DESERIALIZER) {
                @Override
                public Codec<AreaParticleOptions> codec() {
                    return AreaParticleOptions.CODEC;
                }
            });
    public static final RegistryObject<ParticleType<GridParticleOptions>> GRID_PARTICLE = PARTICLES.register("grid",
            () -> new ParticleType<>(false, GridParticleOptions.DUMMY_DESERIALIZER) {
                @Override
                public Codec<GridParticleOptions> codec() {
                    return GridParticleOptions.CODEC;
                }
            });
    public static final RegistryObject<ParticleType<SpiralParticleOptions>> SPIRAL_PARTICLE = PARTICLES.register("spiral",
            () -> new ParticleType<>(false, SpiralParticleOptions.DUMMY_DESERIALIZER) {
                @Override
                public Codec<SpiralParticleOptions> codec() {
                    return SpiralParticleOptions.CODEC;
                }
            });

    public ExoLib()
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        //Register our packets
        PacketHandler.register();

        PARTICLES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        SyncedTag.register(event.getDispatcher());
    }
}
