package com.energiarural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HarvesterBlock extends MachineBlock {

    public HarvesterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected BlockEntityType<? extends MachineBE> type() {
        return ModRegistry.HARVESTER_BE.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HarvesterBE(pos, state);
    }
}
