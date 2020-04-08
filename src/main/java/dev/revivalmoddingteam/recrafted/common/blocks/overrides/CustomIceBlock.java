package dev.revivalmoddingteam.recrafted.common.blocks.overrides;

import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.Random;

public class CustomIceBlock extends IceBlock {

    public CustomIceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
        if (worldIn.getLightFor(LightType.BLOCK, pos) > 11 - state.getOpacity(worldIn, pos) || worldIn.getBiome(pos).getTemperature(pos) >= 0.15F) {
            this.turnIntoWater(state, worldIn, pos);
        }
    }
}
