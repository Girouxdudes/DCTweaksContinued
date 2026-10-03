package com.girouxdudes.dctweaks.util;

import com.girouxdudes.dctweaks.genlayer.*;
import net.minecraft.world.gen.layer.GenLayer;

import static com.girouxdudes.dctweaks.genlayer.GenLayerCacheRegistry.splitCache;

//crawls up through genlayers to calculate total requested area size. Used in GenLayerCacheSplit and associated companion genlayers
public class GenLayerScanner {
    public static void CalculateCacheSize(String name, GenLayer genlayer, int size, int x, int z) {


        if(genlayer instanceof GenLayerCacheSplit) {
            GenLayerCacheSplit split = (GenLayerCacheSplit) genlayer;
            if(split.identifier.equals(name)) {
                GenLayerCacheRegistry.CacheEntry entry = splitCache.get().get(name);
                if(entry == null) {
                splitCache.get().put(name, new GenLayerCacheRegistry.CacheEntry(new int[0], x, z, size));
                } else if(size > entry.size) {
                    entry.x = x;
                    entry.z = z;
                    entry.size = size;
                }
                return;
            }
            GenLayerCacheRegistry.CacheEntry childEntry = GenLayerCacheRegistry.splitCache.get().get(split.identifier);
            if(childEntry != null) {
                GenLayerScanner.CalculateCacheSize(name, ((IGenLayer) genlayer).getParent(), childEntry.size, childEntry.x, childEntry.z);
                return;
            }

        }


        int zoom = GenLayerSizeRegistry.zoomFactor.getOrDefault(genlayer.getClass(), 1);
        int pad = GenLayerSizeRegistry.additivePadding.getOrDefault(genlayer.getClass(), 0);
        int size2 = size / zoom + (pad * 2);
        int x2 = x / zoom - pad;
        int z2 = z / zoom - pad;


        GenLayer parent = ((IGenLayer)(Object)genlayer).getParent();
        if(parent != null) CalculateCacheSize(name, parent, size2, x2, z2);

        if(genlayer instanceof IHasParent2) {
            GenLayer parent2 = ((IHasParent2) genlayer).getParent2();
            CalculateCacheSize(name, parent2, size2, x2, z2);
        }
    }
}
