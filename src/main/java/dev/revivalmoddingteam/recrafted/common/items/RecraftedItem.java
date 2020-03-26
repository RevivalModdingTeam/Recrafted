package dev.revivalmoddingteam.recrafted.common.items;

import dev.revivalmoddingteam.recrafted.Recrafted;
import net.minecraft.item.Item;

public class RecraftedItem extends Item {

    public RecraftedItem(String key, Properties properties) {
        super(properties);
        setRegistryName(Recrafted.makeResource(key));
    }
}
