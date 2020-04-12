package dev.revivalmoddingteam.recrafted;

import dev.revivalmoddingteam.recrafted.api.loader.DrinkManager;
import dev.revivalmoddingteam.recrafted.client.render.ClientManager;
import dev.revivalmoddingteam.recrafted.config.RecraftedConfig;
import dev.revivalmoddingteam.recrafted.handler.Registry;
import dev.revivalmoddingteam.recrafted.network.NetworkHandler;
import dev.revivalmoddingteam.recrafted.player.IPlayerCap;
import dev.revivalmoddingteam.recrafted.player.PlayerCapFactory;
import dev.revivalmoddingteam.recrafted.player.PlayerCapStorage;
import dev.revivalmoddingteam.recrafted.world.WorldTypeRecrafted;
import dev.revivalmoddingteam.recrafted.world.biome.RecraftedBiome;
import dev.revivalmoddingteam.recrafted.world.capability.IWorldCap;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapStorage;
import dev.revivalmoddingteam.recrafted.world.season.Seasons;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import toma.config.Config;

@Mod(Recrafted.MODID)
public class Recrafted {
    public static final String MODID = "recrafted";
    public static final Logger log = LogManager.getLogger();

    public static DrinkManager drinkManager = new DrinkManager();

    public static WorldTypeRecrafted worldTypeRecrafted;

    public Recrafted() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        Registry.REntityTypes.TYPES.register(modEventBus);
        Registry.RContainerTypes.TYPES.register(modEventBus);
        Registry.RTileEntityTypes.TYPES.register(modEventBus);

        modEventBus.addListener(this::setupCommon);
        modEventBus.addListener(this::setupClient);
        forgeEventBus.addListener(this::serverStarting);
        forgeEventBus.addListener(this::serverAboutToStart);

        Config.registerConfig(this.getClass(), RecraftedConfig::new);
    }

    private void setupClient(FMLClientSetupEvent event) {
        ClientManager.registerEntityRenderers();
    }

    private void setupCommon(FMLCommonSetupEvent event) {
        NetworkHandler.initialize();
        CapabilityManager.INSTANCE.register(IWorldCap.class, new WorldCapStorage(), WorldCapFactory::new);
        CapabilityManager.INSTANCE.register(IPlayerCap.class, new PlayerCapStorage(), PlayerCapFactory::new);
        Seasons.register();
        RecraftedBiome.fillBiomeList();
        worldTypeRecrafted = new WorldTypeRecrafted();
    }

    private void serverAboutToStart(FMLServerAboutToStartEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        server.getResourceManager().addReloadListener(drinkManager);
    }

    private void serverStarting(FMLServerStartingEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        server.getWorlds().forEach(w -> {
            if(w.getDimension().getType() == DimensionType.OVERWORLD) {
                RecraftedBiome.updateBiomeMapData(WorldCapFactory.getData(w).getSeasonData().getSeason(), w);
            }
        });
    }

    public static ResourceLocation makeResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}
