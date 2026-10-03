package com.girouxdudes.dctweaks.genlayer;

import com.bloodnbonesgaming.lib.util.script.ArgType;
import com.bloodnbonesgaming.lib.util.script.ScriptArgs;
import com.bloodnbonesgaming.lib.util.script.ScriptClassDocumentation;
import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import com.girouxdudes.dctweaks.util.IHasParent2;
import com.girouxdudes.dctweaks.util.GenLayerScanner;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import java.util.HashMap;
import java.util.Map;

@ScriptClassDocumentation(documentationFile = "./config/dimensionalcontrol/documentation/genlayers/GenLayerRecombine", classExplaination = "A variant of GenLayerCombine that supports the split cache system. The string cache identifier must match the one used in the relevant GenLayerCacheSplit")
public class GenLayerRecombine extends GenLayer implements IHasParent2 {
    private GenLayer parent2;
    public String identifier;
    private Map<Integer, Map<Integer, Integer>> combinations = new HashMap<>();

    @ScriptMethodDocumentation(args = "long, GenLayer, GenLayer, String", usage = "base layer seed, first parent, second parent, cache identifier", notes = "Constructs the layer.")
    public GenLayerRecombine(long seed, GenLayer parent, GenLayer parent2, String identifier) {
        super(seed);
        this.parent = parent;
        this.parent2 = parent2;
        this.identifier = identifier;
    }
    @ScriptArgs(args = {ArgType.NON_NULL_BIOME_ID_ARRAY})
    @ScriptMethodDocumentation(usage = "input 1, input 2, output", notes = "Takes 2 ids and returns the output")
    public void addCombination(int[] combination) {
        combinations.computeIfAbsent(combination[0], k -> new HashMap<>()).put(combination[1], combination[2]);
    }


    public GenLayer getParent2() {
        return parent2;
    }


    public int[] getInts(int chunkX, int chunkZ, int width, int depth) {
        if (!GenLayerCacheRegistry.calculating.get().getOrDefault(identifier, false)) {
            GenLayerCacheRegistry.calculating.get().put(identifier, true);
            GenLayerScanner.CalculateCacheSize(identifier, this, width, chunkX, chunkZ);
        }

        int[] parentInts = this.parent.getInts(chunkX, chunkZ, width, depth);
        int[] parentInts2 = this.parent2.getInts(chunkX, chunkZ, width, depth);
        int[] returnInts = IntCache.getIntCache(width * depth);

        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                initChunkSeed(x + chunkX, z + chunkZ);
                int index = x + z * width;
                int currentBiome = parentInts[index];
                int currentBiome2 = parentInts2[index];
                returnInts[index] = currentBiome;
                if (combinations.containsKey(currentBiome)) {
                    Integer out = combinations.get(currentBiome).get(currentBiome2);
                    if (out != null) returnInts[index] = out;
                }
            }
        }
        //splitcache clear
        GenLayerCacheRegistry.splitCache.get().remove(identifier);
        GenLayerCacheRegistry.calculating.get().remove(identifier);

        return returnInts;
    }
}