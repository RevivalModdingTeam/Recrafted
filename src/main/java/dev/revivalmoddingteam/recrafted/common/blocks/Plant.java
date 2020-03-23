package dev.revivalmoddingteam.recrafted.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Plant {

    boolean canPlaceOn(World world, BlockState state, BlockPos pos);

    void createPlant(World world, BlockPos pos);
}
