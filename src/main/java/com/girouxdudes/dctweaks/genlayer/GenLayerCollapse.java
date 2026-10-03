package com.girouxdudes.dctweaks.genlayer;

import com.bloodnbonesgaming.lib.util.script.ArgType;
import com.bloodnbonesgaming.lib.util.script.ScriptArgs;
import com.bloodnbonesgaming.lib.util.script.ScriptClassDocumentation;
import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

@ScriptClassDocumentation(documentationFile = "./config/dimensionalcontrol/documentation/genlayers/GenLayerCollapse", classExplaination = "Intended to be a performant way to batch-transform multiple ids to a single one. Unlike GenLayerTouching, it doesn't request a greater parent area.")
public class GenLayerCollapse extends GenLayer {

    Int2IntOpenHashMap collapseGroups = new Int2IntOpenHashMap();

    @ScriptMethodDocumentation(args = "long, GenLayer", usage = "base layer seed, parent layer", notes = "Constructs the layer with its base seed and parent layer")
    public GenLayerCollapse(long seed, GenLayer parent) {
        super(seed);
        super.parent = parent;
    }
    public int[] getInts(int areaX, int areaZ, int areaWidth, int areaHeight) {
        int[] parentInts = this.parent.getInts(areaX, areaZ, areaWidth, areaHeight);
        int[] returnInts = IntCache.getIntCache(areaWidth * areaHeight);

        for (int z = 0; z < areaHeight; z++) {
            for (int x = 0; x < areaWidth; x++) {
                int biome = parentInts[x + z * areaWidth];
                biome = collapseGroups.getOrDefault(biome, biome);
                returnInts[x + z * areaWidth] = biome;
            }
        }
        return returnInts;
    }
    @ScriptArgs(args = {ArgType.NON_NULL_BIOME_ID_ARRAY, ArgType.NON_NULL_BIOME_ID})
    @ScriptMethodDocumentation(usage = "target biome ids, output biome id", notes = "The coordinate threshold is non-inclusive")
    public void addData(int[] inputs, int output) {
        for (int input : inputs) {
            collapseGroups.put(input, output);
        }
    }
}