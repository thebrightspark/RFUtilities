package com.brightspark.rfutilities.waila;

import com.brightspark.rfutilities.machine.TileMachine;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class Waila
{
    public static void init()
    {
        FMLInterModComms.sendMessage("Waila", "register", "com.brightspark.rfutilities.waila.Waila.callbackRegister");
    }

    public static void callbackRegister(IWailaRegistrar registrar)
    {
        registrar.registerBodyProvider(new WailaProviderEnergyStorage(), TileMachine.class);
    }
}
