package com.emilian.pvparsenal.item;

import com.emilian.pvparsenal.PvpArsenal;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item VISION = register("vision",
            new VisionItem(new Item.Settings()
                    .maxCount(1)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(PvpArsenal.MOD_ID, "vision")))));

    public static final Item TIME_CLOCK = register("time_clock",
            new TimeClockItem(new Item.Settings()
                    .maxCount(1)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(PvpArsenal.MOD_ID, "time_clock")))));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(PvpArsenal.MOD_ID, name), item);
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(VISION);
            entries.add(TIME_CLOCK);
        });
        PvpArsenal.LOGGER.info("Registered PvP Arsenal items");
    }
}
