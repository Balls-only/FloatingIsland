package com.floatingisland;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class FloatingIslandMod implements ModInitializer {
    public static final String MOD_ID = "floatingisland";

    private static final Map<UUID, IslandData> ACTIVE_ISLANDS = new HashMap<>();

    private record IslandData(ServerWorld world, Map<BlockPos, BlockState> originals) {}

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(
                ToggleIslandPayload.ID,
                ToggleIslandPayload.CODEC
        );

        ServerPlayNetworking.registerGlobalReceiver(
                ToggleIslandPayload.ID,
                (payload, context) -> context.server().execute(
                        () -> handleToggle(context.player(), payload.enabled(), payload.size())
                )
        );
    }

    private static void handleToggle(ServerPlayerEntity player, boolean enabled, int size) {
        if (!player.hasPermissionLevel(2)) {
            return;
        }

        UUID playerId = player.getUuid();
        restoreIsland(playerId);

        if (!enabled || (size != 16 && size != 32)) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        BlockPos playerPos = player.getBlockPos();

        int originX = Math.floorDiv(playerPos.getX(), 16) * 16;
        int originZ = Math.floorDiv(playerPos.getZ(), 16) * 16;
        int baseY = playerPos.getY();

        Map<BlockPos, BlockState> originals = new HashMap<>();
        Random random = new Random(
                world.getSeed() ^ originX ^ ((long) originZ << 32) ^ baseY
        );

        double center = size / 2.0;
        double radius = size * 0.48;

        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                double dx = x + 0.5 - center;
                double dz = z + 0.5 - center;

                if (Math.sqrt(dx * dx + dz * dz) > radius) {
                    continue;
                }

                BlockPos dirtPos = new BlockPos(originX + x, baseY, originZ + z);
                BlockPos grassPos = dirtPos.up();

                placeTracked(world, dirtPos, Blocks.DIRT.getDefaultState(), originals);
                placeTracked(world, grassPos, Blocks.GRASS_BLOCK.getDefaultState(), originals);

                if (random.nextFloat() < 0.12f) {
                    int height = 1 + random.nextInt(4);
                    for (int y = 1; y <= height; y++) {
                        BlockPos bambooPos = grassPos.up(y);
                        placeTracked(
                                world,
                                bambooPos,
                                Blocks.BAMBOO.getDefaultState(),
                                originals
                        );
                    }
                }
            }
        }

        ACTIVE_ISLANDS.put(playerId, new IslandData(world, originals));
    }

    private static void placeTracked(
            ServerWorld world,
            BlockPos pos,
            BlockState newState,
            Map<BlockPos, BlockState> originals
    ) {
        BlockPos immutablePos = pos.toImmutable();
        originals.putIfAbsent(immutablePos, world.getBlockState(immutablePos));
        world.setBlockState(immutablePos, newState, Block.NOTIFY_ALL);
    }

    private static void restoreIsland(UUID playerId) {
        IslandData island = ACTIVE_ISLANDS.remove(playerId);
        if (island == null) {
            return;
        }

        ServerWorld world = island.world();

        for (Map.Entry<BlockPos, BlockState> entry : island.originals().entrySet()) {
            BlockPos pos = entry.getKey();

            if (!world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                continue;
            }

            world.setBlockState(pos, entry.getValue(), Block.NOTIFY_ALL);
        }
    }
}
