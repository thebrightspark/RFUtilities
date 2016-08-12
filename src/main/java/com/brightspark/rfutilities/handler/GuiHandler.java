package com.brightspark.rfutilities.handler;

import com.brightspark.rfutilities.machine.AbstractBlockMachine;
import com.brightspark.rfutilities.machine.GuiEnergyStorage;
import com.brightspark.rfutilities.machine.TileMachine;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        BlockPos pos = new BlockPos(x, y, z);
        Block block = world.getBlockState(pos).getBlock();
        if(block instanceof AbstractBlockMachine)
            return new GuiEnergyStorage((TileMachine) world.getTileEntity(pos));
        return null;
    }
}
