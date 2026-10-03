package com.girouxdudes.dctweaks.mixins;

import com.girouxdudes.dctweaks.util.IGenLayer;
import net.minecraft.world.gen.layer.GenLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GenLayer.class)
public class GenLayerMixin implements IGenLayer {
    @Shadow
    protected GenLayer parent;

    public GenLayer getParent() {
        return parent;
    }
}
