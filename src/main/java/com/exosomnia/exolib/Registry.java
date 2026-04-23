package com.exosomnia.exolib;

import com.exosomnia.exolib.capabilities.persistentplayerdata.IPersistentPlayerDataStorage;
import com.exosomnia.exolib.capabilities.persistentplayerdata.PersistentPlayerDataStorage;
import com.exosomnia.exolib.commands.SyncedTag;
import com.exosomnia.exolib.loot.conditions.DimensionCondition;
import com.exosomnia.exolib.loot.conditions.MobCategoryCondition;
import com.exosomnia.exolib.loot.conditions.EntityTypeTagCondition;
import com.exosomnia.exolib.loot.conditions.StructureCondition;
import com.exosomnia.exolib.loot.modifiers.BiomeLootModifier;
import com.exosomnia.exolib.loot.modifiers.GlobalLootModifier;
import com.exosomnia.exolib.loot.modifiers.StructureLootModifier;
import com.exosomnia.exolib.networking.PacketHandler;
import com.exosomnia.exolib.particles.options.RGBSParticleOptions;
import com.exosomnia.exolib.recipes.brewing.SimpleBrewingRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Registry {

    public final List<SimpleBrewingRecipe> SIMPLE_BREWING_RECIPES = new ArrayList<>();
    public void addSimpleBrewingRecipe(Potion potion, Item ingredient, Potion output) {
        SIMPLE_BREWING_RECIPES.add(new SimpleBrewingRecipe(potion, ingredient, output));
    }

    private final Supplier<ParticleType<RGBSParticleOptions>> rgbsSupplier = () ->
            new ParticleType<>(false) {
                @Override
                public MapCodec<RGBSParticleOptions> codec() {
                    return RGBSParticleOptions.CODEC;
                }
                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, RGBSParticleOptions> streamCodec() { return RGBSParticleOptions.STREAM_CODEC; }
            };
    public final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ExoLib.MODID);
    public final DeferredHolder<ParticleType<?>, ParticleType<RGBSParticleOptions>> AREA_PARTICLE = PARTICLES.register("area", rgbsSupplier);
    public final DeferredHolder<ParticleType<?>, ParticleType<RGBSParticleOptions>> GRID_PARTICLE = PARTICLES.register("grid", rgbsSupplier);
    public final DeferredHolder<ParticleType<?>, ParticleType<RGBSParticleOptions>> SPIRAL_PARTICLE = PARTICLES.register("spiral", rgbsSupplier);
    public final DeferredHolder<ParticleType<?>, ParticleType<RGBSParticleOptions>> SPARKLE_PARTICLE = PARTICLES.register("sparkle", rgbsSupplier);
    public final DeferredHolder<ParticleType<?>, ParticleType<RGBSParticleOptions>> SWIRL_PARTICLE = PARTICLES.register("swirl", rgbsSupplier);
    public final DeferredHolder<ParticleType<?>, ParticleType<RGBSParticleOptions>> TWINKLE_PARTICLE = PARTICLES.register("twinkle", rgbsSupplier);

    public final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, ExoLib.MODID);
    public final DeferredHolder<LootItemConditionType, LootItemConditionType> DIMENSION_CONDITION = LOOT_ITEM_CONDITIONS.register("dimension_condition", () -> new LootItemConditionType(DimensionCondition.CODEC.get()));
    public final DeferredHolder<LootItemConditionType, LootItemConditionType> STRUCTURE_CONDITION = LOOT_ITEM_CONDITIONS.register("structure_condition", () -> new LootItemConditionType(StructureCondition.CODEC.get()));
    public final DeferredHolder<LootItemConditionType, LootItemConditionType> MOB_CATEGORY_CONDITION = LOOT_ITEM_CONDITIONS.register("mob_category_condition", () -> new LootItemConditionType(MobCategoryCondition.CODEC.get()));
    public final DeferredHolder<LootItemConditionType, LootItemConditionType> MOB_TYPE_CONDITION = LOOT_ITEM_CONDITIONS.register("entity_type_condition", () -> new LootItemConditionType(EntityTypeTagCondition.CODEC.get()));

    public final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODS = DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ExoLib.MODID);

    public final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<StructureLootModifier>> LOOT_MOD_STRUCTURE = GLOBAL_LOOT_MODS.register("structure_loot_mod", StructureLootModifier.CODEC);
    public final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<BiomeLootModifier>> LOOT_MOD_BIOME = GLOBAL_LOOT_MODS.register("biome_loot_mod", BiomeLootModifier.CODEC);
    public final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<GlobalLootModifier>> LOOT_MOD_GLOBAL = GLOBAL_LOOT_MODS.register("global_loot_mod", GlobalLootModifier.CODEC);

    public final EntityCapability<IPersistentPlayerDataStorage, Void> PERSISTENT_PLAYER_DATA = EntityCapability.createVoid(
            ResourceLocation.fromNamespaceAndPath(ExoLib.MODID, "persistent_player_data"),
            IPersistentPlayerDataStorage.class);

    public void registerObjects(IEventBus eventBus) {
        PARTICLES.register(eventBus);
        LOOT_ITEM_CONDITIONS.register(eventBus);
        GLOBAL_LOOT_MODS.register(eventBus);

        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        NeoForge.EVENT_BUS.addListener(this::registerBrewingRecipes);
        eventBus.addListener(this::registerCapabilities);
        eventBus.addListener(this::registerPackets);
    }

    public void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(ExoLib.MODID);
        PacketHandler.register(registrar);
    }

    public void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        for (SimpleBrewingRecipe brewingRecipe : SIMPLE_BREWING_RECIPES) {
            event.getBuilder().addRecipe(brewingRecipe);
        }
    }

    public void registerCommands(RegisterCommandsEvent event) {
        SyncedTag.register(event.getDispatcher());
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(PERSISTENT_PLAYER_DATA, EntityType.PLAYER, (player, context) -> new PersistentPlayerDataStorage(new CompoundTag()));
    }
}
