package dev.revivalmoddingteam.recrafted.common;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

// TODO beter categories
public class ItemGroups {

    public static final ItemGroup RECRAFTED_BLOCKS = new ItemGroup("r_blocks") {
        @Override
        public ItemStack createIcon() {
            return ItemStack.EMPTY;
        }
    };

    public static final ItemGroup RECRAFTED_ITEMS = new ItemGroup("r_items") {
        @Override
        public ItemStack createIcon() {
            return ItemStack.EMPTY;
        }
    };
}
