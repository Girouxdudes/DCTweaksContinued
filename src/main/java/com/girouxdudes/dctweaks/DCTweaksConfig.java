package com.girouxdudes.dctweaks;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = "dctweaks")
@EventBusSubscriber(modid = "dctweaks")
public class DCTweaksConfig {
   private static final String config = "dctweaks.config.";

   @SubscribeEvent
   public static void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("dctweaks")) {
         ConfigManager.sync("dctweaks", Type.INSTANCE);
      }
   }
}
