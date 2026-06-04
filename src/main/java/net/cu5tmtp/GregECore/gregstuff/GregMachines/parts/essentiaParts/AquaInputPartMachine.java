package net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.essentiaParts;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;

import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class AquaInputPartMachine extends FluidHatchPartMachine {

    public static final PartAbility AQUA_INPUT = new PartAbility("aqua_input");

    public AquaInputPartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier, IO.IN, 20000, 1);
    }

    public static PartAbility getPartAbility() {
        return AQUA_INPUT;
    }

    public static final MachineDefinition AQUA_INPUT_MACHINE = REGISTRATE.machine(
                    "aqua_input_machine",
                    holder -> new AquaInputPartMachine(holder, GTValues.ULV))
            .rotationState(RotationState.NON_Y_AXIS)
            .abilities(AQUA_INPUT)
            .workableCasingModel(
                    GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GregECore.id("block/overlay/aqua_input"))
            .tooltips(Component.literal("Use this to input Aqua Essentia to Infusion Altar.")
                    .withStyle(ChatFormatting.LIGHT_PURPLE))
            .register();

    public static void init() {
    }
}