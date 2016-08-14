package com.brightspark.rfutilities.waila;

import com.brightspark.rfutilities.machine.TileMachine;
import com.brightspark.rfutilities.util.Common;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class WailaProviderEnergyStorage implements IWailaDataProvider
{
    private static final String LANG_PRE = "waila.energyStorage.";

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack stack, List<String> list, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return list;
    }

    @Override
    public List<String> getWailaBody(ItemStack stack, List<String> list, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TileEntity te = accessor.getTileEntity();
        if(te instanceof TileMachine)
        {
            TileMachine machine = (TileMachine) te;
            list.add(I18n.format(LANG_PRE + "stored"));
            list.add(machine.getEnergyPercentString());
            list.add(Common.addDigitGrouping(machine.getEnergyStored(null)) + " / " + Common.addDigitGrouping(machine.getMaxEnergyStored(null)));
            list.add(I18n.format(LANG_PRE + "inOut"));
            list.add(Common.addDigitGrouping(machine.getMaxReceieve(null)) + " / " + Common.addDigitGrouping(machine.getMaxExtract(null)));
        }
        return list;
    }

    @Override
    public List<String> getWailaTail(ItemStack stack, List<String> list, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return list;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP entityPlayerMP, TileEntity tileEntity, NBTTagCompound nbtTagCompound, World world, BlockPos blockPos)
    {
        return nbtTagCompound;
    }
}
