package com.brightspark.rfutilities.machine;

import com.brightspark.rfutilities.RFUtilities;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class AbstractBlockContainer extends BlockContainer
{
    protected boolean hasGui = false;

    public AbstractBlockContainer(String name)
    {
        super(Material.ROCK);
        setCreativeTab(RFUtilities.RFU_TAB);
        setUnlocalizedName(name);
        setRegistryName(name);
        setHardness(2f);
        setResistance(10f);
    }

    public void setHasGui()
    {
        hasGui = true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if(!hasGui)
            return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
        if(!world.isRemote && !player.isSneaking())
            player.openGui(RFUtilities.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
