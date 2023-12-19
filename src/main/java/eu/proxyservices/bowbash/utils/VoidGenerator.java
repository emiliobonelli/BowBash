package eu.proxyservices.bowbash.utils;

import org.bukkit.generator.ChunkGenerator;

public class VoidGenerator extends ChunkGenerator {

    public ChunkData generateChunkData(org.bukkit.World world, java.util.Random random, int x, int z, ChunkGenerator.BiomeGrid biome) {
        return super.createChunkData(world);
    }

}
