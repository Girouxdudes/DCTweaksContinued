package com.girouxdudes.dctweaks.mixins;

import com.bloodnbonesgaming.dimensionalcontrol.world.mapgen.layer.GenLayerCombineWhitelist;
import com.girouxdudes.dctweaks.util.IHasParent2;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = GenLayerCombineWhitelist.class, remap = false)
public class GenLayerCombineWhitelistMixin extends GenLayer implements IHasParent2 {
    @Shadow(remap = false)
    protected GenLayer parent2;

    @Shadow(remap = false)
    protected int[] whitelist;

    public GenLayerCombineWhitelistMixin(long p_i2125_1_) {
        super(p_i2125_1_);
    }

    public GenLayer getParent2() {
        return parent2;
    }


    @Overwrite
    public int[] getInts(int chunkX, int chunkZ, int width, int depth) {
        int[] parentInts = this.parent.getInts(chunkX, chunkZ, width, depth);
        int[] parentInts2 = this.parent2.getInts(chunkX, chunkZ, width, depth);
        int[] returnInts = IntCache.getIntCache(width * depth);
        for (int z = 0; z < depth; z++) {
            for (int x =0; x < width; x++) {
                initChunkSeed((x + chunkX), (z + chunkZ));
                int parentIndex = x + z * width;
                int currentBiome = parentInts[parentIndex];
                int currentBiome2 = parentInts2[parentIndex];
                returnInts[x + z * width] = inWhitelist(currentBiome) ? currentBiome : currentBiome2;
            }
        }
        return returnInts;
    }

    private boolean inWhitelist(int id) {
        for (int i : this.whitelist) {
            if (id == i)
                return true;
        }
        return false;
    }

}
