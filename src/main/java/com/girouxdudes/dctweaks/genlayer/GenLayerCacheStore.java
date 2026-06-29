package com.girouxdudes.dctweaks.genlayer;

import com.bloodnbonesgaming.lib.util.script.ScriptClassDocumentation;
import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import net.minecraft.world.gen.layer.GenLayer;

@ScriptClassDocumentation(documentationFile = "./config/dimensionalcontrol/documentation/genlayers/GenLayerCacheStore", classExplaination = "Goes at the end of a biome provider script intended to be shared amongst multiple dimensions. Takes a seed and a parent genlayer")
public class GenLayerCacheStore extends GenLayer {

    @ScriptMethodDocumentation(args = "long, GenLayer", usage = "base layer seed, parent layer", notes = "Constructs the layer with its base seed and parent layer")
    public GenLayerCacheStore(long seed, GenLayer parent) {
        super(seed);
        super.parent = parent;
        GenLayerCacheRegistry.cacheLayer = this;
    }
    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        GenLayerCacheRegistry.cache = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
        return new int[0];
    }

}
