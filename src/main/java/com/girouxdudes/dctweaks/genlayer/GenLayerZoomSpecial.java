package com.girouxdudes.dctweaks.genlayer;

import com.bloodnbonesgaming.lib.util.script.ArgType;
import com.bloodnbonesgaming.lib.util.script.ScriptArgs;
import com.bloodnbonesgaming.lib.util.script.ScriptClassDocumentation;
import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;


@ScriptClassDocumentation(documentationFile = "./config/dimensionalcontrol/documentation/genlayers/GenLayerZoomSpecial", classExplaination = "This is a special variant of zoom that allows you to specify a replacement biome when magnifying. Useful if you want a biome to stay the same size.")
public class GenLayerZoomSpecial extends GenLayer {
    private Int2IntOpenHashMap substitutions = new Int2IntOpenHashMap();
    private Int2IntOpenHashMap biases = new Int2IntOpenHashMap();
    private Int2IntOpenHashMap groups = new Int2IntOpenHashMap();
    private Int2ObjectOpenHashMap<Int2ObjectOpenHashMap<int[]>> permutations = new Int2ObjectOpenHashMap<>();

    private Int2IntOpenHashMap counts = new Int2IntOpenHashMap();
    private IntArrayList topBiomes = new IntArrayList();
    private Int2ObjectOpenHashMap<IntArrayList> groups2 = new Int2ObjectOpenHashMap<>();
    private IntArrayList perms = new IntArrayList();

    public GenLayerZoomSpecial(long p_i2134_1_, GenLayer p_i2134_3_)
    {
        super(p_i2134_1_);
        super.parent = p_i2134_3_;
    }

    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight)
    {
        int i = areaX >> 1;
        int j = areaY >> 1;
        int k = (areaWidth >> 1) + 2;
        int l = (areaHeight >> 1) + 2;
        int[] aint = this.parent.getInts(i, j, k, l);
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
                aint1[l1] = j2;

                int bottomLeft = this.selectWithBias(j2, k2);
                aint1[l1++ + i1] = substitutions.getOrDefault(bottomLeft, bottomLeft);

                int topRight = this.selectWithBias(j2, l2);
                aint1[l1] = substitutions.getOrDefault(topRight, topRight);

                int bottomRight = this.selectWithGroupBias(j2, l2, k2, i3);
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

        return aint2;
    }
    private int selectWithBias(int a, int b) {
        Int2ObjectOpenHashMap<int[]> inner = permutations.get(a);
        if(inner != null) {
            int[] options = inner.get(b);
            if(options != null) return options[nextInt(options.length)];
        }
        int biasA = biases.getOrDefault(a, 0);
        int biasB = biases.getOrDefault(b, 0);
        if(biasA > biasB) return a;
        if(biasB > biasA) return b;
        return this.selectRandom(new int[]{a, b});
    }

    private int selectWithBiasLarge(int[] biomes, int[] biomes2) {
        counts.clear();
        topBiomes.clear();
        perms.clear();
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
            Int2ObjectOpenHashMap<int[]> inner = permutations.get(modeValue);
            if(inner != null) {
                for (int biome2 : biomes2) {
                    if(inner.containsKey(biome2)) {
                        for (int biome3: inner.get(biome2)) perms.add(biome3);
                    }
                }
                if(!perms.isEmpty()) return this.selectRandom(perms.toIntArray());
            }
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
        int finalBiome = this.selectRandom(topBiomes.toIntArray());
        Int2ObjectOpenHashMap<int[]> inner2 = permutations.get(finalBiome);
        if(inner2 != null) {
            IntArrayList perms = new IntArrayList();
            for (int biome2 : biomes2) {
                if(inner2.containsKey(biome2)) {
                    for (int biome3: inner2.get(biome2)) perms.add(biome3);
                }
            }
            if(!perms.isEmpty()) return this.selectRandom(perms.toIntArray());
        }
        return finalBiome;
    }

    private int selectWithGroupBias(int a, int b, int c, int d) {
        for (IntArrayList list : groups2.values()) {
            list.clear();
        }
        groups2.clear();
        for (int corner : new int[]{a, b, c, d}) {
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
            return selectWithBiasLarge(groups2.get(winner).toIntArray(), new int[]{a, b, c, d});
        } else {
            return selectWithBiasLarge(new int[]{a, b, c, d}, new int[]{a, b, c, d});
        }

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
    @ScriptArgs(args = {ArgType.NON_NULL_BIOME_ID, ArgType.NON_NULL_BIOME_ID, ArgType.NON_NULL_BIOME_ID_ARRAY})
    @ScriptMethodDocumentation(usage = "biome a, biome b, possible permutations", notes = "Defines permutations of biomes when deciding which corner wins a specific expansion. Think of it as a built-in GenLayerTouching rule")
    public void addPermutation(int biomeA, int biomeB, int[] outcomes) {
        permutations.computeIfAbsent(biomeA, k -> new Int2ObjectOpenHashMap<>()).put(biomeB, outcomes);
        permutations.computeIfAbsent(biomeB, k -> new Int2ObjectOpenHashMap<>()).put(biomeA, outcomes);
    }
}
