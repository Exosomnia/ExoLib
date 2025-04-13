package com.exosomnia.exolib.loot.conditions;

import com.exosomnia.exolib.ExoLib;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class MobCategoryCondition implements LootItemCondition {

    private LootContext.EntityTarget entityTarget;
    private MobCategory category;

    MobCategoryCondition(LootContext.EntityTarget entityTarget, MobCategory category) {
        this.entityTarget = entityTarget;
        this.category = category;
    }

    public LootItemConditionType getType() {
        return ExoLib.REGISTRY.MOB_CATEGORY_CONDITION.get();
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(entityTarget.getParam());
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.getParam(entityTarget.getParam());
        if (!(entity instanceof LivingEntity living)) { return false; }
        return living.getType().getCategory().equals(category);
    }

    public static class Builder implements LootItemCondition.Builder {

        private LootContext.EntityTarget entityTarget;
        private MobCategory category;

        public Builder setEntityTarget(LootContext.EntityTarget entityTarget) {
            this.entityTarget = entityTarget;
            return this;
        }

        public Builder setCategory(MobCategory category) {
            this.category = category;
            return this;
        }

        public LootItemCondition build() {
            return new MobCategoryCondition(entityTarget, category);
        }
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<MobCategoryCondition> {

        @Override
        public void serialize(JsonObject object, MobCategoryCondition condition, JsonSerializationContext context) {
            object.add("entity", context.serialize(condition.entityTarget));
            object.add("category", context.serialize(condition.category.getName()));
        }

        @Override
        public MobCategoryCondition deserialize(JsonObject object, JsonDeserializationContext context) {
            LootContext.EntityTarget entityTarget = GsonHelper.getAsObject(object, "entity", context, LootContext.EntityTarget.class);
            MobCategory category = MobCategory.valueOf(GsonHelper.getAsString(object, "category").toUpperCase());
            return new MobCategoryCondition(entityTarget, category);
        }
    }
}
