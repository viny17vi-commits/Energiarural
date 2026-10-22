package com.energiarural;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Colhe e replanta sozinha plantações maduras (trigo, cenoura, batata, beterraba) em até 20 blocos para cada lado.
 * Varre poucos blocos por tick (em vez de tudo de uma vez) para não derrubar o FPS.
 */
public class HarvesterBE extends MachineBE {
    public static final int RADIUS = 20;
    public static final int COST = 100;
    private static final int SIZE = RADIUS * 2 + 1;
    private static final int LAYERS = 2; // na altura da máquina e uma acima
    private static final int TOTAL = SIZE * SIZE * LAYERS;
    private static final int CHECKS_PER_TICK = 100;
    private int cursor;

    public HarvesterBE(BlockPos pos, BlockState state) {
        super(ModRegistry.HARVESTER_BE.get(), pos, state, 20_000, 1000, 0);
    }

    @Override
    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        for (int i = 0; i < CHECKS_PER_TICK && buffer.energy >= COST; i++) {
            int idx = cursor;
            cursor = (cursor + 1) % TOTAL;
            int dx = idx % SIZE - RADIUS;
            int rest = idx / SIZE;
            int dz = rest % SIZE - RADIUS;
            int dy = rest / SIZE;
            BlockPos pos = worldPosition.offset(dx, dy, dz);
            if (!serverLevel.hasChunkAt(pos)) {
                continue;
            }
            BlockState state = serverLevel.getBlockState(pos);
            if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
                List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, null, null, ItemStack.EMPTY);
                serverLevel.levelEvent(2001, pos, Block.getId(state));
                serverLevel.setBlock(pos, crop.getStateForAge(0), Block.UPDATE_ALL);
                for (ItemStack drop : drops) {
                    Block.popResource(serverLevel, worldPosition.above(), drop);
                }
                buffer.energy -= COST;
                setChanged();
            }
        }
    }
}
