package net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.threadParts;

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

public class ThreadT2PartMachine extends TieredPartMachine {

    public static final PartAbility THREADING_2 = new PartAbility("threading_tier_two");

    public ThreadT2PartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
    }

    public static PartAbility getPartAbility() {
        return THREADING_2;
    }

    public static final MachineDefinition THREADING_2_MACHINE = REGISTRATE.machine("threading_two_machine", (holder) ->
                    new ThreadT2PartMachine(holder, GTValues.ULV))
            .rotationState(RotationState.NON_Y_AXIS)
            .abilities(ThreadT2PartMachine.THREADING_2)
            .workableCasingModel(GTCEu.id("block/casings/firebox/machine_casing_firebox_tungstensteel"), GregECore.id("block/overlay/threading/threading_2"))
            .tooltips(Component.literal("Use this to enable threading on the compatible machines.").withStyle(ChatFormatting.LIGHT_PURPLE))
            .editableUI(new EditableMachineUI("threading_tier_two", GregECore.id("threading_tier_two"), WidgetGroup::new, (group, machine) -> {
                group.addWidget(new LabelWidget(-40, 1, "This machine can now use"));
                group.addWidget(new LabelWidget(0, 29, "§5Threading!"));
            }))
            .register();

    public static void init() {
    }
}
