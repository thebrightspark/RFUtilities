package com.brightspark.rfutilities.machine.itemDetector;

import com.brightspark.rfutilities.machine.AbstractBlockMachineDirectional;
import com.brightspark.rfutilities.reference.Names;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockItemDetector extends AbstractBlockMachineDirectional<TileItemDetector>
{
    public BlockItemDetector()
    {
        super(Names.Blocks.ITEM_DETECTOR);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileItemDetector();
    }

    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        TileItemDetector te = getTileEntity(blockAccess, pos);
        if(te == null)
            return 0;
        return te.getRedstoneOutput();
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }
}
