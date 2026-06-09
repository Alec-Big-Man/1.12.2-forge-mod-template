package com.bearfininteractive.bearfinutilities.network;

import com.bearfininteractive.bearfinutilities.items.ItemPDA;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSavePDAAccount implements IMessage {
    private int hand;
    private String username;
    private String color;

    public PacketSavePDAAccount() {}

    public PacketSavePDAAccount(EnumHand hand, String username, String color) {
        this.hand = hand == EnumHand.MAIN_HAND ? 0 : 1;
        this.username = username;
        this.color = color;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(hand);
        ByteBufUtils.writeUTF8String(buf, username);
        ByteBufUtils.writeUTF8String(buf, color);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hand = buf.readInt();
        username = ByteBufUtils.readUTF8String(buf);
        color = ByteBufUtils.readUTF8String(buf);
    }

    public static class Handler implements IMessageHandler<PacketSavePDAAccount, IMessage> {
        @Override
        public IMessage onMessage(PacketSavePDAAccount msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                EnumHand hand = msg.hand == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                ItemStack stack = player.getHeldItem(hand);
                if (!stack.isEmpty() && stack.getItem() instanceof ItemPDA) {
                    ItemPDA.saveAccount(stack, msg.username, msg.color);
                }
            });
            return null;
        }
    }
}
