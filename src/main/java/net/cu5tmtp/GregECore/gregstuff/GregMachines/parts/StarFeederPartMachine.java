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

import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class StarFeederPartMachine extends ItemBusPartMachine {

    public static final PartAbility STAR_FEEDER = new PartAbility("star_feeder");

    public StarFeederPartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier, IO.BOTH);
    }

    public static PartAbility getPartAbility() {
        return STAR_FEEDER;
    }

    public static final MachineDefinition STAR_FEEDER_MACHINE = REGISTRATE.machine("star_feeder", (holder) ->
                    new StarFeederPartMachine(holder, GTValues.UHV))
            .rotationState(RotationState.ALL)
            .abilities(StarFeederPartMachine.STAR_FEEDER)
            .workableCasingModel(GTCEu.id("block/casings/gcym/atomic_casing"), GregECore.id("block/overlay/feeder"))
            .register();


    public static void init() {
    }
}
