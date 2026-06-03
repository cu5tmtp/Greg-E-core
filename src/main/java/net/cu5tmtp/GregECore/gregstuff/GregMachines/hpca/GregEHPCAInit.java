package net.cu5tmtp.GregECore.gregstuff.GregMachines.hpca;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

import static com.gregtechceu.gtceu.common.data.machines.GTResearchMachines.OVERHEAT_TOOLTIPS;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createHPCAPartModel;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class GregEHPCAInit {

    //Thank you to Pheonixvine, who made his GitHub public and I shamelessly learned how to make HPCA there

    static MachineDefinition GREGE_COOLER_COMPONENT = null;
    static MachineDefinition GREGE_COMPUTATION_COMPONENT = null;

    static {
        GREGE_COOLER_COMPONENT = registerCoolingHPCAPart(
                "grege_heat_sink_component", "Greg-E Active Cooling Component",
                GregECoolerPart::new, false)
                .tooltips(
                        Component.translatable("gtceu.machine.hpca.component_general.upkeep_eut", 150000),
                        Component.translatable("gtceu.machine.hpca.component_general.max_eut", 2500000),
                        Component.translatable("gtceu.machine.hpca.component_type.cooler_active"),
                        Component.translatable("gtceu.machine.hpca.component_type.cooler_active_coolant", 32, GTMaterials.get(GTMaterials.PCBCoolant.getName()).getLocalizedName()),
                        Component.translatable("gtceu.machine.hpca.component_type.cooler_cooling", 17),
                        Component.translatable("gtceu.part_sharing.disabled"))
                .register();

        GREGE_COMPUTATION_COMPONENT = registerComputationHPCAPart(
                "grege_computation_component", "Greg-E Computation Component",
                GregEComputationPart::new, true)
                .tooltips(
                        Component.translatable("gtceu.machine.hpca.component_general.upkeep_eut", 150000),
                        Component.translatable("gtceu.machine.hpca.component_general.max_eut", 2500000),
                        Component.translatable("gtceu.machine.hpca.component_type.computation_cwut", 128),
                        Component.translatable("gtceu.machine.hpca.component_type.computation_cooling", 32),
                        Component.translatable("gtceu.part_sharing.disabled"))
                .tooltipBuilder(OVERHEAT_TOOLTIPS)
                .register();
    }

    private static MachineBuilder<MachineDefinition, ?> registerCoolingHPCAPart(String name, String displayName,
                                                                             Function<IMachineBlockEntity, MetaMachine> constructor,
                                                                             boolean isAdvanced) {
        return REGISTRATE.machine(name, constructor)
                .langValue(displayName)
                .rotationState(RotationState.ALL)
                .abilities(PartAbility.HPCA_COMPONENT)
                .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                .modelProperty(GTMachineModelProperties.IS_HPCA_PART_DAMAGED, false)
                .modelProperty(GTMachineModelProperties.IS_ACTIVE, false)
                .model(createHPCAPartModel(isAdvanced,
                        GregECore.id("block/overlay/cooler/fine/active_cooler"),
                        GregECore.id("block/overlay/cooler/broken/damaged_active")));
    }
    private static MachineBuilder<MachineDefinition, ?> registerComputationHPCAPart(String name, String displayName,
                                                                                 Function<IMachineBlockEntity, MetaMachine> constructor, boolean isAdvanced) {
        return REGISTRATE.machine(name, constructor)
                .langValue(displayName)
                .rotationState(RotationState.ALL)
                .abilities(PartAbility.HPCA_COMPONENT)
                .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                .modelProperty(GTMachineModelProperties.IS_HPCA_PART_DAMAGED, false)
                .modelProperty(GTMachineModelProperties.IS_ACTIVE, false)
                .model(createHPCAPartModel(isAdvanced,
                        GregECore.id("block/overlay/computation/fine/advanced_computation"),
                        GregECore.id("block/overlay/computation/broken/advanced_computation_broken")));
    }

    public static void init() {}
}
