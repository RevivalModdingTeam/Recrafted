package dev.revivalmoddingteam.recrafted.world.biome;

import net.minecraft.world.biome.Biome;

public class RecraftedBiome extends Biome {

    protected final BiomeType type;

    public RecraftedBiome(BiomeType type, Builder builder) {
        super(builder);
        this.type = type;
    }
}
