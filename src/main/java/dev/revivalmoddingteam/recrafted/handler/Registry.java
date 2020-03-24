package dev.revivalmoddingteam.recrafted.handler;

import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.common.ItemGroups;
import dev.revivalmoddingteam.recrafted.common.blocks.GroundFruitBlock;
import dev.revivalmoddingteam.recrafted.common.entity.RecraftedItemEntity;
import dev.revivalmoddingteam.recrafted.common.items.PlantableItem;
import dev.revivalmoddingteam.recrafted.common.items.RecraftedFood;
import dev.revivalmoddingteam.recrafted.world.feature.DefaultTreeFeature;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class Registry {

    @ObjectHolder(Recrafted.MODID)
    public static class RItems {
        // berries and food
        public static final RecraftedFood BLUEBERRY = null;
        public static final RecraftedFood RASPBERRY = null;
        public static final RecraftedFood BLACKBERRY = null;
        public static final RecraftedFood SNOWBERRY = null;
        public static final RecraftedFood STRAWBERRY = null;

        // seeds
        public static final PlantableItem STRAWBERRY_SEEDS = null;
    }

    @ObjectHolder(Recrafted.MODID)
    public static class RBlocks {
        public static final GroundFruitBlock STRAWBERRY_PLANT = null;
    }

    public static class REntityTypes {
        public static final DeferredRegister<EntityType<?>> TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, Recrafted.MODID);
        public static final RegistryObject<EntityType<Entity>> RECRAFTED_ITEM = register("item", builder(RecraftedItemEntity::new, EntityClassification.MISC).setUpdateInterval(2).setTrackingRange(32).disableSummoning());

        private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
            return TYPES.register(name, () -> builder.build(Recrafted.getResource(name).toString()));
        }

        private static <T extends Entity> EntityType.Builder<T> builder(EntityType.IFactory<T> factory, EntityClassification classification) {
            return EntityType.Builder.create(factory, classification);
        }
    }

    public static class RTileEntityTypes {
        public static final DeferredRegister<TileEntityType<?>> TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, Recrafted.MODID);
    }

    public static class RContainerTypes {
        public static final DeferredRegister<ContainerType<?>> TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, Recrafted.MODID);
    }

    public static class RFeatures {
        public static final DeferredRegister<Feature<?>> FEATURES = new DeferredRegister<>(ForgeRegistries.FEATURES, Recrafted.MODID);
        public static final RegistryObject<DefaultTreeFeature> APPLE_TREE_FEATURE = register("apple_tree", () -> new DefaultTreeFeature(NoFeatureConfig::deserialize, true));

        private static <T extends Feature<?>> RegistryObject<T> register(String key, Supplier<T> supplier) {
            return FEATURES.register(key, supplier);
        }
    }

    @Mod.EventBusSubscriber(modid = Recrafted.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class EventListener {

        private static List<BlockItem> BLOCK_ITEM_LIST = new ArrayList<>();

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(
                new GroundFruitBlock("strawberry_plant", true, () -> new ItemStack(RItems.STRAWBERRY, 3))
            );
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            IForgeRegistry<Item> registry = event.getRegistry();
            registry.registerAll(
                    new RecraftedFood("blueberry", new RecraftedFood.Stats().food(1, 0.2F)),
                    new RecraftedFood("raspberry", new RecraftedFood.Stats().food(1, 0.3F)),
                    new RecraftedFood("blackberry", new RecraftedFood.Stats().food(1, 0.3F)),
                    new RecraftedFood("snowberry", new RecraftedFood.Stats().food(1, 0.2F).effect(0.75f, () -> new EffectInstance(Effects.POISON, 200)).alwaysUseable()),
                    new RecraftedFood("strawberry", new RecraftedFood.Stats().food(2, 0.4F).water(1).alwaysUseable()),

                    new PlantableItem("strawberry_seeds", () -> RBlocks.STRAWBERRY_PLANT)
            );
            BLOCK_ITEM_LIST.stream().filter(Objects::nonNull).forEach(registry::register);
            BLOCK_ITEM_LIST = null;
        }

        public static void registerBlockItem(BlockItem blockItem) {
            BLOCK_ITEM_LIST.add(blockItem);
        }

        public static void registerBlockItem(Block block) {
            registerBlockItem(block, new Item.Properties().group(ItemGroups.RECRAFTED_BLOCKS));
        }

        public static void registerBlockItemNoTab(Block block) {
            registerBlockItem(block, new Item.Properties());
        }

        public static void registerBlockItem(Block block, Item.Properties properties) {
            BlockItem blockItem = new BlockItem(block, properties);
            blockItem.setRegistryName(block.getRegistryName());
            BLOCK_ITEM_LIST.add(blockItem);
        }
    }
}
