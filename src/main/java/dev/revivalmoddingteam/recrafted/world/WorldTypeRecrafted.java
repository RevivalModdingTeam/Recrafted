package dev.revivalmoddingteam.recrafted.world;

import dev.revivalmoddingteam.recrafted.world.terrain.RecraftedChunkGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;

public class WorldTypeRecrafted extends WorldType {

    public WorldTypeRecrafted() {
        super("recrafted");
    }

    @Override
    public ChunkGenerator<?> createChunkGenerator(World world) {
        ChunkGeneratorType<OverworldGenSettings, OverworldChunkGenerator> chunkGeneratorType = ChunkGeneratorType.SURFACE;
        OverworldGenSettings settings = chunkGeneratorType.createSettings();
        settings.setDefaultBlock(Blocks.STONE.getDefaultState());
        settings.setDefaultFluid(Blocks.WATER.getDefaultState());
        return new RecraftedChunkGenerator(world, settings);
    }
}
