package com.exosomnia.exolib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExoLib.MODID)
public class ExoLib
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "exolib";
    public static final Registry REGISTRY = new Registry();

    public ExoLib()
    {
        REGISTRY.registerCommon();
        REGISTRY.registerObjects(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
