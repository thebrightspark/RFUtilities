package com.brightspark.rfutilities.item;

import com.brightspark.rfutilities.machine.AbstractBlockMachine;
import com.brightspark.rfutilities.machine.AbstractBlockMachineDirectional;
import com.brightspark.rfutilities.machine.TileMachine;
import com.brightspark.rfutilities.reference.Names;
import com.brightspark.rfutilities.util.Common;
import com.brightspark.rfutilities.util.LogHelper;
import com.brightspark.rfutilities.util.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemWrench extends ItemBasic
{
    public enum EnumWrenchMode
    {
        TURN(0, "turn"),
        CONFIG_SIDE(1, "configSide");

        public static final String LANG = "wrenchMode.";
        private static EnumWrenchMode[] allModes = new EnumWrenchMode[2];
        public final int id;
        public final String unlocName;

        static
        {
            //Create array of all modes
            for(EnumWrenchMode mode : values())
                allModes[mode.id] = mode;
        }

        EnumWrenchMode(int id, String unlocName)
        {
            this.id = id;
            this.unlocName = LANG + unlocName;
        }

        public static EnumWrenchMode getById(int id)
        {
            return id < 0 || id > allModes.length - 1 ? null : allModes[id];
        }

        public EnumWrenchMode getNextMode()
        {
            return id + 1 > allModes.length - 1 ? getById(0) : getById(id + 1);
        }

        public String getDisplayPrefix()
        {
            return TextFormatting.GOLD + "[" + I18n.format(LANG + "mode") + " " + TextFormatting.YELLOW + I18n.format(unlocName) + TextFormatting.GOLD + "] " + TextFormatting.RESET;
        }
    }

    private static final String KEY_MODE = "mode";
    private final int chatIdWrenchMode = Common.getNewChatMessageId();
    private final int chatIdMachineSide = Common.getNewChatMessageId();

    public ItemWrench()
    {
        super(Names.Items.WRENCH);
        setMaxStackSize(1);
    }

    private static void setMode(ItemStack stack, EnumWrenchMode mode)
    {
        NBTHelper.setInteger(stack, KEY_MODE, mode.id);
    }

    private static void nextMode(ItemStack stack)
    {
        EnumWrenchMode mode = EnumWrenchMode.getById(NBTHelper.getInt(stack, KEY_MODE)).getNextMode();
        NBTHelper.setInteger(stack, KEY_MODE, mode.id);
    }

    public static EnumWrenchMode getMode(ItemStack stack)
    {
        return EnumWrenchMode.getById(NBTHelper.getInt(stack, KEY_MODE));
    }

    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        //This method is not called server-side
        if(!player.isSneaking())
        {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if(block instanceof AbstractBlockMachineDirectional && getMode(stack) == EnumWrenchMode.TURN)
            {
                //Set the facing of the block if it's a machine that can be rotated.
                if(!world.isRemote)
                {
                    LogHelper.info("Setting block facing");
                    AbstractBlockMachineDirectional machine = (AbstractBlockMachineDirectional) block;
                    TileMachine machineTE = machine.getTileEntity(world, pos);
                    ((AbstractBlockMachineDirectional) block).setFacingWithWrench(world, pos, state, side);
                    ((TileMachine) world.getTileEntity(pos)).copyDataFrom(machineTE);
                    return EnumActionResult.SUCCESS;
                }
            }
            else if(block instanceof AbstractBlockMachine && getMode(stack) == EnumWrenchMode.CONFIG_SIDE)
            {
                //Change the input/output mode of the side.
                TileMachine machineTE = ((AbstractBlockMachine)block).getTileEntity(world, pos);
                machineTE.nextEnergySidePerm(side);
                Common.addClientChatMessage(new TextComponentString(machineTE.getEnergyPermForSide(side).getChatDisplay(side)), chatIdMachineSide);
                //player.addChatMessage(new TextComponentString(machineTE.getEnergyPermForSide(side).getChatDisplay(side)));
                return EnumActionResult.SUCCESS;
            }
        }
        return super.onItemUseFirst(stack, player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(player.isSneaking())
        {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if(block instanceof AbstractBlockMachine && ((AbstractBlockMachine)block).canPickupWithWrench())
            {
                //Remove the block if it's a machine
                ((AbstractBlockMachine)block).getTileEntity(world, pos).usedWrenchToBreak = true;
                if(block.removedByPlayer(state, world, pos, player, true))
                    block.harvestBlock(world, player, pos, state, world.getTileEntity(pos), stack);
                return EnumActionResult.SUCCESS;
            }
            else
            {
                //Change wrench mode
                nextMode(stack);
                if(world.isRemote)
                    Common.addClientChatMessage(new TextComponentString(getMode(stack).getDisplayPrefix()), chatIdWrenchMode);
                    //player.addChatMessage(new TextComponentString(getMode(stack).getDisplayPrefix()));
                return EnumActionResult.SUCCESS;
            }
        }
        return super.onItemUse(stack, player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        if(player.isSneaking())
        {
            RayTraceResult ray = rayTrace(world, player, false);
            if(ray == null || rayTrace(world, player, false).typeOfHit != RayTraceResult.Type.BLOCK)
            {
                //Change wrench mode
                nextMode(stack);
                if(world.isRemote)
                    Common.addClientChatMessage(new TextComponentString(getMode(stack).getDisplayPrefix()), chatIdWrenchMode);
                    //player.addChatMessage(new TextComponentString(getMode(stack).getDisplayPrefix()));
                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
            }
        }
        return super.onItemRightClick(stack, world, player, hand);
    }

    /**
     * Allow the item one last chance to modify its name used for the
     * tool highlight useful for adding something extra that can't be removed
     * by a user in the displayed name, such as a mode of operation.
     *
     * @param stack the ItemStack for the item.
     * @param displayName the name that will be displayed unless it is changed in this method.
     */
    public String getHighlightTip(ItemStack stack, String displayName)
    {
        return TextFormatting.DARK_GREEN + getMode(stack).getDisplayPrefix() + TextFormatting.RESET + displayName;
    }
}
