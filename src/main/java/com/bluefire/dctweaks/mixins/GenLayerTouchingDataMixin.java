package com.bluefire.dctweaks.mixins;

import com.bloodnbonesgaming.dimensionalcontrol.world.mapgen.layer.GenLayerTouchingData;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GenLayerTouchingData.class, remap = false)
public class GenLayerTouchingDataMixin {


    @Unique private IntOpenHashSet whitelistSet;
    @Unique private IntOpenHashSet blacklistSet;

    @Inject(
            method = "<init>([I[I[IIII)V",
            at = @At("TAIL"),
            remap = false
    )
    private void onInit(int[] biomes, int[] whitelist, int[] blacklist,
                        int requiredCount, int replacement, int chance,
                        CallbackInfo ci) {
        this.whitelistSet = (whitelist != null) ? new IntOpenHashSet(whitelist) : null;
        this.blacklistSet = (blacklist != null) ? new IntOpenHashSet(blacklist) : null;
    }

    @Overwrite(remap = false)
    public boolean replace(int biome) {
        if (this.whitelistSet == null && this.blacklistSet == null) return true;
        if (this.whitelistSet == null) return !this.blacklistSet.contains(biome);
        if (this.blacklistSet == null) return this.whitelistSet.contains(biome);
        return false;
    }
}