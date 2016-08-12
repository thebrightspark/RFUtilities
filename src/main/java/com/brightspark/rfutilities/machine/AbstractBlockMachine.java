package com.brightspark.rfutilities.machine;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public abstract class AbstractBlockMachine<T extends TileMachine> extends AbstractBlockContainer
{
    public AbstractBlockMachine(String name)
    {
        super(name);
        setHasGui();
    }

    public boolean canPickupWithWrench()
    {
        return true;
    }

    public T getTileEntity(IBlockAccess world, BlockPos pos)
    {
        return (T) world.getTileEntity(pos);
    }
}
