package com.bearfininteractive.bearfinutilities.proxy;

import com.bearfininteractive.bearfinutilities.gui.GuiPDA;
import com.bearfininteractive.bearfinutilities.init.ItemsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ModelLoader.setCustomModelResourceLocation(
            ItemsRegistry.PDA, 0,
            new ModelResourceLocation(ItemsRegistry.PDA.getRegistryName(), "inventory")
        );
    }

    @Override
    public void init(FMLInitializationEvent event) {}

    @Override
    public void openPDAGui(ItemStack stack, EnumHand hand) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiPDA(stack, hand));
    }
}
