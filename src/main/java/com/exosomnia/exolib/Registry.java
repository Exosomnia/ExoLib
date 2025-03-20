package com.exosomnia.exolib;

import com.exosomnia.exolib.capabilities.persistentplayerdata.IPersistentPlayerDataStorage;
import com.exosomnia.exolib.commands.SyncedTag;
import com.exosomnia.exolib.loot.conditions.DimensionCondition;
import com.exosomnia.exolib.loot.conditions.MobCategoryCondition;
import com.exosomnia.exolib.loot.conditions.MobTypeCondition;
import com.exosomnia.exolib.loot.conditions.StructureCondition;
import com.exosomnia.exolib.loot.modifiers.BiomeLootModifier;
import com.exosomnia.exolib.loot.modifiers.GlobalLootModifier;
import com.exosomnia.exolib.loot.modifiers.StructureLootModifier;
import com.exosomnia.exolib.networking.PacketHandler;
import com.exosomnia.exolib.particles.options.RGBSParticleOptions;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.loot.IGlobalLootModifier;
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
    public final RegistryObject<LootItemConditionType> STRUCTURE_CONDITION = LOOT_ITEM_CONDITIONS.register("structure_condition", () -> new LootItemConditionType(new StructureCondition.Serializer()));
    public final RegistryObject<LootItemConditionType> MOB_CATEGORY_CONDITION = LOOT_ITEM_CONDITIONS.register("mob_category_condition", () -> new LootItemConditionType(new MobCategoryCondition.Serializer()));
    public final RegistryObject<LootItemConditionType> MOB_TYPE_CONDITION = LOOT_ITEM_CONDITIONS.register("mob_type_condition", () -> new LootItemConditionType(new MobTypeCondition.Serializer()));

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ExoLib.MODID);

    public static final RegistryObject<Codec<StructureLootModifier>> LOOT_MOD_STRUCTURE = GLOBAL_LOOT_MODS.register("structure_loot_mod", StructureLootModifier.CODEC);
    public static final RegistryObject<Codec<BiomeLootModifier>> LOOT_MOD_BIOME = GLOBAL_LOOT_MODS.register("biome_loot_mod", BiomeLootModifier.CODEC);
    public static final RegistryObject<Codec<GlobalLootModifier>> LOOT_MOD_GLOBAL = GLOBAL_LOOT_MODS.register("global_loot_mod", GlobalLootModifier.CODEC);

    public void registerCommon() {
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands); // Register ourselves for server and other game events we are interested in
        PacketHandler.register();        //Register our packets
    }

    public void registerObjects(IEventBus eventBus) {
        PARTICLES.register(eventBus);
        LOOT_ITEM_CONDITIONS.register(eventBus);
        GLOBAL_LOOT_MODS.register(eventBus);
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
