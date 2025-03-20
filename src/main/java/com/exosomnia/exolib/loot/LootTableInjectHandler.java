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
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = ExoLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LootTableInjectHandler {

    private static HashMultimap<ResourceLocation, List<LootPool>> lootTableInjections = HashMultimap.create();
    private static HashMultimap<ResourceLocation, TablePoolInjection> lootPoolInjections = HashMultimap.create();
    private static HashMap<ResourceLocation, HashMap<ResourceLocation, List<LootPool>>> lootPoolStructureInjections = new HashMap<>();
    private static HashMap<ResourceLocation, HashMap<ResourceLocation, List<LootPool>>> lootPoolBiomeInjections = new HashMap<>();
    private static HashMap<ResourceLocation, List<LootPool>> lootPoolGlobalInjections = new HashMap<>();

    private static List<DelayedLootInjection<Structure>> delayedPoolStructureInjections = new ArrayList<>();
    private static List<DelayedLootInjection<Biome>> delayedPoolBiomeInjections = new ArrayList<>();

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
                Gson lootTableGSON = Deserializers.createLootTableSerializer().create();
                Map<ResourceLocation, Resource> resources = resourceManager.listResources("loot_table_injections", (resourceLocation) -> true);

                for (ResourceLocation location : resources.keySet()) {
                    try {
                        InputStreamReader reader = new InputStreamReader(resources.get(location).open());
                        JsonObject head = JsonParser.parseReader(reader).getAsJsonObject();
                        if (head.has("injects")) {
                            JsonArray injectsArray = head.getAsJsonArray("injects");
                            LootTable table = ForgeHooks.loadLootTable(lootTableGSON, location, head, true);
                            if (injectsArray.isEmpty()) {
                                continue;
                            }

                            List<LootPool> injectPools = ((LootTableAccessor) table).getPools();

                            for (JsonElement locationElement : injectsArray) {
                                lootTableInjections.put(ResourceLocation.bySeparator(locationElement.getAsString(), ':'), injectPools);
                            }
                        } else if (head.has("structure_injects")) {
                            JsonObject structureInjectData = head.getAsJsonObject("structure_injects");
                            JsonArray structuresArray = structureInjectData.getAsJsonArray("structures");
                            ResourceLocation cause = ResourceLocation.bySeparator(structureInjectData.get("cause").getAsString(), ':');
                            LootTable table = ForgeHooks.loadLootTable(lootTableGSON, location, head, true);
                            if (structuresArray.isEmpty()) {
                                continue;
                            }

                            List<LootPool> injectPools = ((LootTableAccessor) table).getPools();

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
                        } else if (head.has("biome_injects")) {
                            JsonObject biomeInjectData = head.getAsJsonObject("biome_injects");
                            JsonArray biomeArray = biomeInjectData.getAsJsonArray("biomes");
                            ResourceLocation cause = ResourceLocation.bySeparator(biomeInjectData.get("cause").getAsString(), ':');
                            LootTable table = ForgeHooks.loadLootTable(lootTableGSON, location, head, true);
                            if (biomeArray.isEmpty()) {
                                continue;
                            }

                            List<LootPool> injectPools = ((LootTableAccessor) table).getPools();

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
                        } else if (head.has("global_injects")) {
                            JsonObject globalInjectData = head.getAsJsonObject("global_injects");
                            ResourceLocation cause = ResourceLocation.bySeparator(globalInjectData.get("cause").getAsString(), ':');
                            LootTable table = ForgeHooks.loadLootTable(lootTableGSON, location, head, true);
                            List<LootPool> injectPools = ((LootTableAccessor) table).getPools();
                            List<LootPool> currentPools = lootPoolGlobalInjections.getOrDefault(cause, new ArrayList<>());
                            currentPools.addAll(injectPools);
                            lootPoolGlobalInjections.put(cause, currentPools);
                        } else if (head.has("pool_injects")) {
                            JsonArray poolInjectsArray = head.getAsJsonArray("pool_injects");
                            LootTable table = ForgeHooks.loadLootTable(lootTableGSON, location, head, true);
                            if (poolInjectsArray.isEmpty()) {
                                continue;
                            }

                            List<LootPool> injectPools = ((LootTableAccessor) table).getPools();
                            List<LootPoolEntryContainer> injectEntries = new ArrayList<>();
                            for (LootPool pool : injectPools) {
                                injectEntries.addAll(List.of(((LootPoolAccessor)pool).getEntries()));
                            }
                            if (injectEntries.isEmpty()) { continue; }

                            for (JsonElement locationElement : poolInjectsArray) {
                                JsonObject poolInjectData = locationElement.getAsJsonObject();
                                JsonElement tableLocation = poolInjectData.get("table");
                                String poolName = poolInjectData.has("pool") ? poolInjectData.get("pool").getAsString() : "main";
                                double weightAdjustment = poolInjectData.has("weight_adjustment") ? poolInjectData.get("weight_adjustment").getAsDouble() : 1.0;
                                lootPoolInjections.put(ResourceLocation.bySeparator(tableLocation.getAsString(), ':'), new TablePoolInjection(poolName, weightAdjustment, injectEntries));
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

    @SubscribeEvent
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

                injections.entries.addAll(List.of(targetPoolAccessor.getEntries()));
                targetPoolAccessor.setEntries(injections.entries.toArray(new LootPoolEntryContainer[injections.entries.size()]));
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
    to be done after they are updated. Seems dumb to me, but idk.
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
                    ResourceLocation structureLocation = structureRegistry.getKey(structure.get());
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
                    ResourceLocation structureLocation = biomeRegistry.getKey(biome.get());
                    addInjectDataToMap(lootPoolBiomeInjections, structureLocation, injection.cause, injection.injectPools);
                });
            }
            delayedPoolBiomeInjections.clear();
        }
        BiomeLootModifier.setModifiers(lootPoolBiomeInjections);
    }
}
