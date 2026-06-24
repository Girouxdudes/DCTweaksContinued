package com.bluefire.dctweaks.genlayer;

import com.bloodnbonesgaming.lib.util.script.ArgType;
import com.bloodnbonesgaming.lib.util.script.ScriptArgs;
import com.bloodnbonesgaming.lib.util.script.ScriptClassDocumentation;
import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import java.util.HashMap;
import java.util.Map;

@ScriptClassDocumentation(documentationFile = "./config/dimensionalcontrol/documentation/genlayers/GenLayerClamp", classExplaination = "Allows replacing biomes beyond specified world coordinates")
public class GenLayerClamp extends GenLayer {
    @ScriptMethodDocumentation(args = "long, GenLayer", usage = "base layer seed, parent layer", notes = "Constructs the layer with its base seed and parent layer")
    public GenLayerClamp(long seed, GenLayer parent) {
        super(seed);
        super.parent = parent;
    }
    private static class ClampRule {
        final int threshold;
        final int replacement;

        ClampRule(int threshold, int replacement) {
            this.threshold = threshold;
            this.replacement = replacement;
        }
    }
    private Map<Integer, ClampRule> minX = new HashMap<>();
    private Map<Integer, ClampRule> maxX = new HashMap<>();
    private Map<Integer, ClampRule> minZ = new HashMap<>();
    private Map<Integer, ClampRule> maxZ = new HashMap<>();
    public int[] getInts(int areaX, int areaZ, int areaWidth, int areaHeight) {
        int[] parentInts = this.parent.getInts(areaX, areaZ, areaWidth, areaHeight);
        int[] returnInts = IntCache.getIntCache(areaWidth * areaHeight);

        for (int z = 0; z < areaHeight; z++) {
            for (int x = 0; x < areaWidth; x++) {
                int biome = parentInts[x + z * areaWidth];
                int globalX = areaX + x;
                int globalZ = areaZ + z;
                boolean replaced = false;
                if(minX.containsKey(biome) && minX.get(biome).threshold < globalX) {
                    biome = minX.get(biome).replacement;
                    replaced = true;
                }
                if(!replaced && minZ.containsKey(biome) && minZ.get(biome).threshold < globalZ) {
                    biome = minZ.get(biome).replacement;
                    replaced = true;
                }
                if(!replaced && maxX.containsKey(biome) && maxX.get(biome).threshold > globalX) {
                    biome = maxX.get(biome).replacement;
                    replaced = true;
                }
                if(!replaced && maxZ.containsKey(biome) && maxZ.get(biome).threshold > globalZ) {
                    biome = maxZ.get(biome).replacement;
                }
                returnInts[x + z * areaWidth] = biome;
            }
        }
        return returnInts;
    }
    @ScriptArgs(args = {ArgType.NON_NULL_BIOME_ID, ArgType.INT, ArgType.NON_NULL_BIOME_ID})
    @ScriptMethodDocumentation(usage = "target biome id, minimum x value, replacement biome id", notes = "The coordinate threshold is non-inclusive")
    public void addMinXClamp(int target, int threshold, int replacement) {
        minX.put(target, new ClampRule(threshold, replacement));
    }
    @ScriptArgs(args = {ArgType.NON_NULL_BIOME_ID, ArgType.INT, ArgType.NON_NULL_BIOME_ID})
    @ScriptMethodDocumentation(usage = "target biome id, minimum z value, replacement biome id", notes = "The coordinate threshold is non-inclusive")
    public void addMinZClamp(int target, int threshold, int replacement) {
        minZ.put(target, new ClampRule(threshold, replacement));
    }
    @ScriptArgs(args = {ArgType.NON_NULL_BIOME_ID, ArgType.INT, ArgType.NON_NULL_BIOME_ID})
    @ScriptMethodDocumentation(usage = "target biome id, maximum x value, replacement biome id", notes = "The coordinate threshold is non-inclusive")
    public void addMaxXClamp(int target, int threshold, int replacement) {
        maxX.put(target, new ClampRule(threshold, replacement));
    }
    @ScriptArgs(args = {ArgType.NON_NULL_BIOME_ID, ArgType.INT, ArgType.NON_NULL_BIOME_ID})
    @ScriptMethodDocumentation(usage = "target biome id, maximum z value, replacement biome id", notes = "The coordinate threshold is non-inclusive")
    public void addMaxZClamp(int target, int threshold, int replacement) {
        maxZ.put(target, new ClampRule(threshold, replacement));
    }
}
