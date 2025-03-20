package com.exosomnia.exolib.loot.modifiers;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.mixin.interfaces.ILootParamsMixin;
import com.exosomnia.exolib.mixin.mixins.LootContextAccessor;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class StructureLootModifier extends LootModifier {

    public static final Supplier<Codec<StructureLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(codec -> codecStart(codec).apply(codec, StructureLootModifier::new)));
    public static HashMap<ResourceLocation, HashMap<ResourceLocation, List<LootPool>>> lootModifiers = new HashMap<>();

    private static final ImmutableSet<ResourceLocation> BLACKLIST_TABLES = ImmutableSet.of(ResourceLocation.fromNamespaceAndPath("botania", "elementium_axe_beheading"));

    public StructureLootModifier(LootItemCondition[] conditions) {
        super(new LootItemCondition[0]);
    }

    public static void setModifiers(HashMap<ResourceLocation, HashMap<ResourceLocation, List<LootPool>>> newLootModifiers) { lootModifiers = newLootModifiers; }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (BLACKLIST_TABLES.contains(context.getQueriedLootTableId())) { return generatedLoot; }

        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
        if (origin == null) { return generatedLoot; }

        BlockPos blockPosOrigin = BlockPos.containing(origin);
        ServerLevel level = context.getLevel();

        ILootParamsMixin lootParams = ((ILootParamsMixin)((LootContextAccessor)context).getParams());
        if (!lootParams.shouldLootModify()) { return generatedLoot; }

        ResourceLocation lootCause = lootParams.getCause();
        List<StructureStart> potentialStructs = level.structureManager().startsForStructure(new ChunkPos(blockPosOrigin), structure -> true);
        for (StructureStart structure : potentialStructs) {
            if (!structure.getBoundingBox().isInside(blockPosOrigin)) { continue; }
            ResourceLocation structureResource = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(structure.getStructure());
            HashMap<ResourceLocation, List<LootPool>> lootPoolsByCause = lootModifiers.get(structureResource);
            if (lootPoolsByCause == null) { continue; }

            List<LootPool> lootPools = lootPoolsByCause.get(lootCause);
            if (lootPools == null || lootPools.isEmpty()) { continue; }

            for (LootPool pool : lootPools) {
                pool.addRandomItems(generatedLoot::add, context);
            }
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
