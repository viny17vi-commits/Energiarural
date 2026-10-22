package com.energiarural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Cabo: só gasta processamento quando tem energia passando por ele. */
public class CableBE extends MachineBE {
    public static final int RATE = 1000;

    public CableBE(BlockPos pos, BlockState state) {
        super(ModRegistry.CABLE_BE.get(), pos, state, 4000, RATE, RATE);
    }

    @Override
    public void serverTick() {
        if (level != null && buffer.energy > 0) {
            EnergyUtil.push(level, worldPosition, buffer, RATE);
        }
    }
}
