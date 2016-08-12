package com.brightspark.rfutilities.machine;

import cofh.api.energy.EnergyStorage;

public class CreativeEnergyStorage extends EnergyStorage
{
    public CreativeEnergyStorage()
    {
        super(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        energy = Math.round((float) capacity / 2f);
    }

    @Override
    public EnergyStorage setCapacity(int capacity)
    {
        return this;
    }

    @Override
    public EnergyStorage setMaxTransfer(int maxTransfer)
    {
        setMaxReceive(maxTransfer);
        setMaxExtract(maxTransfer);
        return this;
    }

    @Override
    public EnergyStorage setMaxReceive(int maxReceive)
    {
        this.maxReceive = maxReceive;
        return this;
    }

    @Override
    public EnergyStorage setMaxExtract(int maxExtract)
    {
        this.maxExtract = maxExtract;
        return this;
    }

    @Override
    public int getMaxReceive()
    {
        return maxReceive;
    }

    @Override
    public int getMaxExtract()
    {
        return maxExtract;
    }

    @Override
    public void setEnergyStored(int energy) {}

    @Override
    public void modifyEnergyStored(int energy) {}

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        return maxReceive;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        return maxExtract;
    }

    @Override
    public int getEnergyStored()
    {
        return energy;
    }

    @Override
    public int getMaxEnergyStored()
    {
        return capacity;
    }
}
