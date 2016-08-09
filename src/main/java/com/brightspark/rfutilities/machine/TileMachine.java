package com.brightspark.rfutilities.machine;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import com.brightspark.rfutilities.reference.Config;
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
        NONE
    }

    protected HashMap<EnumFacing, SideEnergyPerm>  energySides = new HashMap<EnumFacing, SideEnergyPerm>(6);
    protected EnergyStorage storage;

    public TileMachine()
    {
        //Machine default storage
        storage = new EnergyStorage(Config.machineEnergyStorage, Config.machineEnergyTransferRate);
    }

    public TileMachine(EnergyStorage storage)
    {
        this.storage = new EnergyStorage(storage.getMaxEnergyStored(), storage.getMaxReceive(), storage.getMaxExtract());
        this.storage.setEnergyStored(storage.getEnergyStored());
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

    private void initSides()
    {
        for(EnumFacing side : EnumFacing.VALUES)
            energySides.put(side, SideEnergyPerm.ALL);
    }

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
        SideEnergyPerm perm = energySides.get(from);
        if(perm == SideEnergyPerm.ALL || perm == SideEnergyPerm.INPUT)
            return storage.receiveEnergy(maxReceive, simulate);
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
