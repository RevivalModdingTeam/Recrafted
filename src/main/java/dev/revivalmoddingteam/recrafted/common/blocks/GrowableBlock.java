package dev.revivalmoddingteam.recrafted.common.blocks;

import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.handler.Registry;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import dev.revivalmoddingteam.recrafted.world.season.Season;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.Supplier;

public class GrowableBlock extends BushBlock implements Plant {

    public static final IntegerProperty AGE_PROPERTY = IntegerProperty.create("age", 0, 3);
    public static final BooleanProperty FROZEN_PROPERTY = BooleanProperty.create("frozen");

    protected final Supplier<ItemStack> growableItem;
    private final boolean requiresFarmland;
    private boolean emptyShape;

    public GrowableBlock(String key, Supplier<ItemStack> growableItem) {
        this(key, false, growableItem);
    }

    public GrowableBlock(String key, boolean requiresFarmland, Supplier<ItemStack> growableItem) {
        super(Properties.create(Material.PLANTS).sound(SoundType.PLANT).tickRandomly());
        this.setRegistryName(Recrafted.makeResource(key));
        this.growableItem = growableItem;
        this.requiresFarmland = requiresFarmland;
        setDefaultState(getStateContainer().getBaseState().with(AGE_PROPERTY, 0).with(FROZEN_PROPERTY, false));
        Registry.EventListener.registerBlockItem(this);
    }

    public GrowableBlock makePassable() {
        this.emptyShape = true;
        return this;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return emptyShape ? VoxelShapes.empty() : VoxelShapes.fullCube();
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return requiresFarmland ? state.getBlock() == Blocks.FARMLAND : super.isValidGround(state, worldIn, pos);
    }

    @Override
    public boolean canPlaceOn(World world, BlockState state, BlockPos pos) {
        return isValidGround(state, world, pos);
    }

    @Override
    public void createPlant(World world, BlockPos pos) {
        world.setBlockState(pos, getDefaultState());
    }

    @Override
    public void randomTick(BlockState state, World worldIn, BlockPos pos, Random random) {
        Season season = WorldCapFactory.getData(worldIn).getSeasonData().getSeason();
        if(!season.isWinter()) {
            if(!isMaxAge(state) && random.nextInt(10) == 0) {
                worldIn.setBlockState(pos, state.with(AGE_PROPERTY, getCurrentAge(state) + 1));
            } else if(state.get(FROZEN_PROPERTY)) {
                worldIn.setBlockState(pos, Blocks.DEAD_BUSH.getDefaultState(), 3);
            }
        } else {
            if(!state.get(FROZEN_PROPERTY) && random.nextInt(10) == 0) {
                worldIn.setBlockState(pos, state.with(FROZEN_PROPERTY, true));
            }
        }
        super.randomTick(state, worldIn, pos, random);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote && state.get(FROZEN_PROPERTY)) {
            if(worldIn.rand.nextInt(10) == 0) {
                player.addItemStackToInventory(new ItemStack(Blocks.ICE));
                worldIn.setBlockState(pos, Blocks.DEAD_BUSH.getDefaultState(), 3);
                return true;
            }
            return false;
        } else {
            if(!worldIn.isRemote && isMaxAge(state)) {
                Season season = WorldCapFactory.getData(worldIn).getSeasonData().getSeason();
                ItemStack stack = growableItem.get();
                int droppedAmount = stack.getCount();
                if(season.isFall()) {
                    droppedAmount *= 2;
                }
                droppedAmount = droppedAmount > 64 ? 64 : droppedAmount;
                stack.setCount(1 + worldIn.rand.nextInt(droppedAmount));
                player.addItemStackToInventory(stack);
                worldIn.setBlockState(pos, state.with(AGE_PROPERTY, 0));
            }
        }
        return true;
    }

    public int getCurrentAge(BlockState state) {
        return state.get(AGE_PROPERTY);
    }

    public boolean isMaxAge(BlockState state) {
        return getCurrentAge(state) == 3;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE_PROPERTY);
        builder.add(FROZEN_PROPERTY);
    }
}
