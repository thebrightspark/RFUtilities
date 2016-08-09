package com.brightspark.rfutilities.machine.itemDetector;

import com.brightspark.rfutilities.machine.TileMachine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class TileItemDetector extends TileMachine implements ITickable
{
    private static String KEY_REDSTONE = "redstone";
    private byte redstoneOutput = 0;

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        redstoneOutput = nbt.getByte(KEY_REDSTONE);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setByte(KEY_REDSTONE, redstoneOutput);
        return nbt;
    }

    public int getRedstoneOutput()
    {
        return (int) redstoneOutput;
    }

    @Override
    public void update()
    {
        //Set the redstone output dependent on the number of items
        List<EntityItem> itemEntities = worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.up()));
        if(itemEntities.size() == 0)
            redstoneOutput = 0;
        else if(itemEntities.size() >= 15)
            redstoneOutput = 15;
        else
        {
            byte count = 0;
            for(EntityItem item : itemEntities)
            {
                count += item.getEntityItem().stackSize;
                if(count > 15)
                {
                    count = 15;
                    break;
                }
            }
            redstoneOutput = count;
        }

        IBlockState state = worldObj.getBlockState(pos);
        worldObj.markAndNotifyBlock(pos, worldObj.getChunkFromBlockCoords(pos), state, state, 3);
    }
}
