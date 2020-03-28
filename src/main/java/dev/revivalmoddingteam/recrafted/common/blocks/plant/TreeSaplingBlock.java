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
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Random;
import java.util.function.Supplier;

public class TreeSaplingBlock extends PlantBlock {

    private final LazyLoadBase<Tree> tree;

    public TreeSaplingBlock(String name, Supplier<Tree> tree, Settings settings) {
        super(name, settings);
        this.setRegistryName(Recrafted.makeResource(name));
        this.tree = new LazyLoadBase<>(tree);
        Registry.EventListener.registerBlockItem(this);
    }

    @Override
    public void tryGrow(BlockState state, World world, BlockPos pos, Random random) {
        if(isMaxAge(state)) {
            tree.getValue().spawn(world, pos, state, random);
            return;
        }
        super.tryGrow(state, world, pos, random);
    }
}
