package com.bluefire.dctweaks.mixins;

import com.bloodnbonesgaming.dimensionalcontrol.world.biomeprovider.BiomeProviderCustomMedium;
import com.bluefire.dctweaks.genlayer.GenLayerClamp;
import com.bluefire.dctweaks.genlayer.GenLayerVoronoiZoomSubstitute;
import com.bluefire.dctweaks.genlayer.GenLayerZoomSubstitute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = BiomeProviderCustomMedium.class, remap = false)
public class BiomeProviderCustomMediumMixin {
    @Shadow
    private Map<String, Class> classKeywords;
    @Inject(method = "<init>()V", at = @At("TAIL"), remap = false)
    private void onInit(CallbackInfo ci) {
        this.classKeywords.put("GenLayerClamp", GenLayerClamp.class);
        this.classKeywords.put("GenLayerVoronoiZoomSubstitute", GenLayerVoronoiZoomSubstitute.class);
        this.classKeywords.put("GenLayerZoomSubstitute", GenLayerZoomSubstitute.class);
    }
}
