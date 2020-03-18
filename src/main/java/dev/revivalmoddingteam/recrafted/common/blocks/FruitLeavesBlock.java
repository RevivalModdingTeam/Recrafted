package dev.revivalmoddingteam.recrafted.common.blocks;

import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.handler.Registry;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import dev.revivalmoddingteam.recrafted.world.season.Season;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FruitLeavesBlock extends LeavesBlock {

    public static final IntegerProperty AGE_PROPERTY = IntegerProperty.create("age", 0, 3);
    public static final BooleanProperty FROZEN_PROPERTY = BooleanProperty.create("frozen");

    private final Supplier<ItemStack> fruitFactory;
    private final Predicate<Season> seasonPredicate;

    public FruitLeavesBlock(String key, Supplier<ItemStack> fruitFactory) {
        this(key, fruitFactory, Season::isFall);
    }

    public FruitLeavesBlock(String key, Supplier<ItemStack> fruitFactory, Predicate<Season> dropFruit) {
        super(Properties.create(Material.PLANTS).sound(SoundType.PLANT).tickRandomly());
        setRegistryName(Recrafted.getResource(key));
        setDefaultState(getStateContainer().getBaseState().with(AGE_PROPERTY, 0).with(FROZEN_PROPERTY, false));
        this.fruitFactory = fruitFactory;
        this.seasonPredicate = dropFruit;

        Registry.EventListener.registerBlockItem(this);
    }

    public int getAge(BlockState state) {
        return state.get(AGE_PROPERTY);
    }

    public boolean isMaxAge(BlockState state) {
        return getAge(state) == 3;
    }

    @Override
    public void randomTick(BlockState state, World worldIn, BlockPos pos, Random random) {
        if(worldIn.isRemote) return;
        Season season = WorldCapFactory.getData(worldIn).getSeasonData().getSeason();
        if(!season.isWinter()) {
            if(!isMaxAge(state) && random.nextInt(10) == 0) {
                worldIn.setBlockState(pos, state.with(AGE_PROPERTY, getAge(state) + 1));
            } else if(state.get(FROZEN_PROPERTY)) {
                worldIn.setBlockState(pos, state.with(FROZEN_PROPERTY, false).with(AGE_PROPERTY, 0));
            }
        } else {
            if(!state.get(FROZEN_PROPERTY) && random.nextInt(10) == 0) {
                worldIn.setBlockState(pos, state.with(FROZEN_PROPERTY, true));
            }
        }
        if(!state.get(FROZEN_PROPERTY) && seasonPredicate.test(season)) {
            if(random.nextInt(10) == 0) {
                BlockPos air = pos;
                for(Direction direction : Direction.values()) {
                    if(worldIn.isAirBlock(pos.offset(direction))) {
                        air = pos.offset(direction);
                        break;
                    }
                }
                ItemStack stack = fruitFactory.get();
                int droppedAmount = stack.getCount() * 2;
                droppedAmount = droppedAmount > 64 ? 64 : droppedAmount;
                int n = 1 + random.nextInt(droppedAmount);
                for(int i = 0; i < n; i++) {
                    ItemEntity entity = new ItemEntity(worldIn, air.getX() + random.nextDouble(), air.getY() + 0.9, air.getZ() + random.nextDouble(), new ItemStack(stack.getItem()));
                    entity.setMotion(random.nextDouble()/10 - random.nextDouble()/10, 0, random.nextDouble()/10 - random.nextDouble()/10);
                    worldIn.addEntity(entity);
                }
                worldIn.setBlockState(pos, state.with(AGE_PROPERTY, 0));
            }
        }
        super.randomTick(state, worldIn, pos, random);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote && isMaxAge(state)) {
            Season season = WorldCapFactory.getData(worldIn).getSeasonData().getSeason();
            ItemStack stack = fruitFactory.get();
            int droppedAmount = stack.getCount();
            if(seasonPredicate.test(season)) {
                droppedAmount *= 2;
            }
            droppedAmount = droppedAmount > 64 ? 64 : droppedAmount;
            stack.setCount(1 + worldIn.rand.nextInt(droppedAmount));
            player.addItemStackToInventory(stack);
            worldIn.setBlockState(pos, state.with(AGE_PROPERTY, 0));
        }
        return true;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(AGE_PROPERTY);
        builder.add(FROZEN_PROPERTY);
    }
}
