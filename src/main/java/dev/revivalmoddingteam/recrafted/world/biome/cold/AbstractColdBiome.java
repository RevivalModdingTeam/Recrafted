package dev.revivalmoddingteam.recrafted.world.biome.cold;

import dev.revivalmoddingteam.recrafted.world.biome.BiomeType;
import dev.revivalmoddingteam.recrafted.world.biome.RecraftedBiome;

public abstract class AbstractColdBiome extends RecraftedBiome {

    public AbstractColdBiome(Builder builder) {
        super(BiomeType.COLD, builder);
    }
}
