package com.girouxdudes.dctweaks.core;

import com.google.common.collect.Lists;
import java.util.List;
import zone.rong.mixinbooter.ILateMixinLoader;

public class LateLoader implements ILateMixinLoader {
   public List<String> getMixinConfigs() {
      return Lists.newArrayList(new String[]{"mixins.mods.dctweaks.json"});
   }
}
