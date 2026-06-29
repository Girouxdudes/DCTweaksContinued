package com.girouxdudes.dctweaks.mixins;

import com.bloodnbonesgaming.dimensionalcontrol.world.biomeprovider.BiomeProviderCustomMedium;
import com.girouxdudes.dctweaks.genlayer.*;
import com.girouxdudes.dctweaks.util.IBiomeProviderCustomMedium;
import net.minecraft.world.gen.layer.GenLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = BiomeProviderCustomMedium.class)
public class BiomeProviderCustomMediumMixin implements IBiomeProviderCustomMedium {
    @Shadow private long seed;


    @Shadow
    private Map<String, Class> classKeywords;
    @Inject(method = "<init>()V", at = @At("TAIL"), remap = false)
    private void onInit(CallbackInfo ci) {
        this.classKeywords.put("GenLayerClamp", GenLayerClamp.class);
        this.classKeywords.put("GenLayerVoronoiZoomSubstitute", GenLayerVoronoiZoomSubstitute.class);
        this.classKeywords.put("GenLayerZoomSubstitute", GenLayerZoomSubstitute.class);
        this.classKeywords.put("GenLayerCacheStore", GenLayerCacheStore.class);
        this.classKeywords.put("GenLayerCacheRetrieve", GenLayerCacheRetrieve.class);
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public Map<String, Class> getClassKeywords() {
        return this.classKeywords;
    }

}
