package toma.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.gui.GuiModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import toma.config.datatypes.ConfigObject;
import toma.config.object.builder.ConfigBuilder;
import toma.config.ui.ModListScreen;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod("configlib")
public class Config {

    protected static final Map<String, ConfigObject> CONFIGS = new HashMap<>();

    /** Use this to register your config */
    public static void registerConfig(@Nonnull Class<?> modClass, @Nonnull Supplier<IConfig> configObject) {
        Mod mod = modClass.getAnnotation(Mod.class);
        if(mod == null) {
            throw new IllegalArgumentException("Provided " + modClass.toString() + " is not valid @Mod class!");
        }
        IConfig config = configObject.get();
        ConfigObject core = new ConfigObject(mod.value(), null);
        CONFIGS.put(mod.value(), config.getConfig(ConfigBuilder.create(core)));
    }

    public static Map<String, ConfigObject> configs() {
        return CONFIGS;
    }

    /* ====================================================================================================== */

    public static final String MODID = "configlib";
    public static Logger log = LogManager.getLogger("CONFIG");

    public Config() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        modBus.addListener(this::clientSetup);
        forgeBus.addListener(this::serverSetup);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> forgeBus.addListener(this::openGui));
        //registerConfig(getClass(), ConfigImplementation::new);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        ConfigLoader.loadFor(Dist.CLIENT);
    }

    private void serverSetup(FMLServerAboutToStartEvent event) {
        ConfigLoader.loadFor(Dist.DEDICATED_SERVER);
    }

    @OnlyIn(Dist.CLIENT)
    private void openGui(GuiOpenEvent event) {
        if(event.getGui() instanceof GuiModList) {
            event.setGui(new ModListScreen());
        }
    }
}
