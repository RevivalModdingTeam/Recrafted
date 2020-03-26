package dev.revivalmoddingteam.recrafted.common.blocks;

import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.handler.Registry;
import net.minecraft.block.Block;

public class RecraftedBlock extends Block {

    public RecraftedBlock(String key, Block.Properties properties) {
        super(properties);
        setRegistryName(Recrafted.makeResource(key));
        this.registerItemForBlock();
    }

    public void registerItemForBlock() {
        Registry.EventListener.registerBlockItem(this);
    }
}
