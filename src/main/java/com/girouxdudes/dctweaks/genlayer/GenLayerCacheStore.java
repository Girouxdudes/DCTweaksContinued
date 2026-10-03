package com.girouxdudes.dctweaks.genlayer;

import com.bloodnbonesgaming.lib.util.script.ArgType;
import com.bloodnbonesgaming.lib.util.script.ScriptArgs;
import com.bloodnbonesgaming.lib.util.script.ScriptClassDocumentation;
import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import com.girouxdudes.dctweaks.util.GenLayerScanner;
import net.minecraft.world.gen.layer.GenLayer;

import java.util.HashMap;

@ScriptClassDocumentation(documentationFile = "./config/dimensionalcontrol/documentation/genlayers/GenLayerCacheStore", classExplaination = "Goes at the end of a biome provider script intended to be shared amongst multiple dimensions. Takes a seed and a parent genlayer")
public class GenLayerCacheStore extends GenLayer {

    HashMap<String, GenLayer> secondaryLayers = new HashMap<>();
    public String[] identifiers;

    @ScriptMethodDocumentation(args = "long, GenLayer", usage = "base layer seed, parent layer", notes = "Constructs the layer with its base seed and parent layer")
    public GenLayerCacheStore(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
        GenLayerCacheRegistry.cacheLayer = this;
    }

    @ScriptMethodDocumentation(args = "long, GenLayer, string", usage = "base layer seed, parent layer, split cache identifier", notes = "Constructs the layer with its base seed and parent layer. This variant takes identifier strings to be used in conjunction with splitcache layers. If using multiple identifier strings to resolve multiple splits at the same time, the different splits must be fulled nested within one another. If using nested splits, you must list the child splits (those fulled nested within another split) prior to the parent ones in the array.")
    public GenLayerCacheStore(long seed, GenLayer parent, String[] identifiers) {
        super(seed);
        this.parent = parent;
        GenLayerCacheRegistry.cacheLayer = this;
        this.identifiers = identifiers;
    }

    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        if(identifiers != null) {
            for(String identifier : identifiers) {
                GenLayerCacheRegistry.calculating.get().put(identifier, true);
                GenLayerScanner.CalculateCacheSize(identifier, this, areaWidth, areaX, areaY);
                for (HashMap.Entry<String, GenLayer> entry : secondaryLayers.entrySet()) {
                    GenLayerScanner.CalculateCacheSize(identifier, entry.getValue(), areaWidth, areaX, areaY);
                }
            }
        }

        GenLayerCacheRegistry.cache.set(this.parent.getInts(areaX, areaY, areaWidth, areaHeight));
        for(HashMap.Entry<String, GenLayer> entry : secondaryLayers.entrySet()) {
            GenLayerCacheRegistry.secondaryCaches.get().put(entry.getKey(), entry.getValue().getInts(areaX, areaY, areaWidth, areaHeight));
        }
        //splitcache clear
        if(identifiers != null) {
            for(String identifier : identifiers) {
                GenLayerCacheRegistry.splitCache.get().remove(identifier);
                GenLayerCacheRegistry.calculating.get().remove(identifier);
            }
        }
        return new int[0];
    }

    @ScriptArgs(args = {ArgType.STRING, ArgType.GENLAYER})
    @ScriptMethodDocumentation(usage = "secondary cache name, secondary cache genlayer", notes = "Allows storing additional genlayers in the cache.")
    public void addSecondaryCache(String name, GenLayer genlayer) {
        secondaryLayers.put(name, genlayer);
    }

}
