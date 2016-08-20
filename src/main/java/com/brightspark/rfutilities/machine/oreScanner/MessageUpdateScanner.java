package com.brightspark.rfutilities.machine.oreScanner;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Map;

public class MessageUpdateScanner implements IMessage
{
    public enum EnumUpdateType
    {
        SCAN_STATUS,
        BLOCKS_SCANNED,
        BLOCKS_TO_SCAN,
        ORE_COUNT
    }

    private BlockPos pos;
    private EnumUpdateType updateType;
    private Object newValue;

    public MessageUpdateScanner() {}

    public MessageUpdateScanner(BlockPos pos, EnumUpdateType updateType, Object newValue)
    {
        this.pos = pos;
        this.updateType = updateType;
        this.newValue = newValue;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        pos = new BlockPos(x, y, z);
        updateType = EnumUpdateType.values()[buf.readByte()];
        switch(updateType)
        {
            case SCAN_STATUS:
                newValue = TileOreScanner.EnumScanStatus.values()[buf.readByte()];
                break;
            case BLOCKS_TO_SCAN:
            case BLOCKS_SCANNED:
                newValue = buf.readInt();
                break;
            case ORE_COUNT:
                int mapLength = buf.readInt();
                if(mapLength <= 0) break;
                HashMap<Block, Integer> oreCount = new HashMap<Block, Integer>();
                for(int i = 0; i < mapLength; i++)
                {
                    Block block = Block.getBlockFromName(ByteBufUtils.readUTF8String(buf));
                    int count = buf.readInt();
                    oreCount.put(block, count);
                }
                newValue = new HashMap<Block, Integer>(oreCount);
                break;
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeByte((byte) updateType.ordinal());
        switch(updateType)
        {
            case SCAN_STATUS:
                buf.writeByte((byte) ((TileOreScanner.EnumScanStatus)newValue).ordinal());
                break;
            case BLOCKS_TO_SCAN:
            case BLOCKS_SCANNED:
                buf.writeInt((Integer) newValue);
                break;
            case ORE_COUNT:
                HashMap<Block, Integer> oreCount = (HashMap<Block, Integer>) newValue;
                buf.writeInt(oreCount.size());
                if(oreCount.size() <= 0) break;
                for(Map.Entry<Block, Integer> entry : oreCount.entrySet())
                {
                    ByteBufUtils.writeUTF8String(buf, entry.getKey().getRegistryName().toString());
                    buf.writeInt(entry.getValue());
                }
                break;
        }
    }

    public static class Handler implements IMessageHandler<MessageUpdateScanner, IMessage>
    {
        @Override
        public IMessage onMessage(final MessageUpdateScanner message, MessageContext ctx)
        {
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    World world = Minecraft.getMinecraft().theWorld;
                    TileEntity te = world.getTileEntity(message.pos);
                    if(!(te instanceof TileOreScanner)) return;
                    TileOreScanner scanner = (TileOreScanner) te;
                    scanner.updateValue(message.updateType, message.newValue);
                }
            });
            return null;
        }
    }
}
