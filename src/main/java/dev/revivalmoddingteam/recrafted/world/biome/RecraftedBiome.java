package dev.revivalmoddingteam.recrafted.world.biome;

import dev.revivalmoddingteam.recrafted.world.season.Season;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Predicate;

public class RecraftedBiome extends Biome {

    private static Map<BiomeData.WeatherType, Set<BiomeData>> biomeDataMap = new HashMap<>();
    protected final BiomeType type;

    public RecraftedBiome(BiomeType type, Builder builder) {
        super(builder);
        this.type = type;
    }

    public static void fillBiomeList() {
        biomeDataMap.put(BiomeData.WeatherType.STATIC, new HashSet<>());
        biomeDataMap.put(BiomeData.WeatherType.DYNAMIC, new HashSet<>());
        Predicate<Biome> filter = biome -> biome.getDefaultTemperature() > 0 && biome.getDefaultTemperature() <= 0.8F && biome.precipitation != RainType.NONE;
        Collection<Biome> biomes = ForgeRegistries.BIOMES.getValues();
        for(Biome biome : biomes) {
            BiomeData data = BiomeData.from(biome);
            biomeDataMap.get(filter.test(biome) ? BiomeData.WeatherType.DYNAMIC : BiomeData.WeatherType.STATIC).add(data);
        }
    }

    public static void updateBiomeMapData(Season season, World world) {
        for(Map.Entry<BiomeData.WeatherType, Set<BiomeData>> entry : biomeDataMap.entrySet()) {
            entry.getValue().forEach(data -> data.update(season, world, entry.getKey() == BiomeData.WeatherType.DYNAMIC));
        }
    }

    @Override
    public float getTemperature(BlockPos pos) {
        return type.averageTemperature + super.getTemperature(pos);
    }

    public static class BiomeData {

        public final Biome biome;
        public final float defaultTemperature;

        private BiomeData(final Biome biome, final float defaultTemperature) {
            this.biome = biome;
            this.defaultTemperature = defaultTemperature;
        }

        public static BiomeData from(Biome biome) {
            return new BiomeData(biome, biome.getDefaultTemperature());
        }

        protected void update(Season season, World world, boolean precipitation) {
            if(precipitation) biome.precipitation = season.isWinter() ? RainType.SNOW : RainType.RAIN;
            biome.temperature = defaultTemperature + season.getTemperature(world) * 2;
        }

        public enum WeatherType {
            STATIC, DYNAMIC
        }
    }
}
