package dev.revivalmoddingteam.recrafted.world.biome.mild;

import dev.revivalmoddingteam.recrafted.world.biome.BiomeType;
import dev.revivalmoddingteam.recrafted.world.biome.RecraftedBiome;

public abstract class AbstractMildBiome extends RecraftedBiome {

    public AbstractMildBiome(Builder builder) {
        super(BiomeType.MILD, builder);
    }
}
