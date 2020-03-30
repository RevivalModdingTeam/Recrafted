package dev.revivalmoddingteam.recrafted.world.biome;

import dev.revivalmoddingteam.recrafted.world.season.Season;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class RecraftedBiome extends Biome {

    public static Set<BiomeData> biomeDataSet = new HashSet<>();
    protected final BiomeType type;

    public RecraftedBiome(BiomeType type, Builder builder) {
        super(builder);
        this.type = type;
    }

    public static void fillBiomeList() {
        Predicate<Biome> filter = biome -> biome.getDefaultTemperature() > 0 && biome.getDefaultTemperature() <= 0.8F && biome.precipitation != RainType.NONE;
        Collection<Biome> biomes = ForgeRegistries.BIOMES.getValues();
        for(Biome biome : biomes) {
            if(filter.test(biome)) {
                biomeDataSet.add(BiomeData.from(biome));
            }
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

        public void update(Season season, World world) {
            biome.precipitation = season.isWinter() ? RainType.SNOW : RainType.RAIN;
            biome.temperature = defaultTemperature + season.getTemperature(world) * 2;
        }
    }
}
