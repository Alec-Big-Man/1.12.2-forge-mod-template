package com.bearfininteractive.bearfinutilities.events;

import com.bearfininteractive.bearfinutilities.Main;
import com.bearfininteractive.bearfinutilities.init.ItemsRegistry;
import com.bearfininteractive.bearfinutilities.items.ItemPDA;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class ChatEventHandler {

    public static final Set<UUID> PROXIMITY_MODE = new HashSet<>();
    private static final double PROXIMITY_RANGE = 250.0;
    private static final List<String> MSG_COMMANDS = Arrays.asList("msg", "tell", "w");

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        EntityPlayerMP player = event.getPlayer();

        if (PROXIMITY_MODE.contains(player.getUniqueID())) {
            event.setCanceled(true);
            sendProximityChat(player, event.getMessage());
            return;
        }

        ItemStack pdaStack = findPDA(player);
        if (pdaStack == null) {
            event.setCanceled(true);
            player.sendMessage(new TextComponentString(
                "§cYou need a PDA to use global chat. Use /pda mode to switch to proximity chat."));
            return;
        }
        if (!isHoldingPDA(player)) {
            event.setCanceled(true);
            player.sendMessage(new TextComponentString("§4Hold your PDA to chat."));
            return;
        }
        pdaStack = player.getHeldItem(EnumHand.MAIN_HAND);
        if (!ItemPDA.isSetUp(pdaStack)) {
            event.setCanceled(true);
            player.sendMessage(new TextComponentString("§cSet up your PDA to type in chat."));
            return;
        }
        String username = ItemPDA.getUsername(pdaStack);
        String color = ItemPDA.getColor(pdaStack);
        event.setComponent(new TextComponentString(color + "<" + username + ">§r " + event.getMessage()));
    }

    private static void sendProximityChat(EntityPlayerMP sender, String message) {
        TextComponentString tag = new TextComponentString("[Proximity]");
        tag.setStyle(new Style()
            .setColor(TextFormatting.GREEN)
            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new TextComponentString("Use /pda mode to switch to global chat"))));

        TextComponentString body = new TextComponentString("<" + sender.getName() + "> " + message);

        TextComponentString combined = new TextComponentString("");
        combined.appendSibling(tag);
        combined.appendSibling(body);

        MinecraftServer server = sender.getServer();
        double rangeSq = PROXIMITY_RANGE * PROXIMITY_RANGE;
        for (EntityPlayerMP nearby : server.getPlayerList().getPlayers()) {
            if (nearby.dimension == sender.dimension && nearby.getDistanceSq(sender) <= rangeSq) {
                nearby.sendMessage(combined);
            }
        }
    }

    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        if (!MSG_COMMANDS.contains(event.getCommand().getName())) return;
        if (!(event.getSender() instanceof EntityPlayerMP)) return;

        EntityPlayerMP sender = (EntityPlayerMP) event.getSender();

        if (!canChat(sender)) {
            event.setCanceled(true);
            sender.sendMessage(new TextComponentString(getBlockReason(sender)));
            return;
        }

        String[] params = event.getParameters();
        if (params.length == 0) return;

        EntityPlayerMP target = sender.getServer().getPlayerList().getPlayerByUsername(params[0]);
        if (target == null) return;

        if (findPDA(target) == null) {
            event.setCanceled(true);
            sender.sendMessage(new TextComponentString("§c" + target.getName() + " doesn't have a PDA."));
        }
    }

    private static boolean canChat(EntityPlayer player) {
        if (!isHoldingPDA(player)) return false;
        ItemStack held = player.getHeldItem(EnumHand.MAIN_HAND);
        return ItemPDA.isSetUp(held);
    }

    private static String getBlockReason(EntityPlayer player) {
        if (findPDA(player) == null) return "§cYou need a PDA to use /msg.";
        if (!isHoldingPDA(player)) return "§4Hold your PDA to use /msg.";
        return "§cSet up your PDA to use /msg.";
    }

    private static boolean isHoldingPDA(EntityPlayer player) {
        ItemStack held = player.getHeldItem(EnumHand.MAIN_HAND);
        return !held.isEmpty() && held.getItem() == ItemsRegistry.PDA;
    }

    private static ItemStack findPDA(EntityPlayer player) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && stack.getItem() == ItemsRegistry.PDA) return stack;
        }
        for (ItemStack stack : player.inventory.offHandInventory) {
            if (!stack.isEmpty() && stack.getItem() == ItemsRegistry.PDA) return stack;
        }
        return null;
    }
}
