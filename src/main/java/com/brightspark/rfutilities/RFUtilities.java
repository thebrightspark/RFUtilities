package com.brightspark.rfutilities;

import com.brightspark.rfutilities.handler.ConfigHandler;
import com.brightspark.rfutilities.handler.GuiHandler;
import com.brightspark.rfutilities.handler.WrenchOverlayHandler;
import com.brightspark.rfutilities.init.RFUBlocks;
import com.brightspark.rfutilities.init.RFUItems;
import com.brightspark.rfutilities.init.RFURecipes;
import com.brightspark.rfutilities.machine.oreScanner.MessageUpdateScanner;
import com.brightspark.rfutilities.reference.Reference;
import com.brightspark.rfutilities.waila.Waila;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class RFUtilities
{
    @Mod.Instance(Reference.MOD_ID)
    public static RFUtilities instance;

    public static final CreativeTabs RFU_TAB = new CreativeTabs(Reference.MOD_ID)
    {
        @Override
        public Item getTabIconItem()
        {
            return Items.REDSTONE;
        }

        @Override
        public String getTranslatedTabLabel()
        {
            return Reference.MOD_NAME;
        }
    };

    public static SimpleNetworkWrapper NETWORK;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //Initialize item, blocks and configs here

        ConfigHandler.init(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new ConfigHandler());

        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
        NETWORK.registerMessage(MessageUpdateScanner.Handler.class, MessageUpdateScanner.class, 0, Side.CLIENT);

        RFUItems.init();
        RFUBlocks.init();
        if(event.getSide() == Side.CLIENT)
        {
            RFUItems.regModels();
            RFUBlocks.regModels();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        //Initialize textures/models, GUIs, tile entities, recipies, event handlers here

        RFUBlocks.regTileEntities();
        RFURecipes.init();

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new WrenchOverlayHandler());

        if(Loader.isModLoaded("Waila"))
            Waila.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //Run stuff after mods have initialized here

    }
}
