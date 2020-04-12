package dev.revivalmoddingteam.recrafted.api.loader;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dev.revivalmoddingteam.recrafted.api.loader.data.DrinkData;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class DrinkManager extends JsonReloadListener {

    public static Map<Item, DrinkData> DRINK_REGISTRY = new HashMap<>();
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(DrinkData.class, new Deserializer())
            .registerTypeAdapter(DrinkData.EffectEntry[].class, new DrinkData.EffectEntry.EntryDeserializer())
            .registerTypeAdapter(new TypeToken<Supplier<EffectInstance>>(){}.getType(), new DrinkData.EffectEntry.InstanceSupplierDeserializer())
            .create();

    public DrinkManager() {
        super(GSON, "drink");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> map, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        map.forEach((loc, json) -> {
            DrinkData drinkData;
            try {
                drinkData = GSON.fromJson(json, DrinkData.class);
                System.out.println("loaded");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    protected static class Deserializer implements JsonDeserializer<DrinkData> {

        @Override
        public DrinkData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if(!json.isJsonObject()) return null;
            JsonObject object = json.getAsJsonObject();
            String itemKey = Optional.ofNullable(object.getAsJsonPrimitive("item").getAsString()).orElseThrow(() -> new JsonSyntaxException("'item' property must be defined!"));
            boolean alwaysUseable = Optional.ofNullable(object.getAsJsonPrimitive("alwaysUseable").getAsBoolean()).get();
            int thirstLevel = Optional.ofNullable(object.getAsJsonPrimitive("level").getAsInt()).orElseThrow(() -> new JsonSyntaxException("'level' property must be defined!"));
            DrinkData.EffectEntry[] effectEntries = object.has("effects") ? context.deserialize(object.get("effects"), DrinkData.EffectEntry[].class) : new DrinkData.EffectEntry[0];
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemKey));
            if(item == Items.AIR) throw new JsonSyntaxException("Unknown item! " + "(" + itemKey + ")");
            return new DrinkData(item, thirstLevel, alwaysUseable, effectEntries);
        }
    }
}
