package com.bearfininteractive.bearfinutilities.audio;

import com.bearfininteractive.bearfinutilities.init.ItemsRegistry;
import com.bearfininteractive.bearfinutilities.init.SoundsRegistry;
import com.bearfininteractive.bearfinutilities.items.ItemPDA;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PDALoopSound extends MovingSound {
    private final EntityPlayer player;
    private boolean done = false;

    public PDALoopSound(EntityPlayer player) {
        super(SoundsRegistry.IED, SoundCategory.AMBIENT);
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.01f;
        this.pitch = 1.0f;
        this.attenuationType = ISound.AttenuationType.NONE;
        this.xPosF = (float) player.posX;
        this.yPosF = (float) player.posY;
        this.zPosF = (float) player.posZ;
    }

    @Override
    public void update() {
        if (!hasSetUpPDA(player)) {
            done = true;
            return;
        }
        this.xPosF = (float) player.posX;
        this.yPosF = (float) player.posY;
        this.zPosF = (float) player.posZ;
    }

    @Override
    public boolean isDonePlaying() {
        return done;
    }

    private static boolean hasSetUpPDA(EntityPlayer player) {
        ItemStack held = player.getHeldItem(net.minecraft.util.EnumHand.MAIN_HAND);
        return !held.isEmpty() && held.getItem() == ItemsRegistry.PDA && ItemPDA.isSetUp(held);
    }
}
