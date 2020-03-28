package dev.revivalmoddingteam.recrafted.common.blocks.plant;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class AdvancedPlantBlock extends PlantBlock {

    public static final IntegerProperty PLANT_AGE = IntegerProperty.create("plant_age", 0, 3);

    public AdvancedPlantBlock(String name, Settings settings) {
        super(name, settings);
        this.setDefaultState(this.getStateContainer().getBaseState().with(AGE, 0).with(PLANT_AGE, 0).with(FROZEN, false));
    }

    @Override
    public void tryGrow(BlockState state, World world, BlockPos pos, Random random) {
        if(state.get(PLANT_AGE) < 3) {
            if(random.nextFloat() <= 0.1F) {
                world.setBlockState(pos, state.with(PLANT_AGE, state.get(PLANT_AGE) + 1));
            }
        } else super.tryGrow(state, world, pos, random);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PLANT_AGE);
        super.fillStateContainer(builder);
    }
}
