package com.energiarural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class MachineBE extends BlockEntity {
    public final EnergyBuffer buffer;

    protected MachineBE(BlockEntityType<?> type, BlockPos pos, BlockState state, int capacity, int maxIn, int maxOut) {
        super(type, pos, state);
        this.buffer = new EnergyBuffer(capacity, maxIn, maxOut, this::setChanged);
    }

    public abstract void serverTick();

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("energy", buffer.energy);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        buffer.energy = Math.min(buffer.capacity, input.getIntOr("energy", 0));
    }
}
