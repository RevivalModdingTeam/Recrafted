package dev.revivalmoddingteam.recrafted.config;

import com.google.gson.JsonObject;
import dev.revivalmoddingteam.recrafted.world.season.Seasons;
import toma.configlib.config.IConfig;
import toma.configlib.config.display.DisplayEntry;
import toma.configlib.config.types.ConfigTypeObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecraftedConfig implements IConfig {

    private static final List<Runnable> listenerList = new ArrayList<>();
    public static ConfigTypeObject<SeasonConfig> seasonConfig = ConfigTypeObject.create("Seasons", new SeasonConfig());
    public static ConfigTypeObject<WorldConfig> worldConfig = ConfigTypeObject.create("World", new WorldConfig());

    public RecraftedConfig() {
        registerListeners();
    }

    public static void registerListeners() {
        addListener(Seasons::register);
    }

    @Override
    public void serializeClient(DisplayEntry.Obj entry) {
    }

    @Override
    public void deserializeClient(JsonObject object) {
    }

    @Override
    public void serialize(DisplayEntry.Obj entry) {
        seasonConfig.serialize(entry);
        worldConfig.serialize(entry);
    }

    @Override
    public void deserialize(JsonObject object) {
        seasonConfig.deserialize(object);
        worldConfig.deserialize(object);
    }

    @Override
    public Collection<Runnable> getListeners() {
        return listenerList;
    }

    public static SeasonConfig getSeasonConfiguration() {
        return seasonConfig.get();
    }

    public static WorldConfig getWorldConfig() {
        return worldConfig.get();
    }

    public static WorldConfig.PlantConfig getPlantConfig() {
        return worldConfig.get().plants.get();
    }

    public static void addListener(final Runnable action) {
        listenerList.add(action);
    }
}
