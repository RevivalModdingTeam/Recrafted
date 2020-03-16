package dev.revivalmoddingteam.recrafted.world.biome.warm;

import dev.revivalmoddingteam.recrafted.world.biome.BiomeType;
import dev.revivalmoddingteam.recrafted.world.biome.RecraftedBiome;

public abstract class AbstractWarmBiome extends RecraftedBiome {

    public AbstractWarmBiome(Builder builder) {
        super(BiomeType.WARM, builder);
    }
}
