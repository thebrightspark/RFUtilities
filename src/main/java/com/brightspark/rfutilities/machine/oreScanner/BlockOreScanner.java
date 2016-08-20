package com.brightspark.rfutilities.machine.oreScanner;

import com.brightspark.rfutilities.machine.AbstractBlockMachine;
import com.brightspark.rfutilities.reference.Names;
import com.brightspark.rfutilities.util.Common;
import com.brightspark.rfutilities.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;

public class BlockOreScanner extends AbstractBlockMachine<TileOreScanner>
{
    private static final String CHAT_LANG = "oreScanner.chat.";
    private final int chatIdStart = Common.getNewChatMessageId();
    private final int chatIdStatus = Common.getNewChatMessageId();
    private final int chatIdProgress = Common.getNewChatMessageId();

    public BlockOreScanner()
    {
        super(Names.Blocks.ORE_SCANNER);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileOreScanner();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileOreScanner te = getTileEntity(world, pos);
        if(player.isSneaking())
        {
            if(te.getScanStatus() == TileOreScanner.EnumScanStatus.INACTIVE)
            {
                te.startScanning();
                if(world.isRemote)
                    Common.addClientChatMessage(new TextComponentTranslation(CHAT_LANG + "start"), chatIdStart);
            }
        }
        else
        {
            LogHelper.info(te.getScanStatus());
            //Show status
            if(world.isRemote)
            {
                Common.addClientChatMessage(new TextComponentTranslation(CHAT_LANG + "status", te.getScanStatus().toString()), chatIdStatus);
                switch(te.getScanStatus())
                {
                    case ACTIVE:
                        //Show progress
                        Common.addClientChatMessage(new TextComponentTranslation(CHAT_LANG + "progress", te.getProgressString()), chatIdProgress);
                        break;
                    case FINISHED:
                        //TODO: Show finished ore counts
                        String log = "Found ores:\n";
                        for(Map.Entry<Block, Integer> entry : te.getOreCount().entrySet())
                            log += entry.getKey().getLocalizedName() + " -> " + entry.getValue().toString() + "\n";
                        LogHelper.info(log);
                        break;
                }
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
    }
}
