package com.exosomnia.exolib.loot.conditions;

import com.exosomnia.exolib.ExoLib;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class DimensionCondition implements LootItemCondition {

    @Nullable
    private ResourceKey<Level> dimension;
    @Nullable
    private ResourceKey<Biome> biome;

    DimensionCondition(ResourceKey<Level> dimension, ResourceKey<Biome> biome) {
        this.dimension = dimension;
        this.biome = biome;
    }

    public LootItemConditionType getType() {
        return ExoLib.REGISTRY.DIMENSION_CONDITION.get();
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.ORIGIN);
    }

    @Override
    public boolean test(LootContext lootContext) {
        ServerLevel level = lootContext.getLevel();
        Vec3 origin = lootContext.getParamOrNull(LootContextParams.ORIGIN);
        if (origin == null) { return false; }
        else if (dimension != null && !level.dimension().equals(dimension)) { return false; }
        else if (biome != null) {
            Holder<Biome> lootBiome = level.getBiome(BlockPos.containing(origin.x, origin.y, origin.z));
            if (!lootBiome.equals(level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(biome))) { return false; }
        }
        return true;
    }

    public static class Builder implements LootItemCondition.Builder {

        @Nullable
        private ResourceKey<Level> dimension;
        @Nullable
        private ResourceKey<Biome> biome;

        public DimensionCondition.Builder setDimension(ResourceKey<Level> dimension) {
            this.dimension = dimension;
            return this;
        }

        public DimensionCondition.Builder setBiome(ResourceKey<Biome> biome) {
            this.biome = biome;
            return this;
        }

        public LootItemCondition build() {
            return new DimensionCondition(dimension, biome);
        }
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<DimensionCondition> {

        @Override
        public void serialize(JsonObject object, DimensionCondition condition, JsonSerializationContext context) {
            object.add("dimension", context.serialize(condition.dimension.location().getNamespace()));
            object.add("biome", context.serialize(condition.biome.location().getNamespace()));
        }

        @Override
        public DimensionCondition deserialize(JsonObject object, JsonDeserializationContext context) {
            ResourceKey<Level> dimension = object.has("dimension") ? ResourceKey.create(Registries.DIMENSION,
                    ResourceLocation.of(GsonHelper.getAsString(object, "dimension"), ':')) : null;
            ResourceKey<Biome> biome = object.has("biome") ? ResourceKey.create(Registries.BIOME,
                    ResourceLocation.of(GsonHelper.getAsString(object, "biome"), ':')) : null;
            return new DimensionCondition(dimension, biome);
        }
    }
}
