package dev.revivalmoddingteam.recrafted.util.helper;

import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import dev.revivalmoddingteam.recrafted.world.season.Season;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class TemperatureHelper {

    public static float getTemperatureAt(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        Season season = WorldCapFactory.getData(world).getSeasonData().getSeason();
        float biomeTemp = biome.getTemperature(pos);
        float biomeTempModified = biomeTemp * season.getTemperatureModified(world);
        float temp = getTemperatureBasedOnTime(world, biomeTempModified);
        if(world.isRaining()) {
            temp = temp * 0.85F;
        }
        return temp;
    }

    public static float getTemperatureBasedOnTime(World world, float base) {
        float f0 = (world.getDayTime() / 12000F) * 180;
        float f1 = base * (float) Math.sin(f0 / 180.0F * Math.PI);
        return base + f1;
    }
}
