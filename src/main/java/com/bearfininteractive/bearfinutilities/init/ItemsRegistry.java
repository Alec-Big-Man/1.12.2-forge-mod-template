package com.bearfininteractive.bearfinutilities.init;

import com.bearfininteractive.bearfinutilities.items.ItemPDA;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemsRegistry {
    public static final ItemPDA PDA = new ItemPDA();

    public static void registerItems(IForgeRegistry<Item> registry) {
        registry.register(PDA);
    }
}
