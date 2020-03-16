package toma.configlib;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import toma.configlib.config.ConfigObject;
import toma.configlib.config.IConfig;
import toma.configlib.config.sides.ClientManager;
import toma.configlib.config.sides.Manager;
import toma.configlib.config.sides.ServerManager;
import toma.configlib.example.ConfigImplementationExample;

import java.util.HashMap;
import java.util.Map;

// Default mod registration
@Mod("configlib")
public class ConfigLib {

    // Map of all configs
    public static Map<String, ConfigObject> CONFIGS = new HashMap<>();
    public static Logger log = LogManager.getLogger();
    public static Manager sidedManager = DistExecutor.runForDist(() -> ClientManager::new, () -> ServerManager::new);

    public ConfigLib() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSetup);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
        // REGISTRATION OF THE CONFIG IMPLEMENTATION
        //registerCustomConfigMod(this.getClass(), new ConfigImplementationExample());
    }

    // loads all configs based on their sides
    private void onSetup(FMLCommonSetupEvent event) {
        sidedManager.load();
    }

    private void onServerStart(FMLServerAboutToStartEvent event) {
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> ServerManager::loadServerConfig);
    }

    /**
     * Use this to register your {@link IConfig} implementation
     * @param main - Class with {@link Mod} annotation
     * @param value - the {@link IConfig} implementation
     */
    public static void registerCustomConfigMod(Class<?> main, IConfig value) {
        Mod modHolder = main.getAnnotation(Mod.class);
        if(modHolder == null) {
            throw new RuntimeException(main.toString() + " is not valid Mod class!");
        }
        CONFIGS.put(modHolder.value(), new ConfigObject.Builder().impl(value).build());
    }
}
