package dev.revivalmoddingteam.recrafted.api.loader.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dev.revivalmoddingteam.recrafted.api.loader.DrinkManager;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class DrinkData {

    public final int thirstLevel;
    public final boolean alwaysUseable;
    public final EffectEntry[] effectEntries;

    public DrinkData(Item item, int thirstLevel, boolean alwaysUseable, EffectEntry[] effectEntries) {
        this.thirstLevel = thirstLevel;
        this.alwaysUseable = alwaysUseable;
        this.effectEntries = effectEntries;
        DrinkManager.DRINK_REGISTRY.put(item, this);
    }

    public static class EffectEntry {

        private float chance;
        private Supplier<EffectInstance> instanceSupplier;

        public EffectEntry(float chance, Supplier<EffectInstance> instanceSupplier) {
            this.chance = chance;
            this.instanceSupplier = instanceSupplier;
        }

        public float getChance() {
            return chance;
        }

        public Supplier<EffectInstance> getInstanceSupplier() {
            return instanceSupplier;
        }

        public static class EntryDeserializer implements JsonDeserializer<EffectEntry[]> {

            @Override
            public EffectEntry[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if(!json.isJsonArray()) throw new JsonSyntaxException("Expected JsonArray under 'effects' key!");
                JsonArray array = json.getAsJsonArray();
                List<EffectEntry> effectEntries = new ArrayList<>();
                for(JsonElement element : array) {
                    if(!element.isJsonObject()) continue;
                    JsonObject object = element.getAsJsonObject();
                    float chance = object.has("chance") ? Math.max(0.0F, Math.min(1.0F, object.getAsJsonPrimitive("chance").getAsFloat())) : 1.0F;
                    Supplier<EffectInstance> instanceSupplier = DrinkManager.GSON.fromJson(json, new TypeToken<Supplier<EffectInstance>>(){}.getType());
                    effectEntries.add(new EffectEntry(chance, Objects.requireNonNull(instanceSupplier, "Effect instance supplier cannot be null!")));
                }
                return effectEntries.toArray(new EffectEntry[0]);
            }
        }

        public static class InstanceSupplierDeserializer implements JsonDeserializer<Supplier<EffectInstance>> {

            // TODO
            @Override
            public Supplier<EffectInstance> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return null;
            }
        }
    }
}
