package com.brightspark.rfutilities.item;

import com.brightspark.rfutilities.RFUtilities;
import net.minecraft.item.Item;

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
}
