package com.energiarural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Bateria: guarda energia e distribui para os cabos/máquinas ao redor. */
public class BatteryBE extends MachineBE {
    public BatteryBE(BlockPos pos, BlockState state) {
        super(ModRegistry.BATTERY_BE.get(), pos, state, 2_000_000, 2000, 2000);
    }

    @Override
    public void serverTick() {
        if (level != null && buffer.energy > 0) {
            EnergyUtil.push(level, worldPosition, buffer, 2000);
        }
    }
}
