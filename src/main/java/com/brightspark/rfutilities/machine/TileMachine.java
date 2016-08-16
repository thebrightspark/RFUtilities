package com.brightspark.rfutilities.machine;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import com.brightspark.rfutilities.reference.Config;
import com.brightspark.rfutilities.util.Common;
import com.brightspark.rfutilities.util.LogHelper;
import com.brightspark.rfutilities.util.NBTHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.HashMap;

public class TileMachine extends TileEntity implements IEnergyReceiver
{
    public enum SideEnergyPerm
    {
        ALL(0, "all"),
        INPUT(1, "input"),
        OUTPUT(2, "output"),
        NONE(3, "none");

        private static final String LANG = "sideEnergyPerm.";
        private static SideEnergyPerm[] allPerms = new SideEnergyPerm[4];
        public final int id;
        public final String unlocName;

        static
        {
            //Create array of all perms
            for(SideEnergyPerm perm : values())
                allPerms[perm.id] = perm;
        }

        SideEnergyPerm(int id, String unlocName)
        {
            this.id = id;
            this.unlocName = LANG + unlocName;
        }

        public static SideEnergyPerm getById(int id)
        {
            return id < 0 || id > allPerms.length - 1 ? null : allPerms[id];
        }

        public SideEnergyPerm getNextPerm()
        {
            return id + 1 > allPerms.length - 1 ? getById(0) : getById(id + 1);
        }

        public boolean canInput()
        {
            return this == INPUT || this == ALL;
        }

        public boolean canOutput()
        {
            return this == OUTPUT || this == ALL;
        }

        public String getChatDisplay(EnumFacing side)
        {
            String sideText = Common.capitaliseFirstLetter(side.getName());
            return TextFormatting.BLUE + "[" + I18n.format(LANG + "mode", sideText) + " " + TextFormatting.DARK_AQUA + I18n.format(unlocName) + TextFormatting.BLUE + "]" + TextFormatting.RESET;
        }
    }

    protected HashMap<EnumFacing, SideEnergyPerm>  energySides = new HashMap<EnumFacing, SideEnergyPerm>(6);
    protected EnergyStorage storage;
    //This is used in block.getDrops() so that the energy is only saved to the ItemStack when a wrench is used.
    public boolean usedWrenchToBreak = false;

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
            energySides.put(side, SideEnergyPerm.ALL);
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

    public void copyDataFrom(TileMachine machine)
    {
        if(machine == null || machine.storage == null)
            return;
        storage.setCapacity(machine.getMaxEnergyStored(null));
        storage.setMaxExtract(machine.getMaxExtract(null));
        storage.setMaxReceive(machine.getMaxReceieve(null));
        storage.setEnergyStored(machine.getEnergyStored(null));
    }

    public void setEnergySidePerm(EnumFacing side, SideEnergyPerm perm)
    {
        energySides.put(side, perm);
    }

    public void nextEnergySidePerm(EnumFacing side)
    {
        SideEnergyPerm perm = SideEnergyPerm.getById(energySides.get(side).id).getNextPerm();
        energySides.put(side, perm);
    }

    public SideEnergyPerm getEnergyPermForSide(EnumFacing side)
    {
        return energySides.get(side);
    }

    /* NBT */

    public static final String KEY_STACK_ENERGY = "stackEnergy";

    public void writeEnergyToStack(ItemStack stack)
    {
        LogHelper.info("Machine Energy (write): " + storage.getEnergyStored());
        NBTHelper.setInteger(stack, KEY_STACK_ENERGY, storage.getEnergyStored());
    }

    public void readEnergyFromStack(ItemStack stack)
    {
        storage.setEnergyStored(NBTHelper.getInt(stack, KEY_STACK_ENERGY));
        LogHelper.info("Machine Energy (read): " + storage.getEnergyStored());
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
