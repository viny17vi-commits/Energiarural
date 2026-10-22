package com.energiarural;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WaterMillBlock extends MachineBlock {
    public static final MapCodec<WaterMillBlock> CODEC = simpleCodec(WaterMillBlock::new);

    public WaterMillBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
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
