package toma.config.event;

import net.minecraftforge.eventbus.api.Event;
import toma.config.datatypes.ConfigObject;

public class ConfigUpdateEvent extends Event {

    private final ConfigObject object;
    private final String modid;

    public ConfigUpdateEvent(String modid, ConfigObject configObject) {
        this.modid = modid;
        this.object = configObject;
    }

    public ConfigObject getConfigObject() {
        return object;
    }

    public String getModid() {
        return modid;
    }
}
