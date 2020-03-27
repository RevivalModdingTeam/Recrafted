package dev.revivalmoddingteam.recrafted.common.items;

import dev.revivalmoddingteam.recrafted.common.ItemGroups;
import dev.revivalmoddingteam.recrafted.common.blocks.plant.Plant;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class PlantableItem extends RecraftedItem {

    private final Supplier<? extends Plant> plantSupplier;

    public PlantableItem(String name, Supplier<? extends Plant> plant) {
        super(name, new Properties().group(ItemGroups.RECRAFTED_ITEMS));
        this.plantSupplier = plant;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Plant plant = plantSupplier.get();
        if(plant.canPlaceOn(world, world.getBlockState(pos), pos)) {
            plant.createPlant(world, pos.up());
            if(!context.getPlayer().isCreative()) {
                context.getItem().shrink(1);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }
}
