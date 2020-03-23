package dev.revivalmoddingteam.recrafted.world.feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class DefaultTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {

    public DefaultTreeFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> function, boolean updateBlocks) {
        super(function, updateBlocks);
    }

    @Override
    protected boolean place(Set<BlockPos> changedBlocks, IWorldGenerationReader worldIn, Random rand, BlockPos position, MutableBoundingBox boundsIn) {
        return false;
    }
}
