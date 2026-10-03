package com.girouxdudes.dctweaks.genlayer;

import com.bloodnbonesgaming.lib.util.script.ScriptClassDocumentation;
import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import net.minecraft.world.gen.layer.GenLayer;

import static com.girouxdudes.dctweaks.genlayer.GenLayerCacheRegistry.splitCache;

@ScriptClassDocumentation(documentationFile = "./config/dimensionalcontrol/documentation/genlayers/GenLayerCacheSplit", classExplaination = "Caches previous genlayers so that multiple child genlayers may reference this layer without recalculating earlier genlayers each time. Must be paired with either GenLayerRecombine, GenLayerZoomFilter, or GenLayerCacheStore")
public class GenLayerCacheSplit extends GenLayer {
    public String identifier;

    @ScriptMethodDocumentation(args = "long, GenLayer, string", usage = "base layer seed, parent layer, identifier", notes = "Constructs the layer with its base seed, parent layer, and identifier string. Each instance of this genlayer must have a unique name, which it must also share with any paired genlayers mentioned before")
    public GenLayerCacheSplit(long seed, GenLayer parent, String identifier) {
        super(seed);
        super.parent = parent;
        this.identifier = identifier;
    }


    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] returnInts = new int[areaWidth * areaHeight];
        GenLayerCacheRegistry.CacheEntry cache = splitCache.get().get(identifier);
        if(cache.ints.length == 0) {
            splitCache.get().put(identifier, new GenLayerCacheRegistry.CacheEntry(this.parent.getInts(cache.x, cache.z, cache.size, cache.size), cache.x, cache.z, cache.size));
            cache = splitCache.get().get(identifier);
        }
        int offsetX = areaX - cache.x;
        int offsetZ = areaY - cache.z;
        int[] finalCache = cache.ints;
        for (int z = 0; z < areaHeight; z++) {
            System.arraycopy(finalCache, (z + offsetZ) * cache.size + offsetX, returnInts, z * areaWidth, areaWidth);
        }
        return returnInts;
    }
}
