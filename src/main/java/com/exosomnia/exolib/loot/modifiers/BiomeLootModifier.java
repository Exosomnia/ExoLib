package com.exosomnia.exolib.loot.modifiers;

import com.exosomnia.exolib.mixin.interfaces.ILootParamsMixin;
import com.exosomnia.exolib.mixin.mixins.LootContextAccessor;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class BiomeLootModifier implements IGlobalLootModifier {

    public static final Supplier<MapCodec<BiomeLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(instance -> instance.point(new BiomeLootModifier())));
    public static HashMap<ResourceLocation, HashMap<ResourceLocation, List<LootPool>>> lootModifiers = new HashMap<>();

    private static final ImmutableSet<ResourceLocation> BLACKLIST_TABLES = ImmutableSet.of(ResourceLocation.fromNamespaceAndPath("botania", "elementium_axe_beheading"));

    public BiomeLootModifier() {}

    public static void setModifiers(HashMap<ResourceLocation, HashMap<ResourceLocation, List<LootPool>>> newLootModifiers) { lootModifiers = newLootModifiers; }

    @Override
    public @NotNull ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (BLACKLIST_TABLES.contains(context.getQueriedLootTableId())) { return generatedLoot; }

        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
        if (origin == null) { return generatedLoot; }

        BlockPos blockPosOrigin = BlockPos.containing(origin);
        ServerLevel level = context.getLevel();

        ILootParamsMixin lootParams = ((ILootParamsMixin)((LootContextAccessor)context).getParams());
        if (!lootParams.shouldLootModify()) { return generatedLoot; }

        ResourceLocation lootCause = lootParams.getCause();
        Holder<Biome> lootBiome = level.getBiome(blockPosOrigin);

        ResourceLocation biomeResource = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(lootBiome.value());
        HashMap<ResourceLocation, List<LootPool>> lootPoolsByCause = lootModifiers.get(biomeResource);
        if (lootPoolsByCause == null) { return generatedLoot; }

        List<LootPool> lootPools = lootPoolsByCause.get(lootCause);
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
