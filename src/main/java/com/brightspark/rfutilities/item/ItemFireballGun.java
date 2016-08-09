package com.brightspark.rfutilities.item;

import com.brightspark.rfutilities.entity.EntityLaunchedFireball;
import com.brightspark.rfutilities.reference.Names;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemFireballGun extends ItemEnergyBasic
{
    private static final int ENERGY_PER_USE = 500;

    public ItemFireballGun()
    {
        super(Names.Items.FIREBALL_GUN);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        //Only work if enough energy
        if(getEnergyStored(stack) < ENERGY_PER_USE)
            return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);

        //Draw energy for use
        if(!player.isCreative())
            extractEnergy(stack, ENERGY_PER_USE, false);

        //Spawn fireball
        if(!world.isRemote)
        {
            EntityLaunchedFireball fireball = new EntityLaunchedFireball(world, player);
            world.spawnEntityInWorld(fireball);
        }

        return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
    }
}
