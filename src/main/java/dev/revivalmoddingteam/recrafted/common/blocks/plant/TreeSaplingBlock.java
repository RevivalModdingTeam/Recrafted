package dev.revivalmoddingteam.recrafted.common.blocks.plant;

import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.handler.Registry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.trees.Tree;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Random;

public class TreeSaplingBlock extends BushBlock {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
    private final Tree tree;

    public TreeSaplingBlock(String name, Tree tree) {
        super(Properties.create(Material.PLANTS).sound(SoundType.PLANT).tickRandomly());
        this.setRegistryName(Recrafted.makeResource(name));
        this.setDefaultState(this.getStateContainer().getBaseState().with(AGE, 0));
        this.tree = tree;
        Registry.EventListener.registerBlockItem(this);
    }

    @Override
    public void randomTick(BlockState state, World worldIn, BlockPos pos, Random random) {
        if(random.nextFloat() <= 0.05F) {
            if(state.get(AGE) == 3) {
                if(!ForgeEventFactory.saplingGrowTree(worldIn, random, pos))
                    return;
                tree.spawn(worldIn, pos, state, random);
            } else worldIn.setBlockState(pos, state.with(AGE, state.get(AGE) + 1));
        }
        super.randomTick(state, worldIn, pos, random);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}
