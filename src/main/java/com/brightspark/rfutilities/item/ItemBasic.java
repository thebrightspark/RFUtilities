package com.brightspark.rfutilities.item;

import com.brightspark.rfutilities.RFUtilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemBasic extends Item
{
    protected final String TOOLTIP;

    public ItemBasic(String itemName)
    {
        setCreativeTab(RFUtilities.RFU_TAB);
        setUnlocalizedName(itemName);
        setRegistryName(itemName);
        TOOLTIP = getUnlocalizedName() + ".tooltip.";
    }

    @Override
    public RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids)
    {
        return super.rayTrace(worldIn, playerIn, useLiquids);
    }
}
