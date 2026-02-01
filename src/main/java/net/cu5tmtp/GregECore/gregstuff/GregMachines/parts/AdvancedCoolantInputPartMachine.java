package net.cu5tmtp.GregECore.gregstuff.GregMachines.parts;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.gui.editor.EditableMachineUI;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class AdvancedCoolantInputPartMachine extends FluidHatchPartMachine {

    public static final PartAbility ADVANCED_COOLANT_INPUT = new PartAbility("advanced_coolant_input");

    public AdvancedCoolantInputPartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier, IO.BOTH, 20000, 1);
    }

    public static PartAbility getPartAbility() {
        return ADVANCED_COOLANT_INPUT;
    }

    public static final MachineDefinition ADVANCED_COOLANT_INPUT_MACHINE = REGISTRATE.machine("advanced_coolant_input_machine", (holder) ->
                    new AdvancedCoolantInputPartMachine(holder, GTValues.UHV))
            .rotationState(RotationState.NON_Y_AXIS)
            .abilities(AdvancedCoolantInputPartMachine.ADVANCED_COOLANT_INPUT)
            .workableCasingModel(GTCEu.id("draconium_fusion"), GregECore.id("block/overlay/feeder"))
            .tooltips(Component.literal("Use this to input coolant into advanced machines.").withStyle(ChatFormatting.LIGHT_PURPLE))
            .register();

    public static void init() {
    }
}
