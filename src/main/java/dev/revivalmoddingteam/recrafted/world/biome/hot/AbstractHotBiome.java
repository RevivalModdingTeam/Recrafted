package dev.revivalmoddingteam.recrafted.world.biome.hot;

import dev.revivalmoddingteam.recrafted.world.biome.BiomeType;
import dev.revivalmoddingteam.recrafted.world.biome.RecraftedBiome;

public abstract class AbstractHotBiome extends RecraftedBiome {

    public AbstractHotBiome(Builder builder) {
        super(BiomeType.HOT, builder);
    }
}
