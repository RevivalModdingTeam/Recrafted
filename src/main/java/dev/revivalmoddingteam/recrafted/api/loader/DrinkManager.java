package dev.revivalmoddingteam.recrafted.api.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class DrinkManager extends JsonReloadListener {

    private static final Gson GSON = new GsonBuilder().create();

    public DrinkManager() {
        super(GSON, "drink");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {

    }
}
