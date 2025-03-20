package com.exosomnia.exolib.loot.conditions;

import com.exosomnia.exolib.ExoLib;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public class MobTypeCondition implements LootItemCondition {

    private static final ImmutableMap<String, MobType> STRING_TO_MOBTYPE_MAPPING = ImmutableMap.of(
            "UNDEFINED", MobType.UNDEFINED,
            "UNDEAD", MobType.UNDEAD,
            "ARTHROPOD", MobType.ARTHROPOD,
            "ILLAGER", MobType.ILLAGER,
            "WATER", MobType.WATER
    );

    private static final ImmutableMap<MobType, String> MOBTYPE_TO_STRING_MAPPING = ImmutableMap.of(
            MobType.UNDEFINED, "UNDEFINED",
            MobType.UNDEAD, "UNDEAD",
            MobType.ARTHROPOD, "ARTHRO,POD",
            MobType.ILLAGER, "ILLAGER",
            MobType.WATER, "WATER"
    );

    private LootContext.EntityTarget entityTarget;
    private MobType type;

    MobTypeCondition(LootContext.EntityTarget entityTarget, MobType type) {
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
        return living.getMobType().equals(type);
    }

    public static class Builder implements LootItemCondition.Builder {

        private LootContext.EntityTarget entityTarget;
        private MobType type;

        public Builder setEntityTarget(LootContext.EntityTarget entityTarget) {
            this.entityTarget = entityTarget;
            return this;
        }

        public Builder setType(MobType type) {
            this.type = type;
            return this;
        }

        public LootItemCondition build() {
            return new MobTypeCondition(entityTarget, type);
        }
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<MobTypeCondition> {

        @Override
        public void serialize(JsonObject object, MobTypeCondition condition, JsonSerializationContext context) {
            object.add("entity", context.serialize(condition.entityTarget));
            object.add("type", context.serialize(MOBTYPE_TO_STRING_MAPPING.get(condition.type)));
        }

        @Override
        public MobTypeCondition deserialize(JsonObject object, JsonDeserializationContext context) {
            LootContext.EntityTarget entityTarget = GsonHelper.getAsObject(object, "entity", context, LootContext.EntityTarget.class);
            MobType type = STRING_TO_MOBTYPE_MAPPING.get(GsonHelper.getAsString(object, "type").toUpperCase());
            return new MobTypeCondition(entityTarget, type);
        }
    }
}
