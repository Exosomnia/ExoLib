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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class StructureCondition implements LootItemCondition {

    public static final Supplier<MapCodec<StructureCondition>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ResourceKey.codec(Registries.STRUCTURE).fieldOf("structure").forGetter(c -> c.structure)
            ).apply(instance, StructureCondition::new))
    );

    private ResourceKey<Structure> structure;

    StructureCondition(ResourceKey<Structure> structure) {
        this.structure = structure;
    }

    public LootItemConditionType getType() {
        return ExoLib.REGISTRY.STRUCTURE_CONDITION.get();
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.ORIGIN);
    }

    @Override
    public boolean test(LootContext lootContext) {
        Vec3 origin = lootContext.getParamOrNull(LootContextParams.ORIGIN);
        if (origin == null) { return false; }

        ServerLevel level = lootContext.getLevel();
        Structure lootStructure = level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(structure);
        if (lootStructure == null || level.structureManager().getStructureAt(BlockPos.containing(origin.x, origin.y, origin.z), lootStructure) == StructureStart.INVALID_START) { return false; }

        return true;
    }

    public static class Builder implements LootItemCondition.Builder {

        private ResourceKey<Structure> structure;

        public Builder setStructure(ResourceKey<Structure> structure) {
            this.structure = structure;
            return this;
        }

        public LootItemCondition build() {
            return new StructureCondition(structure);
        }
    }
}
