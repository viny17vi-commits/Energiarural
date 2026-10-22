package com.energiarural;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CableBlock extends MachineBlock {
    public static final MapCodec<CableBlock> CODEC = simpleCodec(CableBlock::new);
    private static final Map<Direction, BooleanProperty> PROPS = Map.of(
            Direction.NORTH, BlockStateProperties.NORTH,
            Direction.SOUTH, BlockStateProperties.SOUTH,
            Direction.EAST, BlockStateProperties.EAST,
            Direction.WEST, BlockStateProperties.WEST,
            Direction.UP, BlockStateProperties.UP,
            Direction.DOWN, BlockStateProperties.DOWN);
    private static final VoxelShape CORE = Block.box(6, 6, 6, 10, 10, 10);
    private static final Map<Direction, VoxelShape> ARMS = Map.of(
            Direction.NORTH, Block.box(6, 6, 0, 10, 10, 6),
            Direction.SOUTH, Block.box(6, 6, 10, 10, 10, 16),
            Direction.EAST, Block.box(10, 6, 6, 16, 10, 10),
            Direction.WEST, Block.box(0, 6, 6, 6, 10, 10),
            Direction.UP, Block.box(6, 10, 6, 10, 16, 10),
            Direction.DOWN, Block.box(6, 0, 6, 10, 6, 10));

    public CableBlock(Properties properties) {
        super(properties);
        BlockState def = stateDefinition.any();
        for (BooleanProperty p : PROPS.values()) {
            def = def.setValue(p, false);
        }
        registerDefaultState(def);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected BlockEntityType<? extends MachineBE> type() {
        return ModRegistry.CABLE_BE.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CableBE(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PROPS.values().toArray(new BooleanProperty[0]));
    }

    private static boolean connects(BlockState neighbor) {
        return neighbor.getBlock() instanceof CableBlock || neighbor.hasBlockEntity();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        for (Direction d : Direction.values()) {
            BlockState neighbor = context.getLevel().getBlockState(context.getClickedPos().relative(d));
            state = state.setValue(PROPS.get(d), connects(neighbor));
        }
        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos currentPos,
                                     Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
        return state.setValue(PROPS.get(facing), connects(facingState));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE;
        for (Direction d : Direction.values()) {
            if (state.getValue(PROPS.get(d))) {
                shape = Shapes.or(shape, ARMS.get(d));
            }
        }
        return shape;
    }
}
