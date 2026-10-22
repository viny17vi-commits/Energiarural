package com.energiarural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/** Hidrelétrica: gera energia conforme a água ao redor (água corrente gira mais a roda). */
public class WaterMillBE extends MachineBE {
    /** Teto de geração (FE/t), parecido com o gerador eólico do Mekanism no máximo. */
    public static final int MAX_RATE = 192;
    private static final int PER_POINT = 16;
    private int rate;

    public WaterMillBE(BlockPos pos, BlockState state) {
        super(ModRegistry.WATER_MILL_BE.get(), pos, state, 4000, 0, 1000);
    }

    @Override
    public void serverTick() {
        if (level == null) {
            return;
        }
        // Só recalcula a água 1 vez por segundo (barato para o FPS)
        if ((level.getGameTime() + worldPosition.asLong()) % 20 == 0) {
            int points = 0;
            for (Direction d : Direction.values()) {
                FluidState fluid = level.getFluidState(worldPosition.relative(d));
                if (fluid.is(FluidTags.WATER)) {
                    points += fluid.isSource() ? 1 : 2;
                }
            }
            rate = Math.min(MAX_RATE, points * PER_POINT);
        }
        if (rate > 0 && buffer.energy < buffer.capacity) {
            buffer.generate(rate);
        }
        EnergyUtil.push(level, worldPosition, buffer, 1000);
    }
}
