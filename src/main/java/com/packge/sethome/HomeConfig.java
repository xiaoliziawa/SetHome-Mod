package com.packge.sethome;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class HomeConfig {
    public static ForgeConfigSpec.IntValue MAX_HOMES;

    public static void register() {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("Home Settings");

        MAX_HOMES = SERVER_BUILDER
                .comment("Maximum number of homes a player can set")
                .defineInRange("maxHomes", 5, 1, Integer.MAX_VALUE);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
    }
}
