package com.exosomnia.exolib.loot;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.loot.modifiers.BiomeLootModifier;
import com.exosomnia.exolib.loot.modifiers.GlobalLootModifier;
import com.exosomnia.exolib.loot.modifiers.StructureLootModifier;
import com.exosomnia.exolib.mixin.mixins.LootPoolAccessor;
import com.exosomnia.exolib.mixin.mixins.LootPoolSingletonContainerAccessor;
import com.exosomnia.exolib.mixin.mixins.LootTableAccessor;
import com.google.common.collect.HashMultimap;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ExoLib.MODID)
public class LootTableInjectHandler {

    private static final HashMultimap<ResourceLocation, List<LootPool>> lootTableInjections = HashMultimap.create();
    private static final HashMultimap<ResourceLocation, TablePoolInjection> lootPoolInjections = HashMultimap.create();
    private static final HashMap<ResourceLocation, HashMap<ResourceLocation, List<LootPool>>> lootPoolStructureInjections = new HashMap<>();
    private static final HashMap<ResourceLocation, HashMap<ResourceLocation, List<LootPool>>> lootPoolBiomeInjections = new HashMap<>();
    private static final HashMap<ResourceLocation, List<LootPool>> lootPoolGlobalInjections = new HashMap<>();

    private static final List<DelayedLootInjection<Structure>> delayedPoolStructureInjections = new ArrayList<>();
    private static final List<DelayedLootInjection<Biome>> delayedPoolBiomeInjections = new ArrayList<>();

    private record DelayedLootInjection<T>(TagKey<T> tagKey, ResourceLocation cause,
                                        List<LootPool> injectPools) {}

    private record TablePoolInjection(String name, double weightAdjustment, List<LootPoolEntryContainer> entries) {}

    //TODO: Get rid of all these if/elses move to switch based enum or HashMap > Strategy lookup.
    @SubscribeEvent
    public static void addLootTableInjectsData(AddReloadListenerEvent event) {
        event.addListener((barrier, resourceManager, prepProfiler, reloadProfiler, prepExecutor, reloadExecutor) -> {
            lootTableInjections.clear();
            lootPoolInjections.clear();
            lootPoolStructureInjections.clear();
            lootPoolBiomeInjections.clear();
            lootPoolGlobalInjections.clear();
            return CompletableFuture.runAsync(() -> {
                Map<ResourceLocation, Resource> resources = resourceManager.listResources("loot_table_injections", (resourceLocation) -> true);

                for (ResourceLocation location : resources.keySet()) {
                    try {
                        InputStreamReader reader = new InputStreamReader(resources.get(location).open());
                        JsonObject head = JsonParser.parseReader(reader).getAsJsonObject();
                        if (head.has("injects")) {
                            JsonArray injectsArray = head.getAsJsonArray("injects");
                            Optional<LootTable> loadedTable = LootDataType.TABLE.deserialize(location, JsonOps.INSTANCE, head);
                            if (loadedTable.isEmpty() || injectsArray.isEmpty()) {
                                continue;
                            }

                            List<LootPool> injectPools = ((LootTableAccessor) loadedTable.get()).getPools();

                            for (JsonElement locationElement : injectsArray) {
                                injectToLootTable(event.getRegistryAccess().registryOrThrow(Registries.LOOT_TABLE), ResourceLocation.bySeparator(locationElement.getAsString(), ':'), injectPools);
                                //lootTableInjections.put(ResourceLocation.bySeparator(locationElement.getAsString(), ':'), injectPools);
                            }
                        }
                        else if (head.has("structure_injects")) {
                            JsonObject structureInjectData = head.getAsJsonObject("structure_injects");
                            JsonArray structuresArray = structureInjectData.getAsJsonArray("structures");
                            ResourceLocation cause = ResourceLocation.bySeparator(structureInjectData.get("cause").getAsString(), ':');
                            Optional<LootTable> loadedTable = LootDataType.TABLE.deserialize(location, JsonOps.INSTANCE, head);
                            if (loadedTable.isEmpty() || structuresArray.isEmpty()) {
                                continue;
                            }

                            List<LootPool> injectPools = ((LootTableAccessor) loadedTable.get()).getPools();

                            for (JsonElement structureElement : structuresArray) {
                                String structureString = structureElement.getAsString();
                                if (structureString.startsWith("#")) {
                                    structureString = structureString.substring(1);
                                    TagKey<Structure> structureTag = TagKey.create(Registries.STRUCTURE, ResourceLocation.bySeparator(structureString, ':'));
                                    delayedPoolStructureInjections.add(new DelayedLootInjection<>(structureTag, cause, injectPools));
                                } else {
                                    ResourceLocation structureLocation = ResourceLocation.bySeparator(structureElement.getAsString(), ':');
                                    addInjectDataToMap(lootPoolStructureInjections, structureLocation, cause, injectPools);
                                }
                            }
                        }
                        else if (head.has("biome_injects")) {
                            JsonObject biomeInjectData = head.getAsJsonObject("biome_injects");
                            JsonArray biomeArray = biomeInjectData.getAsJsonArray("biomes");
                            ResourceLocation cause = ResourceLocation.bySeparator(biomeInjectData.get("cause").getAsString(), ':');
                            Optional<LootTable> loadedTable = LootDataType.TABLE.deserialize(location, JsonOps.INSTANCE, head);
                            if (loadedTable.isEmpty() || biomeArray.isEmpty()) {
                                continue;
                            }

                            List<LootPool> injectPools = ((LootTableAccessor) loadedTable.get()).getPools();

                            for (JsonElement biomeElement : biomeArray) {
                                String biomeString = biomeElement.getAsString();
                                if (biomeString.startsWith("#")) {
                                    biomeString = biomeString.substring(1);
                                    TagKey<Biome> biomeTag = TagKey.create(Registries.BIOME, ResourceLocation.bySeparator(biomeString, ':'));
                                    delayedPoolBiomeInjections.add(new DelayedLootInjection<>(biomeTag, cause, injectPools));
                                } else {
                                    ResourceLocation biomeLocation = ResourceLocation.bySeparator(biomeElement.getAsString(), ':');
                                    addInjectDataToMap(lootPoolBiomeInjections, biomeLocation, cause, injectPools);
                                }
                            }
                        }
                        else if (head.has("global_injects")) {
                            JsonObject globalInjectData = head.getAsJsonObject("global_injects");
                            ResourceLocation cause = ResourceLocation.bySeparator(globalInjectData.get("cause").getAsString(), ':');
                            Optional<LootTable> loadedTable = LootDataType.TABLE.deserialize(location, JsonOps.INSTANCE, head);
                            if (loadedTable.isEmpty()) {
                                continue;
                            }

                            List<LootPool> injectPools = ((LootTableAccessor) loadedTable.get()).getPools();
                            List<LootPool> currentPools = lootPoolGlobalInjections.getOrDefault(cause, new ArrayList<>());
                            currentPools.addAll(injectPools);
                            lootPoolGlobalInjections.put(cause, currentPools);
                        }
                        else if (head.has("pool_injects")) {
                            JsonArray poolInjectsArray = head.getAsJsonArray("pool_injects");
                            Optional<LootTable> loadedTable = LootDataType.TABLE.deserialize(location, JsonOps.INSTANCE, head);
                            if (loadedTable.isEmpty() || poolInjectsArray.isEmpty()) {
                                continue;
                            }

                            List<LootPool> injectPools = ((LootTableAccessor) loadedTable.get()).getPools();
                            List<LootPoolEntryContainer> injectEntries = new ArrayList<>();
                            for (LootPool pool : injectPools) {
                                injectEntries.addAll(((LootPoolAccessor)pool).getEntries());
                            }
                            if (injectEntries.isEmpty()) { continue; }

                            for (JsonElement locationElement : poolInjectsArray) {
                                JsonObject poolInjectData = locationElement.getAsJsonObject();
                                JsonElement tableLocation = poolInjectData.get("table");
                                String poolName = poolInjectData.has("pool") ? poolInjectData.get("pool").getAsString() : "main";
                                double weightAdjustment = poolInjectData.has("weight_adjustment") ? poolInjectData.get("weight_adjustment").getAsDouble() : 1.0;
                                injectToLootPool(event.getRegistryAccess().registryOrThrow(Registries.LOOT_TABLE), ResourceLocation.bySeparator(tableLocation.getAsString(), ':'), new TablePoolInjection(poolName, weightAdjustment, injectEntries));
                            }


                        }
                    } catch (Exception e) {
                        LogUtils.getLogger().info(e.toString());
                        LogUtils.getLogger().info("-------------------------------------------");
                        for (StackTraceElement element : e.getStackTrace()) {
                            LogUtils.getLogger().info(element.toString());
                        }
                    }
                }
                GlobalLootModifier.setModifiers(lootPoolGlobalInjections);

            }, reloadExecutor).thenCompose(barrier::wait);
        });
    }

    private static void addInjectDataToMap(HashMap<ResourceLocation, HashMap<ResourceLocation, List<LootPool>>> map, ResourceLocation location, ResourceLocation cause, List<LootPool> injectPools) {
        HashMap<ResourceLocation, List<LootPool>> mapInjects = map.getOrDefault(location, new HashMap<>());
        List<LootPool> lootPools = mapInjects.getOrDefault(cause, new ArrayList<>());
        lootPools.addAll(injectPools);

        mapInjects.put(cause, lootPools);
        map.put(location, mapInjects);
    }

    private static void injectToLootPool(Registry<LootTable> registry, ResourceLocation resourceLocation, TablePoolInjection injections) {
        LootTable targetTable = registry.get(resourceLocation);
        if (targetTable == null) return;

        LootPool targetPool = targetTable.getPool(injections.name);
        targetPool = targetPool == null ? targetTable.getPool("pool"+injections.name) : targetPool;
        if (targetPool == null) return;

        LootPoolAccessor targetPoolAccessor = (LootPoolAccessor)targetPool;
        if (injections.weightAdjustment != 1.0) {
            for (LootPoolEntryContainer targetPoolEntries : targetPoolAccessor.getEntries()) {
                if (targetPoolEntries instanceof LootPoolSingletonContainer targetPoolContainer) {
                    LootPoolSingletonContainerAccessor accessor = ((LootPoolSingletonContainerAccessor)targetPoolContainer);
                    accessor.setWeight((int)(accessor.getWeight() * injections.weightAdjustment));
                    accessor.setQuality((int)(accessor.getQuality() * injections.weightAdjustment));
                }
            }
        }

        injections.entries.addAll(targetPoolAccessor.getEntries());
        targetPoolAccessor.setEntries(injections.entries);
    }

    public static void injectToLootTable(Registry<LootTable> registry, ResourceLocation resourceLocation, List<LootPool> injections) {
        LootTable targetTable = registry.get(resourceLocation);
        if (targetTable == null) return;

        for (LootPool pool : injections) {
            targetTable.addPool(pool);
        }
    }

    //@SubscribeEvent
    public static void addLoadedPoolsToTable(LootTableLoadEvent event) {
        ResourceLocation tableLocation = event.getName();
        if (lootPoolInjections.containsKey(tableLocation)) {
            LootTable targetTable = event.getTable();
            Set<TablePoolInjection> toInjectEntries = lootPoolInjections.get(tableLocation);
            for (TablePoolInjection injections : toInjectEntries) {
                LootPool targetPool = targetTable.getPool(injections.name);
                targetPool = targetPool == null ? targetTable.getPool("pool"+injections.name) : targetPool;
                if (targetPool == null) continue;

                LootPoolAccessor targetPoolAccessor = (LootPoolAccessor)targetPool;
                if (injections.weightAdjustment != 1.0) {
                    for (LootPoolEntryContainer targetPoolEntries : targetPoolAccessor.getEntries()) {
                        if (targetPoolEntries instanceof LootPoolSingletonContainer targetPoolContainer) {
                            LootPoolSingletonContainerAccessor accessor = ((LootPoolSingletonContainerAccessor)targetPoolContainer);
                            accessor.setWeight((int)(accessor.getWeight() * injections.weightAdjustment));
                            accessor.setQuality((int)(accessor.getQuality() * injections.weightAdjustment));
                        }
                    }
                }

                injections.entries.addAll(targetPoolAccessor.getEntries());
                targetPoolAccessor.setEntries(injections.entries);
            }
        }

        if (lootTableInjections.containsKey(tableLocation)) {
            Set<List<LootPool>> toInjectPools = lootTableInjections.get(tableLocation);
            for (List<LootPool> poolSet : toInjectPools) {
                for (LootPool pool : poolSet) {
                    event.getTable().addPool(pool);
                }
            }
        }
    }

    /*
    Tags load after all other resources, any data relying on tags from reload needs
    to be done after they are updated.
     */
    @SubscribeEvent
    public static void addDelayedLootInjections(TagsUpdatedEvent event) {
        if (!event.getUpdateCause().equals(TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD)) {
            return;
        }

        if (!delayedPoolStructureInjections.isEmpty()) {
            for (DelayedLootInjection<Structure> injection : delayedPoolStructureInjections) {
                Registry<Structure> structureRegistry = event.getRegistryAccess().registryOrThrow(Registries.STRUCTURE);
                structureRegistry.getTagOrEmpty(injection.tagKey).forEach(structure -> {
                    ResourceLocation structureLocation = structureRegistry.getKey(structure.value());
                    addInjectDataToMap(lootPoolStructureInjections, structureLocation, injection.cause, injection.injectPools);
                });
            }
            delayedPoolStructureInjections.clear();
        }
        StructureLootModifier.setModifiers(lootPoolStructureInjections);

        if (!delayedPoolBiomeInjections.isEmpty()) {
            for (DelayedLootInjection<Biome> injection : delayedPoolBiomeInjections) {
                Registry<Biome> biomeRegistry = event.getRegistryAccess().registryOrThrow(Registries.BIOME);
                biomeRegistry.getTagOrEmpty(injection.tagKey).forEach(biome -> {
                    ResourceLocation structureLocation = biomeRegistry.getKey(biome.value());
                    addInjectDataToMap(lootPoolBiomeInjections, structureLocation, injection.cause, injection.injectPools);
                });
            }
            delayedPoolBiomeInjections.clear();
        }
        BiomeLootModifier.setModifiers(lootPoolBiomeInjections);
    }
}
