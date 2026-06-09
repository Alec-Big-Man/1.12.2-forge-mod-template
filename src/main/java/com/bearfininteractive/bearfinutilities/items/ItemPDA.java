package com.bearfininteractive.bearfinutilities.items;

import com.bearfininteractive.bearfinutilities.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemPDA extends Item {
    public ItemPDA() {
        setTranslationKey("pda");
        setRegistryName(Main.MODID, "pda");
        setCreativeTab(CreativeTabs.TOOLS);
        setMaxStackSize(1);
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(
        World world,
        EntityPlayer player,
        EnumHand hand) {
        if (world.isRemote) {
            Main.proxy.openPDAGui(player.getHeldItem(hand), hand);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(
            hand));
    }


    public static boolean isSetUp(ItemStack stack) {
        if (!stack.hasTagCompound())
            return false;
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt.hasKey("pda_username") && !nbt.getString("pda_username")
            .trim().isEmpty() && nbt.hasKey("pda_color");
    }


    public static String getUsername(ItemStack stack) {
        if (!stack.hasTagCompound())
            return "";
        return stack.getTagCompound().getString("pda_username");
    }


    public static String getColor(ItemStack stack) {
        if (!stack.hasTagCompound())
            return "§f";
        String c = stack.getTagCompound().getString("pda_color");
        return c.isEmpty() ? "§f" : c;
    }


    public static void saveAccount(
        ItemStack stack,
        String username,
        String color) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString("pda_username", username);
        stack.getTagCompound().setString("pda_color", color);
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(
            "§l§bGlobal Uplink Established."));
    }
}
