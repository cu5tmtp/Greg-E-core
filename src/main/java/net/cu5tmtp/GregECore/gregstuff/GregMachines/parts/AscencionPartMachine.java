package net.cu5tmtp.GregECore.gregstuff.GregMachines.parts;

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
import net.minecraft.resources.ResourceLocation;

import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class AscencionPartMachine extends ItemBusPartMachine {

    public static final PartAbility ASCENCION_HOLDER = new PartAbility("ascencion_holder");

    public AscencionPartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier, IO.BOTH);
    }

    public static PartAbility getPartAbility() {
        return ASCENCION_HOLDER;
    }

    public static final MachineDefinition ASCENCION_PART_MACHINE = REGISTRATE.machine("ascencion_holder", (holder) ->
                    new AscencionPartMachine(holder, GTValues.ULV))
            .rotationState(RotationState.NON_Y_AXIS)
            .abilities(AscencionPartMachine.ASCENCION_HOLDER)
            .workableCasingModel(GregECore.id("block/blankrune"), GregECore.id("block/overlay/feeder"))
            .register();
    public static void init() {
    }
}
