package com.bluefire.dctweaks.genlayer;

import com.bloodnbonesgaming.lib.util.script.ArgType;
import com.bloodnbonesgaming.lib.util.script.ScriptArgs;
import com.bloodnbonesgaming.lib.util.script.ScriptClassDocumentation;
import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

@ScriptClassDocumentation(documentationFile = "./config/dimensionalcontrol/documentation/genlayers/GenLayerZoomSpecial", classExplaination = "This is a special variant of zoom that allows you to specify a replacement biome when magnifying. Useful if you want a biome to stay the same size.")
public class GenLayerZoomSubstitute extends GenLayer {
    private Int2IntOpenHashMap substitutions = new Int2IntOpenHashMap();
    public GenLayerZoomSubstitute(long p_i2134_1_, GenLayer p_i2134_3_)
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
            int j2 = aint[i2 + 0 + (k1 + 0) * k];

            for (int k2 = aint[i2 + 0 + (k1 + 1) * k]; i2 < k - 1; ++i2)
            {
                this.initChunkSeed((long)(i2 + i << 1), (long)(k1 + j << 1));
                int l2 = aint[i2 + 1 + (k1 + 0) * k];
                int i3 = aint[i2 + 1 + (k1 + 1) * k];
                aint1[l1] = j2;

                int bottomLeft = this.selectRandom(new int[] {j2, k2});
                aint1[l1++ + i1] = substitutions.getOrDefault(bottomLeft, bottomLeft);

                int topRight = this.selectRandom(new int[] {j2, l2});
                aint1[l1] = substitutions.getOrDefault(topRight, topRight);

                int bottomRight = this.selectModeOrRandom(j2, l2, k2, i3);
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
    @ScriptArgs(args = {ArgType.NON_NULL_BIOME_ID, ArgType.NON_NULL_BIOME_ID})
    @ScriptMethodDocumentation(usage = "target biome id, replacement biome id", notes = "Sets a replacement biome that will be placed instead of the target biome during magnification.")
    public void addSubstitution(int target, int substitute) {
        substitutions.put(target, substitute);
    }
}
