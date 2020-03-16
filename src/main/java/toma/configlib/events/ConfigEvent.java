package toma.configlib.events;

import net.minecraftforge.eventbus.api.Event;

import java.io.File;

/**
 * Some events in case you wanted to do something special
 * More events might come in future, but now I don't see any use for them
 *
 * @author Toma, 06.03.2020
 */
public class ConfigEvent extends Event {

    /* Everything should be pretty much self explanatory here */

    private final File configFile;
    private final String modid;

    private ConfigEvent(final File file, final String modid) {
        this.configFile = file;
        this.modid = modid;
    }

    public boolean compareIDs(String idToCompare) {
        return idToCompare.equalsIgnoreCase(modid);
    }

    public File getConfigFile() {
        return configFile;
    }

    public static class Load extends ConfigEvent {
        public Load(final File file, final String modid) {
            super(file, modid);
        }
    }
}
