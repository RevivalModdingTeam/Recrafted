package dev.revivalmoddingteam.recrafted.util.helper;

import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import dev.revivalmoddingteam.recrafted.world.season.Season;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class TemperatureHelper {

    public static float getTemperatureAt(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        Season season = WorldCapFactory.getData(world).getSeasonData().getSeason();
        float biomeTemp = biome.getTemperature(pos);
        float biomeTempModified = biomeTemp * season.getTemperatureModified(world);
        float temp = getTemperatureBasedOnHeight(world, pos, getTemperatureBasedOnTime(world, biomeTempModified));
        //System.out.println(world.getLightFor(LightType.BLOCK, pos));
        if(world.isRaining()) {
            temp = temp * 0.85F;
        }
        return temp;
    }

    public static float getTemperatureBasedOnTime(World world, float base) {
        float f0 = (world.getDayTime() / 12000F) * 180;
        float f1 = (base / 2.0F) * (float) Math.sin(f0 / 180.0F * Math.PI);
        return base + f1;
    }

    public static float getTemperatureBasedOnHeight(World world, BlockPos pos, float f) {
        int seaLevel = world.getSeaLevel();
        int posY = pos.getY();
        float delta = Math.abs((seaLevel - posY) / seaLevel);
        float tempJump = 0.5F;
        return f - tempJump * delta;
    }
}
