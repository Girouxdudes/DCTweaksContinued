package com.girouxdudes.dctweaks.genlayer;

import com.bloodnbonesgaming.lib.util.script.ScriptClassDocumentation;
import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import net.minecraft.world.gen.layer.GenLayer;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ScriptClassDocumentation(documentationFile = "./config/dimensionalcontrol/documentation/genlayers/GenLayerCacheSuperChunk", classExplaination = "Caches parent layer outputs and reuses them for shared areas. Should be placed right before a zoom layer. Scale is the number of subsequent zoom layers minus 4, and the name must be unique if using multiple SuperChunkCaches. Requires GenLayerFinalSplit to be present at the end of the biome provider script. My advice is to add these genlayers only after you've completely the script.")
public class GenLayerCacheSuperChunk extends GenLayer {
    private final int scale;
    private final String name;
    private final boolean isSilent;
    @ScriptMethodDocumentation(args = "long, GenLayer, scale, name, boolean", usage = "base layer seed, parent layer, zoom scale, identifier string, silent boolean", notes = "Constructs the layer with its base seed and parent layer. Scale refers to the number of zoom layers minus 2 from GenLayerFinalSplit, and the string identifier is used to distinguish multiple superchunk genlayers. If using superchunks in a proxy dimension that shares initial generation parameters with the parent dimension, set the silent boolean to true, otherwise make it false.")
    public GenLayerCacheSuperChunk(long seed, GenLayer parent, int scale, String name, boolean isSilent) {
        super(seed);
        this.parent = parent;
        this.scale = scale;
        this.name = name;
        this.isSilent = isSilent;
    }

    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        ThreadLocal<Boolean> genSuperChunks = GenLayerCacheRegistry.genSuperChunks;
        if (genSuperChunks != null && genSuperChunks.get()) {
            Map<Long, GenLayerCacheRegistry.SuperChunkEntry> cache = GenLayerCacheRegistry.superChunks.computeIfAbsent(name, k -> new ConcurrentHashMap<>());
            long key = (long) areaX << 32 | (areaY & 0xFFFFFFFFL);
            GenLayerCacheRegistry.SuperChunkEntry entry = cache.get(key);
            if (entry == null) {
                int[] output = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
                int[] stored = Arrays.copyOf(output, output.length);
                cache.put(key, new GenLayerCacheRegistry.SuperChunkEntry(stored, (1 << (scale * 2)) - 1));
                return output;
            } else {
                if(!isSilent) {
                    if (entry.remaining.decrementAndGet() == 0) cache.remove(key);
                }
                return entry.data;
            }
        }
        return parent.getInts(areaX, areaY, areaWidth, areaHeight);
    }
}
