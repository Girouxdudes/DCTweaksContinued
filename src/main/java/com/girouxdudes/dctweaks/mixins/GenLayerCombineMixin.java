package com.girouxdudes.dctweaks.mixins;

import com.bloodnbonesgaming.dimensionalcontrol.world.mapgen.layer.GenLayerCombine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GenLayerCombine.class, remap = true)
public class GenLayerCombineMixin extends GenLayer {
   private Map<Integer, Map<Integer, Integer>> combinationsl = new HashMap<>();

   @Shadow(remap = false)
   protected GenLayer parent2;
   @Shadow(remap = false)
   protected List<int[]> combinations;

   public GenLayerCombineMixin(long p_i2125_1_) {
      super(p_i2125_1_);
   }

   @Inject(method = "addCombination", cancellable = true, at = @At("HEAD"), remap = false)
   public void addCombinationMixin(int[] combination, CallbackInfo ci) {
      if (!this.combinationsl.containsKey(combination[0])) {
         this.combinationsl.put(combination[0], new HashMap<>());
      }

      this.combinationsl.get(combination[0]).put(combination[1], combination[2]);
      ci.cancel();
   }

   public void initWorldGenSeed(long seed) {
      this.parent.initWorldGenSeed(seed);
      this.parent2.initWorldGenSeed(seed);
      super.initWorldGenSeed(seed);
   }

   public int[] func_75904_a(int chunkX, int chunkZ, int width, int depth) {
      return new int[0];
   }

   @Override
   public int[] getInts(int chunkX, int chunkZ, int width, int depth) {
      return new int[0];
   }

   @Inject(method = "getInts", cancellable = true, at = @At("HEAD"), remap = true)
   public void getIntsMixin(int chunkX, int chunkZ, int width, int depth, CallbackInfoReturnable<int[]> cir) {
      int[] parentInts = this.parent.getInts(chunkX - 1, chunkZ - 1, width + 2, depth + 2);
      int[] parentInts2 = this.parent2.getInts(chunkX - 1, chunkZ - 1, width + 2, depth + 2);
      int[] returnInts = IntCache.getIntCache(width * depth);

      for (int z = 0; z < depth; z++) {
         for (int x = 0; x < width; x++) {
            this.initChunkSeed(x + chunkX, z + chunkZ);
            int parentIndex = x + 1 + (z + 1) * (width + 2);
            int currentBiome = parentInts[parentIndex];
            int currentBiome2 = parentInts2[parentIndex];
            returnInts[x + z * width] = currentBiome;
            if (this.combinationsl.containsKey(currentBiome)) {
               Integer out = this.combinationsl.get(currentBiome).get(currentBiome2);
               if (out != null) {
                  returnInts[x + z * width] = out;
               }
            }
         }
      }

      cir.setReturnValue(returnInts);
   }
}
