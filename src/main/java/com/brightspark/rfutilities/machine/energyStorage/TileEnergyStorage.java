package com.brightspark.rfutilities.machine.energyStorage;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.brightspark.rfutilities.machine.CreativeEnergyStorage;
import com.brightspark.rfutilities.machine.TileMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import java.util.HashMap;
import java.util.Map;

public class TileEnergyStorage extends TileMachine implements ITickable, IEnergyProvider
{
    private boolean doneOnFirstTick = false;
    private Map<EnumFacing, IEnergyReceiver> receivers = new HashMap<EnumFacing, IEnergyReceiver>(6);

    //Creates a Creative Energy Storage
    public TileEnergyStorage()
    {
        super(new CreativeEnergyStorage());
    }

    public TileEnergyStorage(int capacity, int maxReceive, int maxExtract)
    {
        super(capacity, maxReceive, maxExtract);
    }

    protected void initSides()
    {
        for(EnumFacing side : EnumFacing.VALUES)
            energySides.put(side, SideEnergyPerm.ALL);
    }

    public void updateReceivers()
    {
        receivers.clear();
        for(EnumFacing side : EnumFacing.VALUES)
        {
            TileEntity te = worldObj.getTileEntity(pos.offset(side));
            if(te instanceof IEnergyReceiver)
                receivers.put(side, (IEnergyReceiver) te);
        }
    }

    @Override
    public void update()
    {
        //Get neighbouring receivers on the first tick
        if(!doneOnFirstTick)
            updateReceivers();

        //Output to adjacent acceptors
        if(hasEnergy())
            for(EnumFacing side : receivers.keySet())
                if(canExtractEnergy(side))
                    //Outputs energy to the receiver
                    storage.extractEnergy(receivers.get(side).receiveEnergy(side.getOpposite(), storage.extractEnergy(storage.getMaxExtract(), true), false), false);
    }

    public boolean canExtractEnergy(EnumFacing side)
    {
        return hasEnergy() && energySides.get(side).canOutput();
    }

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate)
    {
        if(canExtractEnergy(from))
            return storage.extractEnergy(maxExtract, simulate);
        else
            return 0;
    }
}
