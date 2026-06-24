package com.bluefire.dctweaks.mixins;

import com.bloodnbonesgaming.dimensionalcontrol.world.mapgen.layer.GenLayerTouching;
import com.bloodnbonesgaming.dimensionalcontrol.world.mapgen.layer.GenLayerTouchingData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GenLayerTouching.class, remap = true)
public class GenLayerTouchingMixin extends GenLayer {
   private Map<Integer, List<GenLayerTouchingData>> dataListl = new HashMap<>();

   @Inject(
      method = "Lcom/bloodnbonesgaming/dimensionalcontrol/world/mapgen/layer/GenLayerTouching;addData([I[I[IIII)V",
      cancellable = true,
      at = @At("HEAD"),
      remap = false
   )
   public void addDataMixin(int[] biomes, int[] whitelist, int[] blacklist, int requiredCount, int replacement, int chance, CallbackInfo ci) {
      for (int biome : biomes) {
         if (!this.dataListl.containsKey(biome)) {
            this.dataListl.put(biome, new ArrayList<>());
         }

         this.dataListl.get(biome).add(new GenLayerTouchingData(new int[]{biome}, whitelist, blacklist, requiredCount, replacement, chance));
      }

      ci.cancel();
   }

   public GenLayerTouchingMixin(long p_i2125_1_) {
      super(p_i2125_1_);
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
      int[] returnInts = IntCache.getIntCache(width * depth);

      for (int z = 0; z < depth; z++) {
         for (int x = 0; x < width; x++) {
            this.initChunkSeed(x + chunkX, z + chunkZ);
            int biome = parentInts[x + 1 + (z + 1) * (width + 2)];
            if (this.dataListl.containsKey(biome)) {
               int down = parentInts[x + 1 + (z + 1 - 1) * (width + 2)];
               int right = parentInts[x + 1 + 1 + (z + 1) * (width + 2)];
               int left = parentInts[x + 1 - 1 + (z + 1) * (width + 2)];
               int up = parentInts[x + 1 + (z + 1 + 1) * (width + 2)];
               int downRight = parentInts[x + 1 + 1 + (z + 1 - 1) * (width + 2)];
               int downLeft = parentInts[x + 1 - 1 + (z + 1 - 1) * (width + 2)];
               int upRight = parentInts[x + 1 + 1 + (z + 1 + 1) * (width + 2)];
               int upLeft = parentInts[x + 1 - 1 + (z + 1 + 1) * (width + 2)];

               for (GenLayerTouchingData data : this.dataListl.get(biome)) {
                  int count = 0;
                  int[] neighbors = {down, right, up, left, downRight, downLeft, upRight, upLeft};
                  boolean sufficient = false;
                  for (int i = 0; i < neighbors.length; i++) {
                     if (data.replace(neighbors[i])) count++;
                     if (count == data.getRequiredCount()) {
                        sufficient = true;
                        break;
                     }
                     int remaining = (neighbors.length - 1) - i;
                     if (count + remaining < data.getRequiredCount()) break;
                  }
                  if (sufficient) {
                     if (data.getChance() <= 0 || this.nextInt(data.getChance()) == 0) {
                        biome = data.getReplacement();
                        break;
                     }
                  }
               }
            }

            returnInts[x + z * width] = biome;
         }
      }

      cir.setReturnValue(returnInts);
   }
}
