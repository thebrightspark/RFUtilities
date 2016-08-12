package com.brightspark.rfutilities.machine.energyStorage;

import com.brightspark.rfutilities.machine.AbstractBlockMachine;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEnergyStorage extends AbstractBlockMachine<TileEnergyStorage>
{
    private int capacity, maxReceive, maxExtract;

    //Creates a Creative Energy Storage
    public BlockEnergyStorage(String name)
    {
        super(name);
        capacity = maxReceive = maxExtract = -1;
    }

    public BlockEnergyStorage(String name, int capacity, int maxReceive, int maxExtract)
    {
        super(name);
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        //setHasGui();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        if(capacity == -1)
            return new TileEnergyStorage();
        else
            return new TileEnergyStorage(capacity, maxReceive, maxExtract);
    }

    /**
     * Called when a tile entity on a side of this block changes is created or is destroyed.
     * @param world The world
     * @param pos Block position in world
     * @param neighbor Block position of neighbor
     */
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    {
        getTileEntity(world, pos).updateReceivers();
    }
}
