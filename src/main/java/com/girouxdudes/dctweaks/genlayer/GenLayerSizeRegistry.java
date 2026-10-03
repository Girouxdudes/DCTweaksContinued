package com.girouxdudes.dctweaks.genlayer;

import com.bloodnbonesgaming.dimensionalcontrol.world.mapgen.layer.*;
import net.minecraft.world.gen.layer.*;

import java.util.HashMap;
import java.util.Map;

public class GenLayerSizeRegistry {
    public static final Map<Class<? extends GenLayer>, Integer> additivePadding = new HashMap();
    public static final Map<Class<? extends GenLayer>, Integer> zoomFactor = new HashMap();

    static {
        //Vanilla
        additivePadding.put(GenLayerAddIsland.class, 1);
        additivePadding.put(GenLayerAddMushroomIsland.class, 1);
        additivePadding.put(GenLayerAddSnow.class, 1);
        additivePadding.put(GenLayerBiomeEdge.class, 1);
        additivePadding.put(GenLayerDeepOcean.class, 1);
        additivePadding.put(GenLayerEdge.class, 1);
        additivePadding.put(GenLayerFuzzyZoom.class, 1);
        additivePadding.put(GenLayerHills.class, 1);
        additivePadding.put(GenLayerRareBiome.class, 1);
        additivePadding.put(GenLayerRemoveTooMuchOcean.class, 1);
        additivePadding.put(GenLayerRiver.class, 1);
        additivePadding.put(GenLayerShore.class, 1);
        additivePadding.put(GenLayerSmooth.class, 1);
        additivePadding.put(GenLayerVoronoiZoom.class, 1);
        additivePadding.put(GenLayerZoom.class, 1);

        zoomFactor.put(GenLayerFuzzyZoom.class, 2);
        zoomFactor.put(GenLayerVoronoiZoom.class, 4);
        zoomFactor.put(GenLayerZoom.class, 2);

        //Dimensional Control
        additivePadding.put(GenLayerAddMushroomIslandDC.class, 1);
        additivePadding.put(GenLayerBiomeEdgeDC.class, 1);
        additivePadding.put(GenLayerDeepOceanDC.class, 1);
        additivePadding.put(GenLayerHillsDC.class, 1);
        additivePadding.put(GenLayerRemoveTooMuchOcean.class, 1);
        additivePadding.put(GenLayerShoreDC.class, 1);
        additivePadding.put(GenLayerTouching.class, 1);

        //In-house
        additivePadding.put(GenLayerVoronoiZoomSubstitute.class, 1);
        additivePadding.put(GenLayerZoomSpecial.class, 1);
        additivePadding.put(GenLayerZoomFilter.class, 1);

        zoomFactor.put(GenLayerVoronoiZoomSubstitute.class, 4);
        zoomFactor.put(GenLayerZoomSpecial.class, 2);
        zoomFactor.put(GenLayerZoomFilter.class, 2);

    }
}
