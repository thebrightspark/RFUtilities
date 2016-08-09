package com.brightspark.rfutilities.init;

import com.brightspark.rfutilities.machine.itemDetector.BlockItemDetector;
import com.brightspark.rfutilities.machine.itemDetector.TileItemDetector;
import com.brightspark.rfutilities.reference.Names;
import com.brightspark.rfutilities.util.Common;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RFUBlocks
{
    public static final BlockItemDetector blockItemDetector = new BlockItemDetector();

    private static void regBlock(Block block)
    {
        GameRegistry.register(block);
        GameRegistry.register((new ItemBlock(block)).setRegistryName(block.getRegistryName()));
    }

    public static void init()
    {
        regBlock(blockItemDetector);
    }

    public static void regModels()
    {
        Common.regModel(blockItemDetector);
    }

    public static void regTileEntities()
    {
        GameRegistry.registerTileEntity(TileItemDetector.class, Names.Blocks.ITEM_DETECTOR);
    }
}
