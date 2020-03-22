package dev.revivalmoddingteam.recrafted.common.blocks;


import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.handler.Registry;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import dev.revivalmoddingteam.recrafted.world.season.Season;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.Supplier;

public class GroundFruitBlock extends BushBlock {

    public static final IntegerProperty PLANT_AGE = IntegerProperty.create("plant", 0, 3);
    public static final IntegerProperty FRUIT_AGE = IntegerProperty.create("fruit", 0, 3);
    public static final BooleanProperty IS_FROZEN = BooleanProperty.create("frozen");

    private final Supplier<ItemStack> stackSupplier;
    private final boolean requiresFarmland;

    public GroundFruitBlock(String key, Supplier<ItemStack> stackSupplier) {
        this(key, false, stackSupplier);
    }

    public GroundFruitBlock(String key, boolean requiresFarmland, Supplier<ItemStack> stackSupplier) {
        super(Properties.create(Material.PLANTS).sound(SoundType.PLANT).tickRandomly());
        this.setRegistryName(Recrafted.getResource(key));
        this.stackSupplier = stackSupplier;
        this.requiresFarmland = requiresFarmland;
        this.setDefaultState(this.getStateContainer().getBaseState().with(PLANT_AGE, 0).with(FRUIT_AGE, 0).with(IS_FROZEN, false));
        Registry.EventListener.registerBlockItem(this);
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return requiresFarmland ? state.getBlock() == Blocks.FARMLAND : super.isValidGround(state, worldIn, pos);
    }

    @Override
    public void randomTick(BlockState state, World worldIn, BlockPos pos, Random random) {
        Season season = WorldCapFactory.getData(worldIn).getSeasonData().getSeason();
        if(!season.isWinter()) {
            if(isFrozen(state)) {
                if(random.nextFloat() <= 0.15F) {
                    worldIn.setBlockState(pos, state.with(IS_FROZEN, false).with(FRUIT_AGE, 0));
                }
            } else if(isPlantMature(state)) {
                if(!isFruitMature(state) && random.nextFloat() <= 0.1F) {
                    addAgeProperty(FRUIT_AGE, state, worldIn, pos);
                }
            } else {
                if(random.nextFloat() <= 0.1F) {
                    addAgeProperty(PLANT_AGE, state, worldIn, pos);
                }
            }
        } else {
            if(!isFrozen(state) && random.nextFloat() <= 0.15F) {
                worldIn.setBlockState(pos, state.with(IS_FROZEN, true));
            }
        }
        super.randomTick(state, worldIn, pos, random);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote) {
            if(!isFrozen(state) && isFruitMature(state)) {
                ItemStack stack = stackSupplier.get();
                stack.setCount(1 + worldIn.rand.nextInt(stack.getCount()));
                worldIn.addEntity(new ItemEntity(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
                worldIn.setBlockState(pos, state.with(FRUIT_AGE, 0));
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        if(!isFrozen(state) && isFruitMature(state)) {
            if(!worldIn.getWorld().isRemote) {
                ItemStack stack = stackSupplier.get();
                stack.setCount(1 + worldIn.getWorld().rand.nextInt(stack.getCount()));
                worldIn.addEntity(new ItemEntity(worldIn.getWorld(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
            }
        }
    }

    public void addAgeProperty(IntegerProperty property, BlockState state, World world, BlockPos pos) {
        if(state.get(property) < 3) {
            world.setBlockState(pos, state.with(property, state.get(property) + 1));
        }
    }

    public boolean isPlantMature(BlockState state) {
        return getPlantAge(state) == 3;
    }

    public boolean isFruitMature(BlockState state) {
        return getFruitAge(state) == 3;
    }

    public int getPlantAge(BlockState state) {
        return state.get(PLANT_AGE);
    }

    public int getFruitAge(BlockState state) {
        return state.get(FRUIT_AGE);
    }

    public boolean isFrozen(BlockState state) {
        return state.get(IS_FROZEN);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PLANT_AGE);
        builder.add(FRUIT_AGE);
        builder.add(IS_FROZEN);
    }
}
