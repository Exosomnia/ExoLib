package com.exosomnia.exolib.loot.modifiers;

import com.exosomnia.exolib.mixin.interfaces.ILootParamsMixin;
import com.exosomnia.exolib.mixin.mixins.LootContextAccessor;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class GlobalLootModifier implements IGlobalLootModifier {

    public static final Supplier<MapCodec<GlobalLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(instance -> instance.point(new GlobalLootModifier())));
    public static HashMap<ResourceLocation, List<LootPool>> lootModifiers = new HashMap<>();

    private static final ImmutableSet<ResourceLocation> BLACKLIST_TABLES = ImmutableSet.of(ResourceLocation.fromNamespaceAndPath("botania", "elementium_axe_beheading"));

    public GlobalLootModifier() {}

    public static void setModifiers(HashMap<ResourceLocation, List<LootPool>> newLootModifiers) { lootModifiers = newLootModifiers; }

    @Override
    public @NotNull ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (BLACKLIST_TABLES.contains(context.getQueriedLootTableId())) { return generatedLoot; }

        ILootParamsMixin lootParams = ((ILootParamsMixin)((LootContextAccessor)context).getParams());
        if (!lootParams.shouldLootModify()) { return generatedLoot; }

        ResourceLocation lootCause = lootParams.getCause();
        List<LootPool> lootPools = lootModifiers.get(lootCause);
        if (lootPools == null || lootPools.isEmpty()) { return generatedLoot; }

        for (LootPool pool : lootPools) {
            pool.addRandomItems(generatedLoot::add, context);
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
