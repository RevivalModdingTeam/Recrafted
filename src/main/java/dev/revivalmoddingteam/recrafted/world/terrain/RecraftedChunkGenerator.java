package dev.revivalmoddingteam.recrafted.world.terrain;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;

public class RecraftedChunkGenerator extends ChunkGenerator<OverworldGenSettings> {

    private SharedSeedRandom randomSeed;
    private INoiseGenerator surfaceGen;
    private OpenSimplexNoise noise;

    public RecraftedChunkGenerator(World world, OverworldGenSettings settings) {
        super(world, new SingleBiomeProvider(new SingleBiomeProviderSettings().setBiome(Biomes.SNOWY_TUNDRA)), settings);
        randomSeed = new SharedSeedRandom(this.getSeed());
        surfaceGen = new PerlinNoiseGenerator(this.randomSeed, 4);
        noise = new OpenSimplexNoise(world.getSeed());
    }

    @Override
    public void generateSurface(IChunk chunkIn) {
        ChunkPos pos = chunkIn.getPos();
        int chunkX = pos.x;
        int chunkZ = pos.z;
        SharedSeedRandom sharedSeedRandom = new SharedSeedRandom();
        sharedSeedRandom.setBaseChunkSeed(chunkX, chunkZ);
        int xStart = pos.getXStart();
        int zStart = pos.getZStart();
        double scale = 0.0625D;
        Biome[] biomes = chunkIn.getBiomes();
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int posX = xStart + x;
                int posZ = zStart + z;
                int y = chunkIn.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, posX, posZ) + 1;
                double d = surfaceGen.func_215460_a(posX * 0.0625, posZ * 0.0625, 0.0625, x * 0.0625);
                BlockState state = getSettings().getDefaultBlock();
                biomes[z * 16 + x].buildSurface(randomSeed, chunkIn, posX, posZ, y, d, getSettings().getDefaultBlock(), getSettings().getDefaultFluid(), getSeaLevel(), world.getSeed());
            }
        }
    }

    @Override
    public int getGroundHeight() {
        return world.getSeaLevel() + 1;
    }

    @Override
    public void makeBase(IWorld worldIn, IChunk chunkIn) {
        int seaLevel = this.getSeaLevel();
        ChunkPos pos = chunkIn.getPos();
        int chunkX = pos.x;
        int chunkZ = pos.z;
        int xStart = chunkX << 4;
        int zStart = chunkZ << 4;

        double featuresize = 200.0D;
        double height = 127.5;
        int baseHeight = 110;
        double scale = 0.7D;

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int y = (int)((noise.eval((xStart + x) / featuresize, (zStart + z) / featuresize) * height * scale) + baseHeight);
                /*for(int p = 0; p < y; p++) {
                }*/
                worldIn.setBlockState(new BlockPos(xStart + x, y, zStart + z), Blocks.STONE.getDefaultState(), 2);
            }
        }
    }

    @Override
    public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_) {
        return 128;
    }
}
