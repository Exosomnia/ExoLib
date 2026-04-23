package com.exosomnia.exolib.loot.conditions;

import com.exosomnia.exolib.ExoLib;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class DimensionCondition implements LootItemCondition {

    public static final Supplier<MapCodec<DimensionCondition>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(c -> c.dimension),
                    ResourceKey.codec(Registries.BIOME).fieldOf("biome").forGetter(c -> c.biome)
            ).apply(instance, DimensionCondition::new))
    );

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
}
