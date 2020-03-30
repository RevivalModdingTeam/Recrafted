package dev.revivalmoddingteam.recrafted.common.blocks.plant;

import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.handler.Registry;
import dev.revivalmoddingteam.recrafted.util.helper.ModHelper;
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
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public class PlantBlock extends BushBlock implements Plant {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
    public static final BooleanProperty FROZEN = BooleanProperty.create("frozen");

    protected final Settings settings;

    public PlantBlock(String name, Settings settings) {
        super(Properties.create(Material.PLANTS).sound(SoundType.PLANT));
        this.setRegistryName(Recrafted.makeResource(name));
        this.settings = settings;
        this.setDefaultState(this.getStateContainer().getBaseState().with(AGE, 0).with(FROZEN, false));
        this.makeBlockItem();
    }

    public void makeBlockItem() {
        Registry.EventListener.registerBlockItem(this);
    }

    public void tryGrow(BlockState state, World world, BlockPos pos, Random random) {
        if(!isMaxAge(state) && random.nextFloat() <= 0.1F) {
            world.setBlockState(pos, state.with(AGE, getAge(state) + 1));
        }
    }

    @Override
    public void randomTick(BlockState state, World worldIn, BlockPos pos, Random random) {
        Season season = WorldCapFactory.getData(worldIn).getSeasonData().getSeason();
        if(!season.isWinter()) {
            if(state.get(FROZEN)) {
                if(worldIn.rand.nextFloat() <= settings.recoveryChance) {
                    worldIn.setBlockState(pos, state.with(FROZEN, false).with(AGE, 0));
                } else worldIn.setBlockState(pos, Blocks.DEAD_BUSH.getDefaultState());
            } else this.tryGrow(state, worldIn, pos, random);
        } else {
            if(!state.get(FROZEN) && random.nextFloat() <= 0.1F) {
                worldIn.setBlockState(pos, state.with(FROZEN, true));
            }
        }
        super.randomTick(state, worldIn, pos, random);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote && isMaxAge(state)) {
            Season season = WorldCapFactory.getData(worldIn).getSeasonData().getSeason();
            ItemStack stack = settings.product.get();
            stack.setCount(1 + worldIn.rand.nextInt(Math.min(63, season.isFall() ? 2 * stack.getCount() : stack.getCount())));
            ModHelper.spawnEntityWithRandomMotion(worldIn, new ItemEntity(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copy()), 10);
            worldIn.setBlockState(pos, state.with(AGE, 0));
        }
        return true;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return settings.isEmptyShape() ? VoxelShapes.empty() : VoxelShapes.fullCube();
    }

    @Override
    public boolean canPlaceOn(World world, BlockState state, BlockPos pos) {
        return settings.farmlandRequired ? state.getBlock() == Blocks.FARMLAND : super.isValidGround(state, world, pos);
    }

    @Override
    public void createPlant(World world, BlockPos pos) {
        world.setBlockState(pos, getDefaultState());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE).add(FROZEN);
    }

    public boolean isMaxAge(BlockState state) {
        return state.get(AGE) == 3;
    }

    public int getAge(BlockState state) {
        return state.get(AGE);
    }

    public static class Settings {

        private boolean farmlandRequired;
        private boolean emptyShape;
        private Supplier<ItemStack> product;
        private double recoveryChance;

        public Settings requiresFarmLand() {
            this.farmlandRequired = true;
            return this;
        }

        public Settings emptyShape() {
            this.emptyShape = true;
            return this;
        }

        public Settings product(Supplier<ItemStack> product) {
            this.product = Objects.requireNonNull(product);
            return this;
        }

        public Settings recoveryChance(double recoveryChance) {
            this.recoveryChance = recoveryChance;
            return this;
        }

        public boolean isFarmlandRequired() {
            return farmlandRequired;
        }

        public boolean isEmptyShape() {
            return emptyShape;
        }

        public Supplier<ItemStack> getProduct() {
            return product;
        }

        public double getRecoveryChance() {
            return recoveryChance;
        }
    }
}
