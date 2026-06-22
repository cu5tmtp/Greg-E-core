package net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.single;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.common.machine.multiblock.part.CleaningMaintenanceHatchPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.cleanroom.DimensionSimulator;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class DimensionalCleaningMaintenance {

    public static MachineDefinition DIMENSIONAL_CLEANING_HATCH = null;

    static {
            DIMENSIONAL_CLEANING_HATCH = REGISTRATE
                    .machine("dimension_maintenance_hatch",
                            holder -> new CleaningMaintenanceHatchPartMachine(holder,
                                    DimensionSimulator.DIMENSIONAL_SIMULATOR_CLEANROOM))
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.MAINTENANCE)
                    .tooltips(Component.translatable("gtceu.part_sharing.disabled"),
                            Component.translatable("gtceu.machine.maintenance_hatch_cleanroom_auto.tooltip.0"),
                            Component.translatable("gtceu.machine.maintenance_hatch_cleanroom_auto.tooltip.1"))
                    .tooltipBuilder((stack, tooltips) -> tooltips.add(Component.literal("  ").append(Component
                            .translatable(DimensionSimulator.DIMENSIONAL_SIMULATOR_CLEANROOM.getTranslationKey())
                            .withStyle(ChatFormatting.RED))))
                    .tier(GTValues.HV)
                    .colorOverlayTieredHullModel(GregECore.id("block/overlay/dimension_clean/overlay_front"))
                    .register();

    }

    public static void init() {}

}
