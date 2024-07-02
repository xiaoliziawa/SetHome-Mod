package com.packge.sethome;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(HomeMod.MOD_ID)
public class HomeMod {
    public static final String MOD_ID = "homemod";

    public HomeMod() {
        HomeConfig.register();
        MinecraftForge.EVENT_BUS.register(this);
    }
}
