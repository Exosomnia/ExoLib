package com.exosomnia.exolib.loot.conditions;

import com.exosomnia.exolib.ExoLib;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public class EntityTypeTagCondition implements LootItemCondition {

    public static final Codec<TagKey<EntityType<?>>> ENTITY_TYPE_CODEC = Codec.STRING.xmap(
            string -> TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.withDefaultNamespace(string.toLowerCase())),
            entityTypeTag -> entityTypeTag.location().getPath().toLowerCase()
    );

    public static final Supplier<MapCodec<EntityTypeTagCondition>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter(c -> c.entityTarget),
                    ENTITY_TYPE_CODEC.fieldOf("type").forGetter(c -> c.type)
            ).apply(instance, EntityTypeTagCondition::new))
    );

    private LootContext.EntityTarget entityTarget;
    private TagKey<EntityType<?>> type;

    EntityTypeTagCondition(LootContext.EntityTarget entityTarget, TagKey<EntityType<?>> type) {
        this.entityTarget = entityTarget;
        this.type = type;
    }

    public LootItemConditionType getType() {
        return ExoLib.REGISTRY.MOB_TYPE_CONDITION.get();
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(this.entityTarget.getParam());
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.getParam(entityTarget.getParam());
        if (!(entity instanceof LivingEntity living)) { return false; }
        return living.getType().is(type);
    }

    public static class Builder implements LootItemCondition.Builder {

        private LootContext.EntityTarget entityTarget;
        private TagKey<EntityType<?>> type;

        public Builder setEntityTarget(LootContext.EntityTarget entityTarget) {
            this.entityTarget = entityTarget;
            return this;
        }

        public Builder setType(TagKey<EntityType<?>> type) {
            this.type = type;
            return this;
        }

        public LootItemCondition build() {
            return new EntityTypeTagCondition(entityTarget, type);
        }
    }
}
