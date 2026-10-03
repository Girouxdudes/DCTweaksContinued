package com.girouxdudes.dctweaks.genlayer;

import com.bloodnbonesgaming.lib.util.script.ArgType;
import com.bloodnbonesgaming.lib.util.script.ScriptArgs;
import com.bloodnbonesgaming.lib.util.script.ScriptClassDocumentation;
import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import com.girouxdudes.dctweaks.util.GenLayerScanner;
import com.girouxdudes.dctweaks.util.IHasParent2;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

@ScriptClassDocumentation(documentationFile = "./config/dimensionalcontrol/documentation/genlayers/GenLayerZoomFilter", classExplaination = "Zoom layer variant that takes a second genlayer as a filter to give preferential treatment to certain corners during magnification. Ideal for keeping two biome maps synchronized during magnification. Takes an optional string identifier to synchronize with GenLayerCacheSplit")
public class GenLayerZoomFilter extends GenLayer implements IHasParent2 {
    private GenLayer parent2;
    public String identifier;

    private Int2ObjectOpenHashMap<IntOpenHashSet> filters = new Int2ObjectOpenHashMap<>();

    private Int2IntOpenHashMap substitutions = new Int2IntOpenHashMap();
    private Int2IntOpenHashMap biases = new Int2IntOpenHashMap();
    private Int2IntOpenHashMap groups = new Int2IntOpenHashMap();

    private Int2IntOpenHashMap counts = new Int2IntOpenHashMap();
    private IntArrayList topBiomes = new IntArrayList();
    private Int2ObjectOpenHashMap<IntArrayList> groups2 = new Int2ObjectOpenHashMap<>();

    @ScriptMethodDocumentation(args = "long, GenLayer, GenLayer", usage = "base layer seed, parent layer, filter layer", notes = "Constructs the layer with its base seed and two parent layers. If trying to synchronize two genlayers post magnification, the second layer should already be magnified.")
    public GenLayerZoomFilter(long seed, GenLayer parent, GenLayer parent2) {
        super(seed);
        super.parent = parent;
        this.parent2 = parent2;
    }

    @ScriptMethodDocumentation(args = "long, GenLayer, GenLayer, String", usage = "base layer seed, parent layer, filter layer, string identifier", notes = "Constructs the layer with its base seed and two parent layers. If trying to synchronize two genlayers post magnification, the second layer should already be magnified. This variant can be used in conjunction with GenLayerCacheSplit.")
    public GenLayerZoomFilter(long seed, GenLayer parent, GenLayer parent2, String identifier) {
        super(seed);
        super.parent = parent;
        this.parent2 = parent2;
        this.identifier = identifier;
    }

    public GenLayer getParent2() {
        return parent2;
    }

    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        if(identifier != null) {
            if (!GenLayerCacheRegistry.calculating.get().getOrDefault(identifier, false)) {
                GenLayerCacheRegistry.calculating.get().put(identifier, true);
                GenLayerScanner.CalculateCacheSize(identifier, this, areaWidth, areaX, areaY);
            }
        }
        int i = areaX >> 1;
        int j = areaY >> 1;
        int k = (areaWidth >> 1) + 2;
        int l = (areaHeight >> 1) + 2;
        int[] aint = this.parent.getInts(i, j, k, l);
        int[] filterInts = this.parent2.getInts(areaX, areaY, areaWidth, areaHeight);
        int i1 = k - 1 << 1;
        int j1 = l - 1 << 1;
        int[] aint1 = IntCache.getIntCache(i1 * j1);

        for (int k1 = 0; k1 < l - 1; ++k1)
        {
            int l1 = (k1 << 1) * i1;
            int i2 = 0;
            int j2 = aint[i2 + k1 * k];

            for (int k2 = aint[i2 + (k1 + 1) * k]; i2 < k - 1; ++i2)
            {
                this.initChunkSeed((long)(i2 + i << 1), (long)(k1 + j << 1));
                int l2 = aint[i2 + 1 + k1 * k];
                int i3 = aint[i2 + 1 + (k1 + 1) * k];
                int filterPos = (i2 << 1) + 1 + ((k1 << 1) + 1) * areaWidth;
                aint1[l1] = j2;

                int bottomLeft = this.selectWithBias(j2, k2, filterInts[filterPos + areaWidth]);
                aint1[l1++ + i1] = substitutions.getOrDefault(bottomLeft, bottomLeft);

                int topRight = this.selectWithBias(j2, l2, filterInts[filterPos + 1]);
                aint1[l1] = substitutions.getOrDefault(topRight, topRight);

                int bottomRight = this.selectWithGroupBias(j2, l2, k2, i3, filterInts[filterPos + areaWidth + 1]);
                aint1[l1++ + i1] = substitutions.getOrDefault(bottomRight, bottomRight);
                j2 = l2;
                k2 = i3;
            }
        }

        int[] aint2 = IntCache.getIntCache(areaWidth * areaHeight);

        for (int j3 = 0; j3 < areaHeight; ++j3)
        {
            System.arraycopy(aint1, (j3 + (areaY & 1)) * i1 + (areaX & 1), aint2, j3 * areaWidth, areaWidth);
        }
        if(identifier != null) {
            GenLayerCacheRegistry.splitCache.get().remove(identifier);
            GenLayerCacheRegistry.calculating.get().remove(identifier);
        }
        return aint2;
    }

    private int selectWithBias(int a, int b, int filter) {
        IntOpenHashSet allowed = filters.get(filter);
        if(allowed != null) {
            boolean filterA = allowed.contains(a);
            boolean filterB = allowed.contains(b);
            if(filterA && !filterB) return a;
            if(filterB && !filterA) return b;
        }
        int biasA = biases.getOrDefault(a, 0);
        int biasB = biases.getOrDefault(b, 0);
        if(biasA > biasB) return a;
        if(biasB > biasA) return b;
        return this.selectRandom(new int[]{a, b});
    }

    private int selectWithBiasLarge(int[] biomes) {
        counts.clear();
        topBiomes.clear();
        for (int biome : biomes) {
            counts.put(biome, counts.getOrDefault(biome, 0) + 1);
        }

        int modeValue = -1;
        int modeCount = 0;
        boolean modeTied = false;

        for (ObjectIterator<Int2IntMap.Entry> it = counts.int2IntEntrySet().fastIterator(); it.hasNext();) {
            Int2IntMap.Entry entry = it.next();
            int count = entry.getIntValue();
            if (count > modeCount) {
                modeCount = count;
                modeValue = entry.getIntKey();
                modeTied = false;
            } else if(count == modeCount) {
                modeTied = true;
            }
        }
        if(modeCount > 1 && !modeTied) {
            return modeValue;
        }
        int maxBias = Integer.MIN_VALUE;
        for (int biome : biomes) {
            int bias = biases.getOrDefault(biome,0);
            if (bias > maxBias) maxBias = bias;
        }
        for (int biome : biomes) {
            if (biases.getOrDefault(biome, 0) == maxBias) topBiomes.add(biome);
        }
        return this.selectRandom(topBiomes.toIntArray());
    }

    private int[] filterScreen(int a, int b, int c, int d, int filter) {
        IntOpenHashSet allowed = filters.get(filter);
        if(allowed == null) {
            return new int[]{a, b, c, d};
        }
        IntArrayList filteredList = new IntArrayList();
        for (int biome : new int[]{a, b, c, d}) {
            if(allowed.contains(biome)) filteredList.add(biome);
        }
        if(filteredList.isEmpty()) {
            return new int[]{a, b, c, d};
        } else {
            return filteredList.toIntArray();
        }
    }

    private int selectWithGroupBias(int a, int b, int c, int d, int filter) {
        for (IntArrayList list : groups2.values()) {
            list.clear();
        }
        groups2.clear();
        for (int corner : filterScreen(a, b, c, d, filter)) {
            if(groups.containsKey(corner)) {
                int id = groups.get(corner);
                groups2.computeIfAbsent(id, k -> new IntArrayList()).add(corner);
            }
        }
        int winner = -1;
        int winSize = 0;
        boolean tied = false;

        for (ObjectIterator<Int2ObjectMap.Entry<IntArrayList>> it = groups2.int2ObjectEntrySet().fastIterator(); it.hasNext();) {
            Int2ObjectMap.Entry<IntArrayList> entry = it.next();
            int size = entry.getValue().size();
            if(size > winSize) {
                winSize = size;
                winner = entry.getIntKey();
                tied = false;
            } else if(size == winSize) {
                tied = true;
            }
        }
        if(winSize >= 2 && !tied) {
            return selectWithBiasLarge(groups2.get(winner).toIntArray());
        } else {
            return selectWithBiasLarge(new int[]{a, b, c, d});
        }

    }
    @ScriptArgs(args = {ArgType.NON_NULL_BIOME_ID, ArgType.NON_NULL_BIOME_ID_ARRAY})
    @ScriptMethodDocumentation(usage = "target biome id, filter biome ids", notes = "Ties a biome id to a list of ids in the filter biome map.")
    public void addFilter(int biome, int[] filter) {
        filters.put(biome, new IntOpenHashSet(filter));
    }

    @ScriptArgs(args = {ArgType.NON_NULL_BIOME_ID, ArgType.NON_NULL_BIOME_ID})
    @ScriptMethodDocumentation(usage = "target biome id, replacement biome id", notes = "Sets a replacement biome that will be placed instead of the target biome during magnification.")
    public void addSubstitution(int target, int substitute) {
        substitutions.put(target, substitute);
    }

    @ScriptArgs(args = {ArgType.NON_NULL_BIOME_ID, ArgType.INT})
    @ScriptMethodDocumentation(usage = "target biome id, bias value", notes = "Assigns a bias value to a given biome. Used when determining which biomes should expand during magnification. Higher values are prioritized over lower ones.")
    public void addBias(int biome, int bias) { biases.put(biome, bias); }

    @ScriptArgs(args = {ArgType.INT, ArgType.NON_NULL_BIOME_ID_ARRAY})
    @ScriptMethodDocumentation(usage = "group id, group of biome ids", notes = "Groups the given biomes together for magnification purposes. When determining which biome should win the center magnification biome, if enough corners belong to a group, it chooses the new biome from within that group. All group ids should be unique, and a biome should only belong to at most one group")
    public void addGroup(int groupID, int[] biomes) {
        for (int i = 0; i < biomes.length; i++) {
            groups.put(biomes[i], groupID);
        }
    }
}
