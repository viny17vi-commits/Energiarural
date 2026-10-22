package com.energiarural;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(EnergiaRural.MODID)
public class EnergiaRural {
    public static final String MODID = "energiarural";

    public EnergiaRural(IEventBus modBus) {
        ModRegistry.register(modBus);
        modBus.addListener(ModRegistry::registerCapabilities);
    }
}
