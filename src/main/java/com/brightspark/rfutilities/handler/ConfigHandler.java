package com.brightspark.rfutilities.handler;

import com.brightspark.rfutilities.reference.Config;
import com.brightspark.rfutilities.reference.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class ConfigHandler
{
    public static class Categories
    {
        public static final String GENERAL = Configuration.CATEGORY_GENERAL;
        public static final String ITEMS = "items";
        public static final String BLOCKS = "blocks";
        public static final String BLOCKS_GENERAL = BLOCKS + ".general";
        public static final String MACHINES = "machines";
        public static final String MACHINES_GENERAL = MACHINES + ".general";
    }

    public static Configuration configuration;

    public static void init(File configFile)
    {
        if(configuration == null)
        {
            configuration = new Configuration(configFile);
            loadConfiguration();
        }
    }

    private static void loadConfiguration()
    {
        //Items

        Config.canFireballLauncherHitShooter = configuration.getBoolean("canFireballLauncherHitShooter", Categories.ITEMS, Config.canFireballLauncherHitShooter, "Whether a fireball shot from the Fireball Launcher can hit the shooter (explosion can still hurt shooter)");

        //Machines

        Config.machineEnergyStorage = configuration.getInt("machineEnergyStorage", Categories.MACHINES_GENERAL, Config.machineEnergyStorage, 1000, 1000000, "The energy storage buffer for most of the machines");
        Config.machineEnergyTransferRate = configuration.getInt("machineEnergyTransferRate", Categories.MACHINES_GENERAL, Config.machineEnergyTransferRate, 1, 1000000, "The max energy transfer rate for most of the machines");

        if(configuration.hasChanged())
            configuration.save();
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if(event.getModID().equalsIgnoreCase(Reference.MOD_ID))
            //Resync configs
            loadConfiguration();
    }
}
