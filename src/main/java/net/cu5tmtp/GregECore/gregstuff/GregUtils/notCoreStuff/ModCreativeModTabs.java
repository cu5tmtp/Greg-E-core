package net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff;

import net.cu5tmtp.GregECore.block.ModBlocks;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.chillers.BigFreezer;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.chillers.EnhancedBlastChiller;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.dyson.DysonSwarmEnergyCollector;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.dyson.DysonSwarmLauncher;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.furnaces.AcceleratedEBF;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.furnaces.GiantAcceleratedEBF;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.furnaces.LearningAcceleratedEBF;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.endgame.*;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.misc.InfusionAltar;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.reactors.EnhancedFusionReactor;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.reactors.FissionReactor;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.reactors.GiantChemicalReactor;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.*;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;
import net.cu5tmtp.GregECore.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GregECore.MOD_ID);

    public static final RegistryObject<CreativeModeTab> GREGE_TAB = CREATIVE_MODE_TABS.register("gregecoretab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.LINEARACCELERATOR.get()))
                    .title(Component.literal("Greg-E-core"))
                    .displayItems((pParameters, pOutput) -> {
                        ModBlocks.TAB_BLOCKS.forEach(block -> pOutput.accept(block.get()));
                        pOutput.accept(ModItems.ENERGY_BACTERIA.get());
                        pOutput.accept(ModItems.SPEED_BACTERIA.get());
                        pOutput.accept(ModItems.PARALLEL_BACTERIA.get());
                        pOutput.accept(ModItems.ULTIMATE_BACTERIA.get());
                        pOutput.accept(ModItems.SOLAR_ACTIVATOR.get());
                        pOutput.accept(ModItems.SOLAR_SAIL.get());
                        pOutput.accept(ModItems.QUANTUM_ACCELERATOR.get());
                        pOutput.accept(ModItems.ROCKET_CONE.get());
                        pOutput.accept(ModItems.SERVER_RACK.get());
                        pOutput.accept(ModItems.BRASS_PELLET.get());
                        pOutput.accept(ModItems.AMERICIUM_PELLET.get());
                        pOutput.accept(ModItems.NEUTRONIUM_PELLET.get());
                        pOutput.accept(ModItems.TOME1.get());
                        pOutput.accept(ModItems.TOME2.get());
                        pOutput.accept(ModItems.TOME3.get());
                        pOutput.accept(ModItems.TOME4.get());
                        pOutput.accept(ModItems.TOME5.get());
                        pOutput.accept(ModItems.REDEYE.get());
                        pOutput.accept(ModItems.GREENEYE.get());
                        pOutput.accept(ModItems.PURPLEEYE.get());
                        pOutput.accept(ModItems.UNSTABLE.get());
                        pOutput.accept(ModItems.SPACESHIP1.get());
                        pOutput.accept(ModItems.SPACESHIP2.get());
                        pOutput.accept(ModItems.SPACESHIP3.get());
                        pOutput.accept(AcceleratedEBF.ACCELERATEDEBF.getItem());
                        pOutput.accept(GiantAcceleratedEBF.GIANTACCELERATEDEBF.getItem());
                        pOutput.accept(EnhancedFusionReactor.ENHANCED_FUSION_REACTOR.getItem());
                        pOutput.accept(EnhancedBlastChiller.ENHANCEDBLASTCHILLER.getItem());
                        pOutput.accept(BigFreezer.BIGFREEZER.getItem());
                        pOutput.accept(StarMaykr.STAR_MAYKR.getItem());
                        pOutput.accept(RealityFractureEngine.REALITYFRACTUREENGINE.getItem());
                        pOutput.accept(AscencionAltar.ASCENCION_ALTAR.getItem());
                        pOutput.accept(GiantChemicalReactor.GIANTCHR.getItem());
                        pOutput.accept(DysonSwarmLauncher.DYSON_SWARM_LAUNCHER.getItem());
                        pOutput.accept(DysonSwarmEnergyCollector.DYSON_SWARM_LAUNCHER.getItem());
                        pOutput.accept(LearningAcceleratedEBF.LEARNING_ACC_EBF.getItem());
                        pOutput.accept(DeepSpaceExplorer.DEEPSPACEEXPLORER.getItem());
                        pOutput.accept(FornaxUniversi.FORNAX_UNIVERSI.getItem());
                        pOutput.accept(FissionReactor.FISSIONREACTOR.getItem());
                        pOutput.accept(StarFeederPartMachine.STAR_FEEDER_MACHINE.getItem());
                        pOutput.accept(DroneAccessHatchPartMachine.DRONE_ACCESS_MACHINE.getItem());
                        pOutput.accept(ParallelBoosterPartMachine.PARALLEL_BOOSTER_MACHINE.getItem());
                        pOutput.accept(RepairPartsInputPartMachine.REPAIR_PART_INPUT_MACHINE.getItem());
                        pOutput.accept(AdvancedParallelBoosterPartMachine.ADVANCED_PARALLEL_BOOSTER_MACHINE.getItem());
                        pOutput.accept(CoolantInputPartMachine.COOLANT_INPUT_MACHINE.getItem());
                        pOutput.accept(BacteriaInputPartMachine.BACTERIAL_INPUT_MACHINE.getItem());
                        pOutput.accept(AdvancedCoolantInputPartMachine.ADVANCED_COOLANT_INPUT_MACHINE.getItem());
                        pOutput.accept(AdvancedHeaterInputPartMachine.ADVANCED_HEATER_INPUT_MACHINE.getItem());
                        pOutput.accept(AscencionPartMachine.ASCENCION_PART_MACHINE.getItem());
                        pOutput.accept(RealityFractureEnginePartMachine.REALITY_FRACTURE_PART_MACHINE.getItem());
                        pOutput.accept(SpaceElevator.SPACE_ELEVATOR.getItem());
                        pOutput.accept(InfusionAltar.INFUSION_ALTAR.getItem());
                        pOutput.accept(PedestalPartMachine.PEDESTAL_INF_MACHINE.getItem());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
