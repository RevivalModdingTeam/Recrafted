package dev.revivalmoddingteam.recrafted.world.biome.freezing;

import dev.revivalmoddingteam.recrafted.world.biome.BiomeType;
import dev.revivalmoddingteam.recrafted.world.biome.RecraftedBiome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public abstract class AbstractFreezingBiome extends RecraftedBiome {

    public AbstractFreezingBiome(Builder builder) {
        super(BiomeType.FREEZING, builder);
    }

    @Override
    public boolean doesWaterFreeze(IWorldReader worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean doesSnowGenerate(IWorldReader worldIn, BlockPos pos) {
        return true;
    }
}
