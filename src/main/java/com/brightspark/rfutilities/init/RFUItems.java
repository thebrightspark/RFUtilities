package com.brightspark.rfutilities.init;

import com.brightspark.rfutilities.item.ItemFireballGun;
import com.brightspark.rfutilities.item.ItemWrench;
import com.brightspark.rfutilities.util.Common;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RFUItems
{
    public static final ItemWrench itemWrench = new ItemWrench();

    public static final ItemFireballGun itemFireballGun = new ItemFireballGun();

    public static void init()
    {
        GameRegistry.register(itemWrench);

        GameRegistry.register(itemFireballGun);
    }

    public static void regModels()
    {
        Common.regModel(itemWrench);

        Common.regModel(itemFireballGun);
    }
}
