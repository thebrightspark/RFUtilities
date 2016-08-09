package com.brightspark.rfutilities.item;

import com.brightspark.rfutilities.machine.AbstractBlockMachine;
import com.brightspark.rfutilities.machine.AbstractBlockMachineDirectional;
import com.brightspark.rfutilities.reference.Names;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemWrench extends ItemBasic
{
    public ItemWrench()
    {
        super(Names.Items.WRENCH);
        setMaxStackSize(1);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if(block instanceof AbstractBlockMachine)
        {
            if(player.isSneaking())
            {
                //Remove the block if it's a machine
                if(((AbstractBlockMachine) block).canPickupWithWrench())
                {
                    if(block.removedByPlayer(state, world, pos, player, true))
                        block.harvestBlock(world, player, pos, state, world.getTileEntity(pos), stack);
                    return EnumActionResult.FAIL;
                }
            }
            else
            {
                //Set the facing of the block if it's a machine that can be rotated.
                if(block instanceof AbstractBlockMachineDirectional)
                    ((AbstractBlockMachineDirectional)block).setFacingWithWrench(world, pos, state, facing);
            }
        }
        return EnumActionResult.PASS;
    }
}
