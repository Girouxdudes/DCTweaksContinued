package com.girouxdudes.dctweaks.genlayer;

import com.bloodnbonesgaming.lib.util.script.ScriptClassDocumentation;
import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import net.minecraft.world.gen.layer.GenLayer;

import java.util.Arrays;

@ScriptClassDocumentation(documentationFile = "./config/dimensionalcontrol/documentation/genlayers/GenLayerCacheRetrieve", classExplaination = "Goes at the top of biome providers that derive their initial genlayers from a cached biome provider. Note that any genlayers that increase the requested area (such as genlayertouching, which needs neighbor data) cannot be used after this genlayer")
public class GenLayerCacheRetrieve extends GenLayer {
    @ScriptMethodDocumentation(args = "long", usage = "base layer seed", notes = "Constructs the layer with its base seed. The cached genlayer selected is determined by the cache group this dimension belongs to. Do not assign a dimension to multiple cache groups")
    public GenLayerCacheRetrieve(long seed) {
        super(seed);
    }
    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        if (GenLayerCacheRegistry.cache == null) {
            int[] dummy = new int[areaWidth * areaHeight];
            Arrays.fill(dummy, 1);
            return dummy;
        }
        return Arrays.copyOf(GenLayerCacheRegistry.cache, GenLayerCacheRegistry.cache.length);
    }
}
