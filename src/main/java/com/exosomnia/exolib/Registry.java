package com.exosomnia.exolib;

import com.exosomnia.exolib.capabilities.persistentplayerdata.IPersistentPlayerDataStorage;
import com.exosomnia.exolib.commands.SyncedTag;
import com.exosomnia.exolib.loot.conditions.DimensionCondition;
import com.exosomnia.exolib.networking.PacketHandler;
import com.exosomnia.exolib.particles.options.RGBSParticleOptions;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class Registry {

    private final Supplier<ParticleType<RGBSParticleOptions>> rgbsSupplier = () ->
            new ParticleType<>(false, RGBSParticleOptions.DUMMY_DESERIALIZER) {
                @Override
                public Codec<RGBSParticleOptions> codec() {
                    return RGBSParticleOptions.CODEC;
                }
            };
    public final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ExoLib.MODID);
    public final RegistryObject<ParticleType<RGBSParticleOptions>> AREA_PARTICLE = PARTICLES.register("area", rgbsSupplier);
    public final RegistryObject<ParticleType<RGBSParticleOptions>> GRID_PARTICLE = PARTICLES.register("grid", rgbsSupplier);
    public final RegistryObject<ParticleType<RGBSParticleOptions>> SPIRAL_PARTICLE = PARTICLES.register("spiral", rgbsSupplier);
    public final RegistryObject<ParticleType<RGBSParticleOptions>> SPARKLE_PARTICLE = PARTICLES.register("sparkle", rgbsSupplier);
    public final RegistryObject<ParticleType<RGBSParticleOptions>> SWIRL_PARTICLE = PARTICLES.register("swirl", rgbsSupplier);
    public final RegistryObject<ParticleType<RGBSParticleOptions>> TWINKLE_PARTICLE = PARTICLES.register("twinkle", rgbsSupplier);

    public final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, ExoLib.MODID);
    public final RegistryObject<LootItemConditionType> DIMENSION_CONDITION = LOOT_ITEM_CONDITIONS.register("dimension_condition", () -> new LootItemConditionType(new DimensionCondition.Serializer()));

    public void registerCommon() {
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands); // Register ourselves for server and other game events we are interested in
        PacketHandler.register();        //Register our packets
    }

    public void registerObjects(IEventBus eventBus) {
        PARTICLES.register(eventBus);
        LOOT_ITEM_CONDITIONS.register(eventBus);
        eventBus.addListener(this::registerCapabilities);
    }

    public void registerCommands(RegisterCommandsEvent event)
    {
        SyncedTag.register(event.getDispatcher());
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IPersistentPlayerDataStorage.class);
    }
}
