package dev.revivalmoddingteam.recrafted.api.loader.drink;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dev.revivalmoddingteam.recrafted.player.IPlayerCap;
import dev.revivalmoddingteam.recrafted.player.PlayerCapFactory;
import dev.revivalmoddingteam.recrafted.player.objects.PlayerStatData;
import dev.revivalmoddingteam.recrafted.util.helper.JsonHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class DrinkData {

    public final int thirstLevel;
    public final EffectEntry[] effectEntries;

    public DrinkData(Item item, int thirstLevel, EffectEntry[] effectEntries) {
        this.thirstLevel = thirstLevel;
        this.effectEntries = effectEntries;
        DrinkManager.DRINK_REGISTRY.put(item, this);
    }

    public void applyOn(Entity entity) {
        for(EffectEntry effectEntry : effectEntries) {
            if(entity.world.rand.nextFloat() <= effectEntry.chance) {
                ((LivingEntity) entity).addPotionEffect(effectEntry.instanceSupplier.get());
            }
        }
        if(entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            IPlayerCap cap = PlayerCapFactory.get(player);
            PlayerStatData statData = cap.getStats();
            statData.setThirstLevel(Math.min(statData.getThirstLevel() + thirstLevel, 20));
            statData.setThirstSaturation(20.0F);
            cap.syncToClient();
        }
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
                    Supplier<EffectInstance> instanceSupplier = DrinkManager.GSON.fromJson(object, new TypeToken<Supplier<EffectInstance>>(){}.getType());
                    effectEntries.add(new EffectEntry(chance, Objects.requireNonNull(instanceSupplier, "Effect instance supplier cannot be null!")));
                }
                return effectEntries.toArray(new EffectEntry[0]);
            }
        }

        public static class InstanceSupplierDeserializer implements JsonDeserializer<Supplier<EffectInstance>> {

            @Override
            public Supplier<EffectInstance> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject object = (JsonObject) json;
                String effectType = object.has("type") ? object.getAsJsonPrimitive("type").getAsString() : "";
                Effect effect = Objects.requireNonNull(ForgeRegistries.POTIONS.getValue(new ResourceLocation(effectType)), "Unknown effect type: " + effectType + "!");
                int duration = JsonHelper.getAsPrimitive("duration", object, JsonPrimitive::getAsInt, 600);
                int amplifier = JsonHelper.getAsPrimitive("amplifier", object, JsonPrimitive::getAsInt, 0);
                boolean ambient = JsonHelper.getAsPrimitive("ambient", object, JsonPrimitive::getAsBoolean, false);
                boolean particles = JsonHelper.getAsPrimitive("particle", object, JsonPrimitive::getAsBoolean, true);
                boolean icon = JsonHelper.getAsPrimitive("icon", object, JsonPrimitive::getAsBoolean, true);
                return () -> new EffectInstance(effect, duration, amplifier, ambient, particles, icon);
            }
        }
    }
}
