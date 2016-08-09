package com.brightspark.rfutilities.entity;

import com.brightspark.rfutilities.reference.Config;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityLaunchedFireball extends EntityLargeFireball
{
    private float fireballSpeed = 4;

    public EntityLaunchedFireball(World worldIn, EntityLivingBase shooter)
    {
        super(worldIn, shooter, 0, 0, 0);
        Vec3d look = shooter.getLookVec();
        posX = shooter.posX + look.xCoord;
        posY = shooter.posY + 1;
        posZ = shooter.posZ + look.zCoord;
        motionX = look.xCoord * fireballSpeed;
        motionY = look.yCoord * fireballSpeed;
        motionZ = look.zCoord * fireballSpeed;
        accelerationX = accelerationY = accelerationZ = 0;
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (!worldObj.isRemote)
        {
            if (result.entityHit != null && (Config.canFireballLauncherHitShooter && result.entityHit.equals(shootingEntity)))
            {
                result.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, shootingEntity), 6.0F);
                applyEnchantments(shootingEntity, result.entityHit);
            }

            boolean flag = worldObj.getGameRules().getBoolean("mobGriefing");
            worldObj.newExplosion(null, posX, posY, posZ, (float)explosionPower, flag, flag);
            setDead();
        }
    }


}
