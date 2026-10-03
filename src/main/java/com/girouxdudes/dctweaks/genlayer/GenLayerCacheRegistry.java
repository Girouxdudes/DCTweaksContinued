package com.girouxdudes.dctweaks.genlayer;
import net.minecraft.world.gen.layer.GenLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GenLayerCacheRegistry {
    //proxy-related caching
    public static ThreadLocal<int[]> cache = new ThreadLocal<>();
    public static Map<String, int[]> cacheGroups = new HashMap<>();
    public static GenLayer cacheLayer;
    public static ThreadLocal<Map<String, int[]>> secondaryCaches = ThreadLocal.withInitial(HashMap::new);

    //superchunk caching
    public static class SuperChunkEntry {
        public final int[] data;
        public AtomicInteger remaining;

        public SuperChunkEntry(int[] data, int remaining) {
            this.data = data;
            this.remaining = new AtomicInteger(remaining);
        }
    }
    public static Map<String, Map<Long, SuperChunkEntry>> superChunks = new ConcurrentHashMap<>();
    public static ThreadLocal<Boolean> genSuperChunks = ThreadLocal.withInitial(() -> false);

    //split caching
    public static ThreadLocal<Map<String, CacheEntry>> splitCache = ThreadLocal.withInitial(() -> new HashMap<>());
    public static ThreadLocal<Map<String, Boolean>> calculating = ThreadLocal.withInitial(() -> new HashMap<>());

    public static class CacheEntry {
        public int[] ints;
        public int x;
        public int z;
        public int size;

        public CacheEntry(int[] ints, int x, int z, int size) {
            this.ints = ints;
            this.x = x;
            this.z = z;
            this.size = size;
        }

    }
    //final split caching
    public static ThreadLocal<CacheEntry> finalCache = ThreadLocal.withInitial(() -> new CacheEntry(new int[0], 0, 0, 0));


}
