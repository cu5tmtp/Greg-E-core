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
            .tooltips(Component.literal("Items and Recipe Unlocks:").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("Eye of Abyss -> Abyssal Air").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Eye of Void -> Enderium Air").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Eye of Mech -> Forge Smoke").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Eye of Storm -> Captured Lightning").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Eye of Curse -> Cursed Air").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Eye of Flame -> Ignitium Infused Lava").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Miniature Twilight Forest Portal -> Twilight Forest Air:").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Raw Demonite -> Demonic Air").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Delerian Burial Mask -> Mars Air").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Nether Star -> Nether Air").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Dragon Egg -> End Air").withStyle(ChatFormatting.LIGHT_PURPLE))
            .register();

    public static void init() {
    }
}
