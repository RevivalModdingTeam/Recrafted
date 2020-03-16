package dev.revivalmoddingteam.recrafted.handler;

import dev.revivalmoddingteam.recrafted.Recrafted;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registry {

    public static class RItems {

    }

    public static class RBlocks {

    }

    public static class REntityTypes {
        public static final DeferredRegister<EntityType<?>> TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, Recrafted.MODID);
    }

    public static class RTileEntityTypes {
        public static final DeferredRegister<TileEntityType<?>> TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, Recrafted.MODID);
    }

    public static class RContainerTypes {
        public static final DeferredRegister<ContainerType<?>> TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, Recrafted.MODID);
    }
}
