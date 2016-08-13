package com.brightspark.rfutilities.machine;

import com.brightspark.rfutilities.reference.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiEnergyStorage extends GuiScreen
{
    private static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID, Reference.GUI_TEXTURE_DIR + "energyStorage.png");
    private TileMachine machine;
    private int xSize = 31;
    private int ySize = 66;
    private int xBarSize = 15;
    private int yBarSize = 50;

    public GuiEnergyStorage(TileMachine machine)
    {
        this.machine = machine;
        //setGuiSize(64, 96);
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        //TODO: Fix the background rendering
        //Draw gui background
        mc.getTextureManager().bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        //Draw power bar
        //int pixelsHigh = Math.round(yBarSize * machine.getEnergyPercentage()); //The energy bar is 50 pixels high
        //int correctYPos = yBarSize - pixelsHigh;
        //drawTexturedModalRect(x + 8, y + 8 + correctYPos, 31, correctYPos, xBarSize, pixelsHigh);

        //drawCenteredString();


    }
}
