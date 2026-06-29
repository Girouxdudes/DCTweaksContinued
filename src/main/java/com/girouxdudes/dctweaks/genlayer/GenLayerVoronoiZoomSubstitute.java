package com.girouxdudes.dctweaks.genlayer;

import com.bloodnbonesgaming.lib.util.script.ArgType;
import com.bloodnbonesgaming.lib.util.script.ScriptArgs;
import com.bloodnbonesgaming.lib.util.script.ScriptClassDocumentation;
import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

@ScriptClassDocumentation(documentationFile = "./config/dimensionalcontrol/documentation/genlayers/GenLayerVoronoiZoomSpecial", classExplaination = "This is a special variant of voronoi zoom that allows you to specify a replacement biome when magnifying. Useful if you want a biome to stay the same size.")
public class GenLayerVoronoiZoomSubstitute extends GenLayer {
    private Int2IntOpenHashMap substitutions = new Int2IntOpenHashMap();
    @ScriptMethodDocumentation(args = "long, GenLayer", usage = "base layer seed, parent layer", notes = "Constructs the layer with its base seed and parent layer")
    public GenLayerVoronoiZoomSubstitute(long p_i2133_1_, GenLayer p_i2133_3_)
    {
        super(p_i2133_1_);
        super.parent = p_i2133_3_;
    }
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight)
    {
        areaX = areaX - 2;
        areaY = areaY - 2;
        int i = areaX >> 2;
        int j = areaY >> 2;
        int k = (areaWidth >> 2) + 3;
        int l = (areaHeight >> 2) + 3;
        int[] aint = this.parent.getInts(i, j, k, l);
        int i1 = (k - 1) << 2;
        int j1 = (l - 1) << 2;
        int[] aint1 = IntCache.getIntCache(i1 * j1);

        Int2IntOpenHashMap preserveOffsets = new Int2IntOpenHashMap();

        for (int k1 = 0; k1 < l - 1; ++k1)
        {
            int l1 = 0;
            int i2 = aint[l1 + 0 + (k1 + 0) * k];

            for (int j2 = aint[l1 + 0 + (k1 + 1) * k]; l1 < k - 1; ++l1)
            {
                this.initChunkSeed((long)((l1 + i) << 2), (long)((k1 + j) << 2));
                double d1 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D;
                double d2 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D;
                this.initChunkSeed((long)(l1 + i + 1 << 2), (long)(k1 + j << 2));
                double d3 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
                double d4 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D;
                this.initChunkSeed((long)(l1 + i << 2), (long)(k1 + j + 1 << 2));
                double d5 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D;
                double d6 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
                this.initChunkSeed((long)(l1 + i + 1 << 2), (long)(k1 + j + 1 << 2));
                double d7 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
                double d8 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
                int k2 = aint[l1 + 1 + (k1 + 0) * k];
                int l2 = aint[l1 + 1 + (k1 + 1) * k];

                int preserveI3 = Math.max(0, Math.min(3, (int)Math.round(d2)));
                int preserveK3 = Math.max(0, Math.min(3, (int)Math.round(d1)));

                if (substitutions.containsKey(i2))
                {
                    preserveOffsets.put(k1 * (k - 1) + l1, preserveI3 * 4 + preserveK3);
                }

                for (int i3 = 0; i3 < 4; ++i3)
                {
                    int j3 = ((k1 << 2) + i3) * i1 + (l1 << 2);
                    for (int k3 = 0; k3 < 4; ++k3)
                    {
                        double d9 = ((double)i3 - d2) * ((double)i3 - d2) + ((double)k3 - d1) * ((double)k3 - d1);
                        double d10 = ((double)i3 - d4) * ((double)i3 - d4) + ((double)k3 - d3) * ((double)k3 - d3);
                        double d11 = ((double)i3 - d6) * ((double)i3 - d6) + ((double)k3 - d5) * ((double)k3 - d5);
                        double d12 = ((double)i3 - d8) * ((double)i3 - d8) + ((double)k3 - d7) * ((double)k3 - d7);

                        int winner;
                        if (d9 < d10 && d9 < d11 && d9 < d12)
                            winner = i2;
                        else if (d10 < d9 && d10 < d11 && d10 < d12)
                            winner = k2;
                        else if (d11 < d9 && d11 < d10 && d11 < d12)
                            winner = j2;
                        else
                            winner = l2;

                        aint1[j3] = substitutions.getOrDefault(winner, winner);
                        j3++;
                    }
                }

                i2 = k2;
                j2 = l2;
            }
        }

        int[] aint2 = IntCache.getIntCache(areaWidth * areaHeight);
        for (int l3 = 0; l3 < areaHeight; ++l3)
        {
            System.arraycopy(aint1, (l3 + (areaY & 3)) * i1 + (areaX & 3), aint2, l3 * areaWidth, areaWidth);
        }

        for (Int2IntOpenHashMap.Entry entry : preserveOffsets.int2IntEntrySet())
        {
            int cellIndex = entry.getIntKey();
            int k1 = cellIndex / (k - 1);
            int l1 = cellIndex % (k - 1);
            int preserveI3 = entry.getIntValue() / 4;
            int preserveK3 = entry.getIntValue() % 4;

            int i2 = aint[l1 + 0 + (k1 + 0) * k];


            int aint2X = (l1 << 2) + preserveK3 - (areaX & 3);
            int aint2Z = (k1 << 2) + preserveI3 - (areaY & 3);

            if (aint2X >= 0 && aint2X < areaWidth && aint2Z >= 0 && aint2Z < areaHeight)
            {
                aint2[aint2X + aint2Z * areaWidth] = i2;
            }
        }

        return aint2;
    }
    @ScriptArgs(args = {ArgType.NON_NULL_BIOME_ID, ArgType.NON_NULL_BIOME_ID})
    @ScriptMethodDocumentation(usage = "target biome id, replacement biome id", notes = "Sets a replacement biome that will be placed instead of the target biome during magnification.")
    public void addSubstitution(int target, int substitute) {
        substitutions.put(target, substitute);
    }
}
