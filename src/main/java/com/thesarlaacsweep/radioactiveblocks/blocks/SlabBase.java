package com.thesarlaacsweep.radioactiveblocks.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class SlabBase extends SlabBlock {
    public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;
    private static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public SlabBase(Material materialIn, ToolType toolIn, SoundType soundTypeIn) {
        super(
                Block.Properties.create(materialIn)
                        .harvestLevel(2)
                        .hardnessAndResistance(3f, 6000f)
                        .harvestTool(toolIn)
                        .sound(soundTypeIn)
                        .func_235838_a_((blockState) -> { // light level
                            return 15;
                        })
        );
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(TYPE, FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        BlockState blockstate = context.getWorld().getBlockState(blockpos);
        if (blockstate.getBlock() == this) {
            return blockstate.with(FACING, context.getPlacementHorizontalFacing()).with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false);
        } else {
            FluidState fluidState = context.getWorld().getFluidState(blockpos);
            BlockState blockstate1 = this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing()).with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
            Direction direction = context.getFace();
            return direction != Direction.DOWN && (direction == Direction.UP || !(context.getHitVec().y - (double)blockpos.getY() > 0.5D)) ? blockstate1 : blockstate1.with(TYPE, SlabType.TOP);
        }
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn.getClassification(false) == EntityClassification.MONSTER) {
            entityIn.setFire(5000);
            entityIn.attackEntityFrom(DamageSource.ON_FIRE, 3f);
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }
}
