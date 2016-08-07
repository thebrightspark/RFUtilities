package com.brightspark.rfutilities.item;

import cofh.api.energy.IEnergyContainerItem;
import com.brightspark.rfutilities.util.NBTHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemEnergyBasic extends ItemBasic implements IEnergyContainerItem
{
    protected static final String KEY_ENERGY = "Energy";
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public ItemEnergyBasic(String itemName)
    {
        this(itemName, 10000, 10000, 10000);
    }

    public ItemEnergyBasic(String itemName, int capacity, int maxReceive, int maxExtract)
    {
        super(itemName);
        setMaxStackSize(1);
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list)
    {
        ItemStack stack = new ItemStack(this);
        list.add(stack);

        stack = new ItemStack(this);
        NBTHelper.setInteger(stack, KEY_ENERGY, capacity);
        list.add(stack);
    }

    @Override
    public int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate)
    {
        int energy = getEnergyStored(stack);
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if(!simulate)
            NBTHelper.setInteger(stack, KEY_ENERGY, energy + energyReceived);
        return 0;
    }

    @Override
    public int extractEnergy(ItemStack stack, int maxExtract, boolean simulate)
    {
        int energy = getEnergyStored(stack);
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if(!simulate)
            NBTHelper.setInteger(stack, KEY_ENERGY, energy - energyExtracted);
        return 0;
    }

    @Override
    public int getEnergyStored(ItemStack stack)
    {
        return NBTHelper.getInt(stack, KEY_ENERGY);
    }

    @Override
    public int getMaxEnergyStored(ItemStack stack)
    {
        return capacity;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4)
    {
        list.add("Energy:");
        list.add(NBTHelper.getInt(stack, KEY_ENERGY) + " / " + capacity);
    }

    public boolean showDurabilityBar(ItemStack stack)
    {
        return true;
    }

    public double getDurabilityForDisplay(ItemStack stack)
    {
        return 1f - ((float) NBTHelper.getInt(stack, KEY_ENERGY) / (float) capacity);
    }
}
