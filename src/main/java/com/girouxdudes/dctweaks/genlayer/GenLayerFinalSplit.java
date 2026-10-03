package com.girouxdudes.dctweaks.genlayer;

import com.bloodnbonesgaming.lib.util.script.ScriptClassDocumentation;
import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import net.minecraft.world.gen.layer.GenLayer;


@ScriptClassDocumentation(documentationFile = "./config/dimensionalcontrol/documentation/genlayers/GenLayerFinalSplit", classExplaination = "A variant of splitcache that is used at the end of a biome provider. Specifically, setGenLayers takes two genlayers, where the second is just the first with an additional voronoizoom. Placing this layer as the parent of both genlayers used in setGenLayers will cache the shared layers so that they only need to be recomputed once. This layer is also necessary if you want to use GenLayerCacheSuperChunks anywhere in your script.")
public class GenLayerFinalSplit extends GenLayer {

    @ScriptMethodDocumentation(args = "long, GenLayer", usage = "base layer seed, parent layer", notes = "Constructs the layer with its base seed and parent layer")
    public GenLayerFinalSplit(long seed, GenLayer parent) {
        super(seed);
        super.parent = parent;
    }

    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        if(areaWidth <= 10) {
            int[] returnInts = new int[areaWidth * areaHeight];
            ThreadLocal<Boolean> genSuperChunks = GenLayerCacheRegistry.genSuperChunks;
            genSuperChunks.set(true);

            ThreadLocal<GenLayerCacheRegistry.CacheEntry> cache = GenLayerCacheRegistry.finalCache;
            boolean outOfBounds = cache.get().ints.length != 0 && (
                    areaX < cache.get().x || areaY < cache.get().z ||
                            areaX + areaWidth > cache.get().x + cache.get().size ||
                            areaY + areaHeight > cache.get().z + cache.get().size
            );

            //calculate full size
            if(cache.get().ints.length == 0 || outOfBounds) {
                int offset = (10 - areaWidth) / 2;
                cache.set(new GenLayerCacheRegistry.CacheEntry(this.parent.getInts(areaX - offset, areaY - offset, 10, 10), areaX - offset, areaY - offset, 10));
                }
            genSuperChunks.set(false);
            //portion the area
            int offsetX = areaX - cache.get().x;
            int offsetZ = areaY - cache.get().z;
            int[] finalCache = cache.get().ints;
            for (int z = 0; z < areaHeight; z++) {
                System.arraycopy(finalCache, (z + offsetZ) * cache.get().size + offsetX, returnInts, z * areaWidth, areaWidth);
            }
            return returnInts;

        } else {
            return parent.getInts(areaX, areaY, areaWidth, areaHeight);
        }
    }
}
