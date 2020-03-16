package dev.revivalmoddingteam.recrafted.world;

import dev.revivalmoddingteam.recrafted.world.terrain.RecraftedChunkGenerator;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;

public class WorldTypeRecrafted extends WorldType {

    public WorldTypeRecrafted() {
        super("realistic");
    }

    @Override
    public ChunkGenerator<?> createChunkGenerator(World world) {
        return new RecraftedChunkGenerator(world, new OverworldGenSettings());
    }
}
