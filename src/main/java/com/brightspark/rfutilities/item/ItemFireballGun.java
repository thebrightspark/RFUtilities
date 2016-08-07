package com.brightspark.rfutilities.item;

import com.brightspark.rfutilities.reference.Names;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemFireballGun extends ItemEnergyBasic
{
    private static final int ENERGY_PER_USE = 500;
    private static final int FIREBALL_SPEED = 1;
    private static final int FIREBALL_STRENGTH = 1;

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
            //TODO: Get this fireball moving in a straight line!
            Vec3d look = player.getLookVec();
            EntityLargeFireball fireball = new EntityLargeFireball(world, player, 0, 0, 0);    //, look.xCoord * FIREBALL_SPEED, look.yCoord * FIREBALL_SPEED, look.zCoord * FIREBALL_SPEED);
            fireball.explosionPower = FIREBALL_STRENGTH;
            fireball.posX = player.posX + look.xCoord;
            fireball.posY = player.posY + 1;
            fireball.posZ = player.posZ + look.zCoord;
            fireball.motionX = look.xCoord;
            fireball.motionY = look.yCoord;
            fireball.motionZ = look.zCoord;
            world.spawnEntityInWorld(fireball);
        }

        return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
    }
}
