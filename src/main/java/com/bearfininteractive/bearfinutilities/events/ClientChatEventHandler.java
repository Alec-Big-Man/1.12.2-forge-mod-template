package com.bearfininteractive.bearfinutilities.events;

import com.bearfininteractive.bearfinutilities.Main;
import com.bearfininteractive.bearfinutilities.audio.PDALoopSound;
import com.bearfininteractive.bearfinutilities.init.ItemsRegistry;
import com.bearfininteractive.bearfinutilities.items.ItemPDA;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Main.MODID)
public class ClientChatEventHandler {

    private static PDALoopSound currentIEDSound = null;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) {
            currentIEDSound = null;
            return;
        }
        if (currentIEDSound == null || currentIEDSound.isDonePlaying()) {
            if (hasSetUpPDA(player)) {
                currentIEDSound = new PDALoopSound(player);
                mc.getSoundHandler().playSound(currentIEDSound);
            }
        }
    }


    @SubscribeEvent
    public static void onClientChat(ClientChatEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null || !hasSetUpPDA(player))
            return;
        playWav();
        playWav();
    }


    // type CHAT = player messages, SYSTEM = server notices (join/leave, death,
    // etc.)
    @SubscribeEvent
    public static void onClientChatReceived(ClientChatReceivedEvent event) {
        if (event.getType() != ChatType.CHAT)
            return;
        EntityPlayer player = Minecraft.getMinecraft().player;
        boolean hasASetupPDA = hasPDA(player).getItem() == ItemsRegistry.PDA && ItemPDA.isSetUp(hasPDA(player));
        if (player != null && !hasASetupPDA) {
            event.setCanceled(true);
        }
    }


    private static boolean hasSetUpPDA(EntityPlayer player) {
        ItemStack held = player.getHeldItem(
            net.minecraft.util.EnumHand.MAIN_HAND);
        return !held.isEmpty() && held.getItem() == ItemsRegistry.PDA && ItemPDA
            .isSetUp(held);
    }


    private static ItemStack hasPDA(EntityPlayer player) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && stack.getItem() == ItemsRegistry.PDA)
                return stack;
        }
        for (ItemStack stack : player.inventory.offHandInventory) {
            if (!stack.isEmpty() && stack.getItem() == ItemsRegistry.PDA)
                return stack;
        }
        return null;
    }


    private static void playWav() {
        try {
            InputStream is = ClientChatEventHandler.class.getResourceAsStream(
                "/assets/bearfinutilities/sounds/sel.wav");
            if (is == null)
                return;
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(
                new BufferedInputStream(is));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            audioIn.close();
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl)clip.getControl(
                    FloatControl.Type.MASTER_GAIN);
                float db = 20.0f * (float)Math.log10(0.5f); // 50% amplitude ≈
                                                            // -6 dB
                gain.setValue(Math.max(gain.getMinimum(), db));
            }
            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP)
                    clip.close();
            });
            clip.start();
        }
        catch (UnsupportedAudioFileException | LineUnavailableException
            | IOException e) {
            // silent fail
        }
    }
}
