package com.brightspark.rfutilities.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.model.ModelLoader;

public class Common
{
    private static Minecraft mc = Minecraft.getMinecraft();
    private static int chatMessageId = 1;

    //Register a model
    public static void regModel(Item item)
    {
        regModel(item, 0);
    }
    public static void regModel(Block block)
    {
        regModel(Item.getItemFromBlock(block), 0);
    }

    //Register a model with meta
    public static void regModel(Item item, int meta)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    /**
     * Spawns a particle in the world
     */
    public static void spawnEffect(Particle particle)
    {
        mc.effectRenderer.addEffect(particle);
    }

    /**
     * Returns a string of the inputted number with commas added to group the digits.
     */
    public static String addDigitGrouping(int number)
    {
        return addDigitGrouping(Integer.toString(number));
    }

    public static String addDigitGrouping(String number)
    {
        String output = number;
        for(int i = number.length() - 3; i > 0; i -= 3)
            output = output.substring(0, i) + "," + output.substring(i);
        return output;
    }

    public static String capitaliseFirstLetter(String text)
    {
        if(text == null || text.length() <= 0)
            return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static int getNewChatMessageId()
    {
        return chatMessageId++;
    }

    public static void addClientChatMessage(ITextComponent message, int deleteId)
    {
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(message, deleteId);
    }
}
