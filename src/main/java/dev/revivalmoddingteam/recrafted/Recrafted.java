package dev.revivalmoddingteam.recrafted;

import dev.revivalmoddingteam.recrafted.config.RecraftedConfig;
import dev.revivalmoddingteam.recrafted.handler.Registry;
import dev.revivalmoddingteam.recrafted.network.NetworkHandler;
import dev.revivalmoddingteam.recrafted.world.WorldTypeRecrafted;
import dev.revivalmoddingteam.recrafted.world.capability.IWorldCap;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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

        eventBus.addListener(this::setupCommon);

        ConfigLib.registerCustomConfigMod(getClass(), new RecraftedConfig());
    }

    private void setupCommon(FMLCommonSetupEvent event) {
        NetworkHandler.initialize();
        CapabilityManager.INSTANCE.register(IWorldCap.class, new WorldCapStorage(), WorldCapFactory::new);
        worldTypeRecrafted = new WorldTypeRecrafted();
    }

    public static ResourceLocation getResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}
