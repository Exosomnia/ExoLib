package com.exosomnia.exolib.loot.conditions;

import com.exosomnia.exolib.ExoLib;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
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

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<StructureCondition> {

        @Override
        public void serialize(JsonObject object, StructureCondition condition, JsonSerializationContext context) {
            object.add("structure", context.serialize(condition.structure.location().getNamespace()));
        }

        @Override
        public StructureCondition deserialize(JsonObject object, JsonDeserializationContext context) {
            ResourceKey<Structure> structure = object.has("structure") ? ResourceKey.create(Registries.STRUCTURE,
                    ResourceLocation.bySeparator(GsonHelper.getAsString(object, "structure"), ':')) : null;
            return new StructureCondition(structure);
        }
    }
}
