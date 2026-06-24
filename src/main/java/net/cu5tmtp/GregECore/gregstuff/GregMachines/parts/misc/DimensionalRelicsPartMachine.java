package net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.misc;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class DimensionalRelicsPartMachine extends ItemBusPartMachine {

    public DimensionalRelicsPartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier, IO.BOTH);
    }

    public static PartAbility getPartAbility() {
        return PartAbility.PASSTHROUGH_HATCH;
    }

    public static final MachineDefinition DIMENSIONAL_RELICS_MACHINE = REGISTRATE.machine("dimensional_relics_machine", (holder) ->
                    new DimensionalRelicsPartMachine(holder, GTValues.HV))
            .rotationState(RotationState.NON_Y_AXIS)
            .abilities(PartAbility.PASSTHROUGH_HATCH)
            .colorOverlayTieredHullModel(GregECore.id("block/overlay/feeder/overlay_front"))
            .tier(GTValues.HV)
            .tooltips(Component.literal("Use this to input items to unlock dimensional recipes.").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Items and Recipe Unlocks:").withStyle(ChatFormatting.AQUA))
            .tooltips(Component.literal("Eye of Pride").withStyle(ChatFormatting.LIGHT_PURPLE)
                    .append(Component.literal(" -> ").withStyle(ChatFormatting.RED))
                    .append(Component.literal("Forge Smoke, Abyssal Air, Small Pile Of Impure Ancient Metal Dust and Ignitium Infused Lava").withStyle(ChatFormatting.BLUE)))
            .tooltips(Component.literal("Eye of Sin").withStyle(ChatFormatting.LIGHT_PURPLE)
                    .append(Component.literal(" -> ").withStyle(ChatFormatting.RED))
                    .append(Component.literal("Enderium Air, Empty Lava Power Cell Imitation, Captured Lightning and Cursed Air").withStyle(ChatFormatting.BLUE)))
            .tooltips(Component.literal("Stone of Horus").withStyle(ChatFormatting.LIGHT_PURPLE)
                    .append(Component.literal(" -> ").withStyle(ChatFormatting.RED))
                    .append(Component.literal("Earth Orbit, Venus Orbit, Mercury Orbit and Mars Orbit").withStyle(ChatFormatting.BLUE)))
            .tooltips(Component.literal("Charm of Guilliman").withStyle(ChatFormatting.LIGHT_PURPLE)
                    .append(Component.literal(" -> ").withStyle(ChatFormatting.RED))
                    .append(Component.literal("Neptune Orbit, Uranus Orbit, Saturn Orbit, Jupiter Orbit and Sedna Sample Dust").withStyle(ChatFormatting.BLUE)))
            .tooltips(Component.literal("Miniature Twilight Forest Portal").withStyle(ChatFormatting.LIGHT_PURPLE)
                    .append(Component.literal(" -> ").withStyle(ChatFormatting.RED))
                    .append(Component.literal("Twilight Forest Air").withStyle(ChatFormatting.BLUE)))
            .tooltips(Component.literal("Raw Demonite").withStyle(ChatFormatting.LIGHT_PURPLE)
                    .append(Component.literal(" -> ").withStyle(ChatFormatting.RED))
                    .append(Component.literal("Demonic Air").withStyle(ChatFormatting.BLUE)))
            .tooltips(Component.literal("Delerian Burial Mask").withStyle(ChatFormatting.LIGHT_PURPLE)
                    .append(Component.literal(" -> ").withStyle(ChatFormatting.RED))
                    .append(Component.literal("Mars Air").withStyle(ChatFormatting.BLUE)))
            .tooltips(Component.literal("Nether Star").withStyle(ChatFormatting.LIGHT_PURPLE)
                    .append(Component.literal(" -> ").withStyle(ChatFormatting.RED))
                    .append(Component.literal("Nether Air").withStyle(ChatFormatting.BLUE)))
            .tooltips(Component.literal("Dragon Egg").withStyle(ChatFormatting.LIGHT_PURPLE)
                    .append(Component.literal(" -> ").withStyle(ChatFormatting.RED))
                    .append(Component.literal("End Air").withStyle(ChatFormatting.BLUE)))
            .register();

    public static void init() {
    }
}
