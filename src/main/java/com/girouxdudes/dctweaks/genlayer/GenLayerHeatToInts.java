package com.girouxdudes.dctweaks.genlayer;

import com.bloodnbonesgaming.lib.util.script.ArgType;
import com.bloodnbonesgaming.lib.util.script.ScriptArgs;
import com.bloodnbonesgaming.lib.util.script.ScriptClassDocumentation;
import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

@ScriptClassDocumentation(documentationFile = "./config/dimensionalcontrol/documentation/genlayers/GenLayerHeatToBiomes", classExplaination = "A more configurable version of GenLayerBiomeDC. Replaces the heat map with a pseudo-random weighted biome map. For the vanilla heat map, normally 1 is desert, 2 is warm, 3 is cool, 4 is icy.")
public class GenLayerHeatToInts extends GenLayer {

    public static class WeightedInt {
        public final int id;
        public final int weight;
        public WeightedInt(int id, int weight) {
            this.id = id;
            this.weight = weight;
        }
    }

    private Int2ObjectOpenHashMap<List<WeightedInt>> biomeList = new Int2ObjectOpenHashMap<>();

    private int getWeightedEntry(List<WeightedInt> entries) {
        int totalWeight = 0;
        for (WeightedInt entry : entries) {
            totalWeight += entry.weight;
        }
        int roll = nextInt(totalWeight);
        for (WeightedInt entry : entries) {
            roll -= entry.weight;
            if (roll < 0) return entry.id;
        }
        return entries.get(entries.size() - 1).id;
    }

    @ScriptMethodDocumentation(args = "long, GenLayer", usage = "base layer seed, parent", notes = "Constructs the layer with the seed and parent.")
    public GenLayerHeatToInts(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    @ScriptArgs(args = {ArgType.INT, ArgType.NON_NULL_BIOME_ID})
    @ScriptMethodDocumentation(usage = "heat ID, biome ID", notes = "Adds the provided biome id to the provided heat id with a weight of 1. What heat ids are available depends on the GenLayer used before this.")
    public void addBiome(int heat, int id) {
        addBiome(heat, id, 1);
    }

    @ScriptArgs(args = {ArgType.INT, ArgType.NON_NULL_BIOME_ID, ArgType.INT})
    @ScriptMethodDocumentation(usage = "heat ID, biome ID, weight", notes = "Adds the provided biome id to the provided heat id with the provided weight. What heat ids are available depends on the GenLayer used before this.")
    public void addBiome(int heat, int id, int weight) {
        biomeList.computeIfAbsent(heat, k -> new ArrayList<>()).add(new WeightedInt(id, weight));
    }


    public int[] getInts(int chunkX, int chunkZ, int width, int depth) {
        int arrayLength = width * depth;
        int[] parentInts = this.parent.getInts(chunkX, chunkZ, width, depth);
        int[] returnInts = IntCache.getIntCache(arrayLength);
        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                initChunkSeed((x + chunkX), (z + chunkZ));
                int index = x + z * width;
                int currentBiome = parentInts[index];
                if(biomeList.containsKey(currentBiome)) {
                    returnInts[index] = getWeightedEntry(biomeList.get(currentBiome));
                } else {
                    returnInts[index] = currentBiome;
                }
            }
        }
        return returnInts;
    }

}
