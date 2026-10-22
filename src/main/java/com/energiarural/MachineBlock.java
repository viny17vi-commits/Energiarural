package com.energiarural;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class MachineBlock extends Block implements EntityBlock {
    protected MachineBlock(Properties properties) {
        super(properties);
    }

    protected abstract BlockEntityType<? extends MachineBE> type();

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide() || blockEntityType != type()) {
            return null;
        }
        return (lvl, pos, st, be) -> ((MachineBE) be).serverTick();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MachineBE be) {
            player.sendOverlayMessage(Component.literal("Energia: " + be.buffer.energy + " / " + be.buffer.capacity + " FE"));
        }
        return InteractionResult.SUCCESS;
    }
}
