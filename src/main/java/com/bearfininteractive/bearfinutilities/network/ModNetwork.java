package com.bearfininteractive.bearfinutilities.network;

import com.bearfininteractive.bearfinutilities.Main;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModNetwork {
    public static SimpleNetworkWrapper CHANNEL;

    public static void register() {
        CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Main.MODID);
        CHANNEL.registerMessage(PacketSavePDAAccount.Handler.class, PacketSavePDAAccount.class, 0, Side.SERVER);
    }
}
