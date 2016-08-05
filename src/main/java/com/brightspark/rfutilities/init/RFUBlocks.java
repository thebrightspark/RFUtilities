package com.brightspark.rfutilities.init;

import com.brightspark.rfutilities.util.Common;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RFUBlocks
{


    private static void regBlock(Block block)
    {
        GameRegistry.register(block);
        GameRegistry.register((new ItemBlock(block)).setRegistryName(block.getRegistryName()));
    }

    public static void init()
    {
        //regBlock();
    }

    public static void regModels()
    {
        //Common.regModel();
    }
}
