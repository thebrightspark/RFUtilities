package com.brightspark.rfutilities.machine.oreScanner;

import com.brightspark.rfutilities.RFUtilities;
import com.brightspark.rfutilities.machine.TileMachine;
import com.brightspark.rfutilities.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;

public class TileOreScanner extends TileMachine implements ITickable
{
    public enum EnumScanStatus
    {
        INACTIVE,
        CALCULATING,
        ACTIVE,
        FINISHED;

        @Override
        public String toString()
        {
            return I18n.format("oreScanner.chat.status." + name().toLowerCase());
        }

        public EnumScanStatus getNextStatus()
        {
            return ordinal() + 1 > values().length - 1 ? values()[0] : values()[ordinal() + 1];
        }
    }

    private static final String KEY_STATUS = "status";
    private static final String KEY_CHUNK_POS = "chunkPos";
    private static final String KEY_BLOCK_TO_SCAN = "blocksToScan";
    private static final String KEY_BLOCK_SCANNED = "blocksScanned";
    private static final String KEY_CUR_POS = "curPos";
    private static final String KEY_ORE_LIST = "oreList";
    private static final String KEY_BLOCK = "block";
    private static final String KEY_COUNT = "count";

    //The max amount of blocks to be scanned per tick
    private static final int scanPerTick = 20;

    //The current status of the scanning
    private EnumScanStatus scanStatus = EnumScanStatus.INACTIVE;
    //The start X and Z block position for the chunk to be scanned
    private int chunkXStart, chunkZStart = 0;
    //The total amount of blocks that will be scanned (calculated in CALCULATION stage)
    private int blocksToScan = 0;
    //How many blocks have been scanned. Used to get the progress.
    private int blocksScanned = 0;
    //The "pointer" for the current block being scanned. Also used when calculating the total blocks to scan.
    private BlockPos scanPos = BlockPos.ORIGIN;
    //A collection of all the ores found
    private Map<Block, Integer> oreCount = new HashMap<Block, Integer>();

    public TileOreScanner()
    {
        super();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        scanStatus = EnumScanStatus.values()[nbt.getByte(KEY_STATUS)];
        chunkXStart = nbt.getInteger(KEY_CHUNK_POS + "X");
        chunkZStart = nbt.getInteger(KEY_CHUNK_POS + "Z");
        blocksToScan = nbt.getInteger(KEY_BLOCK_TO_SCAN);
        blocksScanned = nbt.getInteger(KEY_BLOCK_SCANNED);
        int x = nbt.getInteger(KEY_CUR_POS + "X");
        int y = nbt.getInteger(KEY_CUR_POS + "Y");
        int z = nbt.getInteger(KEY_CUR_POS + "Z");
        scanPos = new BlockPos(x, y, z);

        NBTTagList list = new NBTTagList();
        for(Block block : oreCount.keySet())
        {
            NBTTagCompound listTag = new NBTTagCompound();
            listTag.setString(KEY_BLOCK, block.getRegistryName().toString());
            listTag.setInteger(KEY_COUNT, oreCount.get(block));
            list.appendTag(listTag);
        }
        nbt.setTag(KEY_ORE_LIST, list);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setByte(KEY_STATUS, (byte) scanStatus.ordinal());
        nbt.setInteger(KEY_CHUNK_POS + "X", chunkXStart);
        nbt.setInteger(KEY_CHUNK_POS + "Z", chunkZStart);
        nbt.setInteger(KEY_BLOCK_TO_SCAN, blocksToScan);
        nbt.setInteger(KEY_BLOCK_SCANNED, blocksScanned);
        nbt.setInteger(KEY_CUR_POS + "X", scanPos.getX());
        nbt.setInteger(KEY_CUR_POS + "Y", scanPos.getY());
        nbt.setInteger(KEY_CUR_POS + "Z", scanPos.getZ());

        oreCount.clear();
        NBTTagList list = nbt.getTagList(KEY_ORE_LIST, Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound listTag = list.getCompoundTagAt(i);
            Block block = Block.getBlockFromName(listTag.getString(KEY_BLOCK));
            if(block == null) continue;
            oreCount.put(block, listTag.getInteger(KEY_COUNT));
        }
        return nbt;
    }

    @Override
    public void update()
    {
        //Run on the server
        if(worldObj.isRemote) return;
        //Only Calculation and Active stages do anything here
        if(scanStatus != EnumScanStatus.CALCULATING && scanStatus != EnumScanStatus.ACTIVE) return;

        //Save the current status for checking what to update the client with later
        EnumScanStatus status = scanStatus;

        for(int i = 0; i < scanPerTick; i++)
        {
            //Do processing for status
            switch(scanStatus)
            {
                case CALCULATING:
                    blocksToScan += worldObj.getTopSolidOrLiquidBlock(scanPos).getY();
                    break;
                case ACTIVE:
                    blocksScanned++;
                    Block block = worldObj.getBlockState(scanPos.add(chunkXStart, 0, chunkZStart)).getBlock();
                    ItemStack stack = new ItemStack(block);
                    if(stack == null || stack.getItem() == null)
                        break;
                    int[] oreIds = OreDictionary.getOreIDs(new ItemStack(block));
                    for(int id : oreIds)
                        if(OreDictionary.getOreName(id).startsWith("ore"))
                        {
                            Integer count = oreCount.get(block);
                            if(count == null)
                                oreCount.put(block, 1);
                            else
                                oreCount.put(block, count + 1);
                            break;
                        }
                    break;
                default:
                    return;
            }
            //Set next block
            setNextBlock();
            //Check if we've reached the end of the chunk for this stage
            if(scanPos == BlockPos.ORIGIN)
            {
                //Go to next stage
                String log = "Moving to next status: " + scanStatus.toString() + " -> ";
                scanStatus = scanStatus.getNextStatus();
                LogHelper.info(log + scanStatus.toString());
                scanPos = worldObj.getTopSolidOrLiquidBlock(new BlockPos(chunkXStart, 0, chunkZStart));
                if(scanStatus == EnumScanStatus.ACTIVE)
                {
                    LogHelper.info("Total blocks calculated! -> " + blocksToScan);
                    LogHelper.info("Scan start pos: " + scanPos);
                }
                break;
            }
        }

        //Update client block
        if(status != scanStatus)
        {
            RFUtilities.NETWORK.sendToAll(new MessageUpdateScanner(pos, MessageUpdateScanner.EnumUpdateType.SCAN_STATUS, scanStatus));
            switch(scanStatus)
            {
                case ACTIVE:
                    RFUtilities.NETWORK.sendToAll(new MessageUpdateScanner(pos, MessageUpdateScanner.EnumUpdateType.BLOCKS_TO_SCAN, blocksToScan));
                    break;
                case FINISHED:
                    RFUtilities.NETWORK.sendToAll(new MessageUpdateScanner(pos, MessageUpdateScanner.EnumUpdateType.BLOCKS_SCANNED, blocksScanned));
                    RFUtilities.NETWORK.sendToAll(new MessageUpdateScanner(pos, MessageUpdateScanner.EnumUpdateType.ORE_COUNT, oreCount));
            }
        }
        else if(scanStatus == EnumScanStatus.ACTIVE)
            RFUtilities.NETWORK.sendToAll(new MessageUpdateScanner(pos, MessageUpdateScanner.EnumUpdateType.BLOCKS_SCANNED, blocksScanned));
    }

    /**
     * Sets the scanPos to the next block along X then Z to be scanned.
     */
    private void setNextXYBlock()
    {
        if(scanPos.east().getX() >= chunkXStart + 16)
            if(scanPos.south().getZ() >= chunkZStart + 16)
            {
                LogHelper.info("Scan end pos: " + scanPos);
                scanPos = BlockPos.ORIGIN;
            }
            else
                scanPos = scanPos.south();
        else
            scanPos = scanPos.east();
    }

    /**
     * Sets the scanPos to the next block to be scanned.
     */
    private void setNextBlock()
    {
        if(scanStatus == EnumScanStatus.CALCULATING)
            setNextXYBlock();
        else
            if(scanPos.down().getY() < 1)
            {
                setNextXYBlock();
                if(scanPos != BlockPos.ORIGIN)
                    scanPos = worldObj.getTopSolidOrLiquidBlock(scanPos);
            }
            else
                scanPos = scanPos.down();
    }

    /**
     * Initiates the scanning of the chunk the block is placed in for ores.
     */
    public void startScanning()
    {
        if(scanStatus != EnumScanStatus.INACTIVE)
            return;
        scanStatus = EnumScanStatus.CALCULATING;
        RFUtilities.NETWORK.sendToAll(new MessageUpdateScanner(pos, MessageUpdateScanner.EnumUpdateType.SCAN_STATUS, scanStatus));
        chunkXStart = ((int) Math.floor(pos.getX() / 16)) * 16;
        chunkZStart = ((int) Math.floor(pos.getZ() / 16)) * 16;
        if(chunkXStart < 0) chunkXStart -= 16;
        if(chunkZStart < 0) chunkZStart -= 16;
        scanPos = worldObj.getTopSolidOrLiquidBlock(new BlockPos(chunkXStart, 0, chunkZStart));
        blocksToScan = 0;
        blocksScanned = 0;
        oreCount.clear();
    }

    public EnumScanStatus getScanStatus()
    {
        return scanStatus;
    }

    /**
     * Returns a copy of the found ores.
     */
    public Map<Block, Integer> getOreCount()
    {
        return new HashMap<Block, Integer>(oreCount);
    }

    public float getProgress()
    {
        switch(scanStatus)
        {
            default:
            case INACTIVE:
                return 0f;
            case FINISHED:
                return 1f;
            case ACTIVE:
                return (float) blocksScanned / (float) blocksToScan;
        }
    }

    public String getProgressString()
    {
        return Math.round(getProgress() * 100) + "%";
    }

    /**
     * This is used to update the block on the client side using a packet from the server.
     */
    public void updateValue(MessageUpdateScanner.EnumUpdateType updateType, Object newValue)
    {
        //LogHelper.info("Updating block: " + updateType.toString());
        switch(updateType)
        {
            case SCAN_STATUS:
                if(!(newValue instanceof EnumScanStatus)) break;
                scanStatus = (EnumScanStatus) newValue;
                LogHelper.info(scanStatus.toString());
                if(scanStatus == EnumScanStatus.FINISHED)
                    LogHelper.info("Blocks scanned: " + blocksScanned + " / " + blocksToScan);
                break;
            case BLOCKS_TO_SCAN:
                if(!(newValue instanceof Integer)) break;
                blocksToScan = (Integer) newValue;
                break;
            case BLOCKS_SCANNED:
                if(!(newValue instanceof Integer)) break;
                blocksScanned = (Integer) newValue;
                break;
            case ORE_COUNT:
                if(!(newValue instanceof Map)) break;
                oreCount = new HashMap<Block, Integer>((Map<Block, Integer>) newValue);
        }
    }
}
