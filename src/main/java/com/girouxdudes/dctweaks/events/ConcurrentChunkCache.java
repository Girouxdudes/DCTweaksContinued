package com.girouxdudes.dctweaks.events;

import com.bloodnbonesgaming.dimensionalcontrol.util.IOHelper;
import com.bloodnbonesgaming.dimensionalcontrol.world.biomeprovider.BiomeProviderCustomMedium;
import com.girouxdudes.dctweaks.genlayer.GenLayerCacheRegistry;
import com.girouxdudes.dctweaks.util.IBiomeProviderCustomMedium;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

//generates all chunks in a proxy group if one of them generates.
@Mod.EventBusSubscriber(modid = "dctweaks")
public class ConcurrentChunkCache {

    private static final ThreadLocal<Boolean> isGenerating = ThreadLocal.withInitial(() -> false);

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getWorld().isRemote) return;
        if (isGenerating.get()) return;

        int dimID = event.getWorld().provider.getDimension();
        Chunk chunk = event.getChunk();

        for (Map.Entry<String, int[]> entry : GenLayerCacheRegistry.cacheGroups.entrySet()) {
            for (int id : entry.getValue()) {
                if(id == dimID) {
                    if(DimensionManager.getWorld(dimID).isChunkGeneratedAt(chunk.x, chunk.z)) return;
                    isGenerating.set(true);

                    BiomeProviderCustomMedium provider = new BiomeProviderCustomMedium();
                    ((IBiomeProviderCustomMedium) provider).setSeed(event.getWorld().getSeed());
                    IOHelper.loadBiomeProvider(entry.getKey(), provider, ((IBiomeProviderCustomMedium) provider).getClassKeywords(), null);
                    GenLayerCacheRegistry.cacheLayer.getInts(chunk.x, chunk.z, 16, 16);

                    for (int proxyID : entry.getValue()) {
                        if(proxyID != dimID) {
                            WorldServer world = DimensionManager.getWorld(proxyID);
                            if(world != null) {
                                world.getChunk(chunk.x, chunk.z);
                            }
                        }
                    }
                    GenLayerCacheRegistry.cache.set(null);
                    GenLayerCacheRegistry.secondaryCaches.get().clear();
                    isGenerating.set(false);
                    return;
                }
            }
        }
        ThreadLocal<GenLayerCacheRegistry.CacheEntry> finalCache = GenLayerCacheRegistry.finalCache;
        finalCache.get().ints = new int[0];
    }
}
