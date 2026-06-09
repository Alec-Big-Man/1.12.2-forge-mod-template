package com.bearfininteractive.bearfinutilities.init;

import com.bearfininteractive.bearfinutilities.Main;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class SoundsRegistry {
    public static SoundEvent IED;

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        IED = register(event, "ied");
    }

    private static SoundEvent register(RegistryEvent.Register<SoundEvent> event, String name) {
        ResourceLocation rl = new ResourceLocation(Main.MODID, name);
        SoundEvent se = new SoundEvent(rl).setRegistryName(rl);
        event.getRegistry().register(se);
        return se;
    }
}
