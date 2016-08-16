package com.brightspark.rfutilities.handler;

import com.brightspark.rfutilities.item.ItemWrench;
import com.brightspark.rfutilities.machine.TileMachine;
import com.brightspark.rfutilities.util.Common;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WrenchOverlayHandler
{
    private static final int fontColour = 0xFFFFFF;
    private static final String LANG = ItemWrench.EnumWrenchMode.LANG + "overlay.";

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        ItemStack heldItem = player.getHeldItemMainhand();
        if(heldItem != null && heldItem.getItem() instanceof ItemWrench && event.getType() == RenderGameOverlayEvent.ElementType.TEXT)
        {
            //Do overlay render
            FontRenderer fontRenderer = mc.fontRendererObj;
            ScaledResolution res = event.getResolution();
            int xMid = res.getScaledWidth() / 2;
            int yMid = res.getScaledHeight() / 2 + 50;

            //Render wrench mode
            ItemWrench.EnumWrenchMode wrenchMode = ItemWrench.getMode(heldItem);
            String text = I18n.format(LANG + "mode") + " " + I18n.format(wrenchMode.unlocName);
            fontRenderer.drawStringWithShadow(text, xMid - (fontRenderer.getStringWidth(text) / 2), yMid, fontColour);

            //Render energy side perm for machine being looked at
            RayTraceResult ray = ((ItemWrench)heldItem.getItem()).rayTrace(mc.theWorld, player, false);
            if(ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK)
                return;
            TileEntity te = mc.theWorld.getTileEntity(ray.getBlockPos());
            if(te == null || !(te instanceof TileMachine))
                return;
            TileMachine.SideEnergyPerm side = ((TileMachine)te).getEnergyPermForSide(ray.sideHit);
            text = I18n.format(LANG + "side", Common.capitaliseFirstLetter(ray.sideHit.getName())) + " " + I18n.format(side.unlocName);
            fontRenderer.drawStringWithShadow(text, xMid - (fontRenderer.getStringWidth(text) / 2), yMid + 10, fontColour);
        }
    }
}
