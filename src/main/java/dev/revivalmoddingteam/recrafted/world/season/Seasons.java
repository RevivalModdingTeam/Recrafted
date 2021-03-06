package dev.revivalmoddingteam.recrafted.world.season;

import dev.revivalmoddingteam.recrafted.network.NetworkHandler;
import dev.revivalmoddingteam.recrafted.network.client.CPacketUpdateBiomePrecipitation;
import dev.revivalmoddingteam.recrafted.util.helper.ModHelper;
import dev.revivalmoddingteam.recrafted.world.biome.RecraftedBiome;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Seasons {

    public static final Season[] REGISTRY = new Season[12];

    @OnlyIn(Dist.CLIENT)
    public static int getGrassColor(IEnviromentBlockReader enviromentBlockReader, BlockPos pos) {
        if (enviromentBlockReader == null) {
            return GrassColors.get(0.5D, 1.0D);
        }
        ClientWorld world = Minecraft.getInstance().world;
        int baseColor = GrassColors.get(0.5D, 1.0D);
        if (world == null) return baseColor;
        Season season = WorldCapFactory.getData(world).getSeasonData().getSeason();
        return ModHelper.inv_mix(enviromentBlockReader.getBiome(pos).getGrassColor(pos), season.getFoliageColorMultiplier());
    }

    @OnlyIn(Dist.CLIENT)
    public static int getFoliageColors(IEnviromentBlockReader enviromentBlockReader, BlockPos pos) {
        if (enviromentBlockReader == null) {
            return FoliageColors.getDefault();
        }
        ClientWorld world = Minecraft.getInstance().world;
        return ModHelper.inv_mix(enviromentBlockReader.getBiome(pos).getFoliageColor(pos), WorldCapFactory.getData(world).getSeasonData().getSeason().getFoliageColorMultiplier());
    }

    public static void onSeasonChange(Season newSeason, World world) {
        if(world.getDimension().getType() != DimensionType.OVERWORLD) return;
        RecraftedBiome.updateBiomeMapData(newSeason, world);
        if(!world.isRemote) {
            NetworkHandler.sendToAllClients(new CPacketUpdateBiomePrecipitation(), world);
        }
    }

    public static void register() {
        REGISTRY[0] = Season.SeasonBuilder.create()
                .key("early_spring")
                .id(0)
                .colors(0x2F88F0, 0x00CD21)
                .tempModifier(0.1F)
                .updatesChunks()
                .build();
        REGISTRY[1] = Season.SeasonBuilder.create()
                .key("spring")
                .id(1)
                .colors(0x2F88F0, 0x00CD21)
                .tempModifier(0.2F)
                .build();
        REGISTRY[2] = Season.SeasonBuilder.create()
                .key("late_spring")
                .id(2)
                .colors(0x2F88F0, 0x00CD21)
                .tempModifier(0.25F)
                .build();
        REGISTRY[3] = Season.SeasonBuilder.create()
                .key("early_summer")
                .id(3)
                .colors(0x34D3EF, 0x529300)
                .tempModifier(0.3F)
                .updatesChunks()
                .build();
        REGISTRY[4] = Season.SeasonBuilder.create()
                .key("summer")
                .id(4)
                .colors(0x34D3EF, 0x529300)
                .tempModifier(0.4F)
                .build();
        REGISTRY[5] = Season.SeasonBuilder.create()
                .key("late_summer")
                .id(5)
                .colors(0x34D3EF, 0x529300)
                .tempModifier(0.2F)
                .build();
        REGISTRY[6] = Season.SeasonBuilder.create()
                .key("early_autumn")
                .id(6)
                .colors(0x005DFF, 0xA74200)
                .tempModifier(0.1F)
                .updatesChunks()
                .build();
        REGISTRY[7] = Season.SeasonBuilder.create()
                .key("autumn")
                .id(7)
                .colors(0x005DFF, 0xA74200)
                .tempModifier(0.05F)
                .build();
        REGISTRY[8] = Season.SeasonBuilder.create()
                .key("late_autumn")
                .id(8)
                .colors(0x005DFF, 0xA74200)
                .tempModifier(0.01F)
                .build();
        REGISTRY[9] = Season.SeasonBuilder.create()
                .key("early_winter")
                .id(9)
                .colors(0x2B8EB5, 0x36B569)
                .tempModifier(-0.5F)
                .updatesChunks()
                .build();
        REGISTRY[10] = Season.SeasonBuilder.create()
                .key("winter")
                .id(10)
                .colors(0x2B8EB5, 0x36B569)
                .tempModifier(-0.8F)
                .build();
        REGISTRY[11] = Season.SeasonBuilder.create()
                .key("late_winter")
                .id(11)
                .colors(0x2B8EB5, 0x36B569)
                .tempModifier(-0.5F)
                .build();
    }
}
