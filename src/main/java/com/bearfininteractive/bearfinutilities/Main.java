package com.bearfininteractive.bearfinutilities;

import com.bearfininteractive.bearfinutilities.command.CommandPDA;
import com.bearfininteractive.bearfinutilities.init.ItemsRegistry;
import com.bearfininteractive.bearfinutilities.network.ModNetwork;
import com.bearfininteractive.bearfinutilities.proxy.CommonProxy;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main {
    public static final String MODID = "bearfinutilities";
    public static final String NAME = "BearfinUtilities";
    public static final String VERSION = "1.0.0";

    @Mod.Instance
    public static Main instance;

    @SidedProxy(
        serverSide = "com.bearfininteractive.bearfinutilities.proxy.CommonProxy",
        clientSide = "com.bearfininteractive.bearfinutilities.proxy.ClientProxy"
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModNetwork.register();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandPDA());
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            ItemsRegistry.registerItems(event.getRegistry());
        }
    }
}
