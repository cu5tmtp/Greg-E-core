package net.cu5tmtp.GregECore.gregstuff.GregMachines.parts;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.gui.editor.EditableMachineUI;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class AdvancedParallelBoosterPartMachine extends TieredPartMachine {

    public static final PartAbility ADVANCED_PARALLEL_BOOSTER = new PartAbility("advanced_parallel_booster");

    public AdvancedParallelBoosterPartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
    }

    public static PartAbility getPartAbility() {
        return ADVANCED_PARALLEL_BOOSTER;
    }

    public static final MachineDefinition ADVANCED_PARALLEL_BOOSTER_MACHINE = REGISTRATE.machine("advanced_parallel_booster_machine", (holder) ->
                    new AdvancedParallelBoosterPartMachine(holder, GTValues.ULV))
            .rotationState(RotationState.NON_Y_AXIS)
            .abilities(AdvancedParallelBoosterPartMachine.ADVANCED_PARALLEL_BOOSTER)
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_robust_tungstensteel"), GregECore.id("block/overlay/advanced_parallel_booster"))
            .tooltips(Component.literal("Use this to quadruple the parallels that the machine can achieve.").withStyle(ChatFormatting.LIGHT_PURPLE))
            .editableUI(new EditableMachineUI("advanced_parallel_booster", GregECore.id("advanced_parallel_booster"), WidgetGroup::new, (group, machine) -> {
                group.addWidget(new LabelWidget(-4, 1, "Parallel multiplier: 4"));
            }))
            .register();


    public static void init() {
    }
}
