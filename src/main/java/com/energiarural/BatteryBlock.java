package com.energiarural;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BatteryBlock extends MachineBlock {

    public BatteryBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected BlockEntityType<? extends MachineBE> type() {
        return ModRegistry.BATTERY_BE.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BatteryBE(pos, state);
    }

    /** Clique direito com uma arma de energia na mão: recarrega a arma com a energia da bateria. */
    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hit) {
        if (!(stack.getItem() instanceof EnergyWeaponItem weapon)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof BatteryBE be) {
            int moved = weapon.charge(stack, Math.min(be.buffer.energy, 5000));
            be.buffer.energy -= moved;
            be.setChanged();
            player.sendOverlayMessage(Component.literal("Arma: " + weapon.getEnergy(stack) + " / " + weapon.tier.capacity
                    + " FE   |   Bateria: " + be.buffer.energy + " FE"));
        }
        return InteractionResult.SUCCESS;
    }
}
