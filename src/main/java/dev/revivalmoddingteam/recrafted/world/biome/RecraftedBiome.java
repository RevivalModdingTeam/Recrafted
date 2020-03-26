package dev.revivalmoddingteam.recrafted.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class RecraftedBiome extends Biome {

    protected final BiomeType type;

    public RecraftedBiome(BiomeType type, Builder builder) {
        super(builder);
        this.type = type;
    }

    @Override
    public float getTemperature(BlockPos pos) {
        return type.averageTemperature + super.getTemperature(pos);
    }
}
