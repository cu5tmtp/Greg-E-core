package net.cu5tmtp.GregECore.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.MOD_ID;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final RegistryObject<Item> PARALLEL_BACTERIA = ITEMS.register("parallel_bacteria",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ENERGY_BACTERIA = ITEMS.register("energy_bacteria",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SPEED_BACTERIA = ITEMS.register("speed_bacteria",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ULTIMATE_BACTERIA = ITEMS.register("ultimate_bacteria",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SOLAR_SAIL = ITEMS.register("solar_sail",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SOLAR_ACTIVATOR = ITEMS.register("solar_activator",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> QUANTUM_ACCELERATOR = ITEMS.register("quantum_accelerator",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SERVER_RACK = ITEMS.register("server_rack",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ROCKET_CONE = ITEMS.register("rocket_cone",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BRASS_PELLET = ITEMS.register("brass_pellet",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> NEUTRONIUM_PELLET = ITEMS.register("neutronium_pellet",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> AMERICIUM_PELLET = ITEMS.register("americium_pellet",
            () -> new Item(new Item.Properties()));

    //Tomes
    public static final RegistryObject<Item> TOME1 = ITEMS.register("tome1",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TOME2 = ITEMS.register("tome2",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TOME3 = ITEMS.register("tome3",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TOME4 = ITEMS.register("tome4",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TOME5 = ITEMS.register("tome5",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PURPLEEYE = ITEMS.register("purpleeye",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> REDEYE = ITEMS.register("redeye",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> GREENEYE = ITEMS.register("greeneye",
            () -> new Item(new Item.Properties()));

}

