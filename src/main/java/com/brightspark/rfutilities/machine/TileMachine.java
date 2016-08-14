package com.brightspark.rfutilities.machine;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import com.brightspark.rfutilities.reference.Config;
import com.brightspark.rfutilities.util.Common;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.HashMap;

public class TileMachine extends TileEntity implements IEnergyReceiver
{
    protected enum SideEnergyPerm
    {
        ALL,
        INPUT,
        OUTPUT,
        NONE;

        public boolean canInput()
        {
            return this == INPUT || this == ALL;
        }

        public boolean canOutput()
        {
            return this == OUTPUT || this == ALL;
        }
    }

    protected HashMap<EnumFacing, SideEnergyPerm>  energySides = new HashMap<EnumFacing, SideEnergyPerm>(6);
    protected EnergyStorage storage;

    public TileMachine()
    {
        //Machine default storage
        this(Config.machineEnergyStorage, Config.machineEnergyTransferRate);
    }

    public TileMachine(EnergyStorage storage)
    {
        this.storage = storage;
        initSides();
    }

    public TileMachine(int capacity)
    {
        this(capacity, capacity);
    }

    public TileMachine(int capacity, int maxTransfer)
    {
        this(capacity, maxTransfer, maxTransfer);
    }

    public TileMachine(int capacity, int maxReceive, int maxExtract)
    {
        storage = new EnergyStorage(capacity, maxReceive, maxExtract);
        initSides();
    }

    protected void initSides()
    {
        for(EnumFacing side : EnumFacing.VALUES)
            energySides.put(side, SideEnergyPerm.INPUT);
    }

    public boolean hasEnergy()
    {
        return storage.getEnergyStored() > 0;
    }

    public boolean isEnergyFull()
    {
        return storage.getEnergyStored() >= storage.getMaxEnergyStored();
    }

    public boolean canReceiveEnergy(EnumFacing side)
    {
        return !isEnergyFull() && energySides.get(side).canInput();
    }

    public int getMaxExtract(EnumFacing side)
    {
        if(side == null || energySides.get(side).canOutput())
            return storage.getMaxExtract();
        else
            return 0;
    }

    public int getMaxReceieve(EnumFacing side)
    {
        if(side == null || energySides.get(side).canInput())
            return storage.getMaxReceive();
        else
            return 0;
    }

    /**
     * Gets a float between 0 and 1 of how full the energy storage is (1 being full and 0 empty).
     * @return Value between 0 and 1.
     */
    public float getEnergyPercentFloat()
    {
        return (float) storage.getEnergyStored() / (float) storage.getMaxEnergyStored();
    }

    public String getEnergyPercentString()
    {
        return Math.round(getEnergyPercentFloat() * 100) + "%";
    }

    /* NBT */

    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        storage.readFromNBT(nbt);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        storage.writeToNBT(nbt);
        return nbt;
    }

    /* Overrides */

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    /**
     * Use this to send data about the block. In this case, the NBTTagCompound.
     */
    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    /**
     * Use this to update the block when a packet is received.
     */
    @Override
    public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
    {
        if(canReceiveEnergy(from))
            return storage.receiveEnergy(maxReceive, simulate);
        else
            return 0;
    }

    @Override
    public int getEnergyStored(EnumFacing from)
    {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from)
    {
        return storage.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from)
    {
        return energySides.get(from) != SideEnergyPerm.NONE;
    }
}
