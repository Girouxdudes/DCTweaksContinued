package com.girouxdudes.dctweaks.mixins;

import com.bloodnbonesgaming.dimensionalcontrol.config.ConfigurationManager;
import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import com.girouxdudes.dctweaks.genlayer.GenLayerCacheRegistry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ConfigurationManager.class, remap = false)
public class ConfigurationManagerMixin {
    @ScriptMethodDocumentation(args = "String, int[]", usage = "cached biome provider, derived dimensions", notes = "Used to define a group of dimensions that share initial genlayers")
    public void addCacheGroup(String provider, int[] dimensions) {
        GenLayerCacheRegistry.cacheGroups.put(provider, dimensions);
    }
}
