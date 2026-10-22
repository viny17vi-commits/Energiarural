package com.energiarural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WaterMillBlock extends MachineBlock {

    public WaterMillBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected BlockEntityType<? extends MachineBE> type() {
        return ModRegistry.WATER_MILL_BE.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WaterMillBE(pos, state);
    }
}
