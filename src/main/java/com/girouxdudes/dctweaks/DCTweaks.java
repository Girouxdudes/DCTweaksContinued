package com.girouxdudes.dctweaks;

import com.bloodnbonesgaming.lib.util.script.ScriptDocumentationHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "dctweaks",
   name = "Dimensional Control Tweaks",
   version = "1.0",
   dependencies = "required-after:mixinbooter@[8.0,);required-after:dimensionalcontrol"
)
public class DCTweaks {
   public static final String MODID = "dctweaks";
   @Instance("dctweaks")
   public static DCTweaks instance;

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
   }

   @EventHandler
   public void initRegistries(FMLInitializationEvent event) {
   }

   @EventHandler
   public void loadComplete(FMLLoadCompleteEvent event) {
      ScriptDocumentationHandler.printAnnotatedDocumentation("com.girouxdudes.dctweaks");
   }
}
