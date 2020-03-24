package dev.revivalmoddingteam.recrafted;

import dev.revivalmoddingteam.recrafted.client.render.ClientManager;
import dev.revivalmoddingteam.recrafted.config.RecraftedConfig;
import dev.revivalmoddingteam.recrafted.handler.Registry;
import dev.revivalmoddingteam.recrafted.network.NetworkHandler;
import dev.revivalmoddingteam.recrafted.player.IPlayerCap;
import dev.revivalmoddingteam.recrafted.player.PlayerCapFactory;
import dev.revivalmoddingteam.recrafted.player.PlayerCapStorage;
import dev.revivalmoddingteam.recrafted.world.WorldTypeRecrafted;
import dev.revivalmoddingteam.recrafted.world.capability.IWorldCap;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import toma.configlib.ConfigLib;

@Mod(Recrafted.MODID)
public class Recrafted {
    public static final String MODID = "recrafted";
    public static final Logger log = LogManager.getLogger();

    public static WorldTypeRecrafted worldTypeRecrafted;

    public Recrafted() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Registry.REntityTypes.TYPES.register(eventBus);
        Registry.RContainerTypes.TYPES.register(eventBus);
        Registry.RTileEntityTypes.TYPES.register(eventBus);
        Registry.RFeatures.FEATURES.register(eventBus);

        eventBus.addListener(this::setupCommon);
        eventBus.addListener(this::setupClient);
        eventBus.register(this);

        ConfigLib.registerCustomConfigMod(getClass(), new RecraftedConfig());
    }

    private void setupClient(FMLClientSetupEvent event) {
        ClientManager.registerEntityRenderers();
    }

    private void setupCommon(FMLCommonSetupEvent event) {
        NetworkHandler.initialize();
        CapabilityManager.INSTANCE.register(IWorldCap.class, new WorldCapStorage(), WorldCapFactory::new);
        CapabilityManager.INSTANCE.register(IPlayerCap.class, new PlayerCapStorage(), PlayerCapFactory::new);
        worldTypeRecrafted = new WorldTypeRecrafted();
    }

    public static ResourceLocation getResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}
