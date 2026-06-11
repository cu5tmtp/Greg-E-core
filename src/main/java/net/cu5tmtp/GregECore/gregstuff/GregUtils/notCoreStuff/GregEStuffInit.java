package net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff;

import net.cu5tmtp.GregECore.gregstuff.GregMachines.*;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.hpca.GregEHPCAInit;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.*;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.essentiaParts.*;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries.GregERenederRegistries;

public class GregEStuffInit {
    public static void initGregEMulti(){
        AcceleratedEBF.init();
        GiantAcceleratedEBF.init();
        GiantChemicalReactor.init();
        DysonSwarmLauncher.init();
        DysonSwarmEnergyCollector.init();
        LearningAcceleratedEBF.init();
        EnhancedFusionReactor.init();
        StarMaykr.init();
        FornaxUniversi.init();
        EnhancedBlastChiller.init();
        BigFreezer.init();
        AscencionAltar.init();
        RealityFractureEngine.init();
        SpaceElevator.init();
        DeepSpaceExplorer.init();
        InfusionAltar.init();
        FissionReactor.init();
    }

    public static void initGregEParts(){
        StarFeederPartMachine.init();
        ParallelBoosterPartMachine.init();
        AdvancedParallelBoosterPartMachine.init();
        AdvancedCoolantInputPartMachine.init();
        RealityFractureEnginePartMachine.init();
        BacteriaInputPartMachine.init();
        CoolantInputPartMachine.init();
        RepairPartsInputPartMachine.init();
        AscencionPartMachine.init();
        AdvancedHeaterInputPartMachine.init();
        DroneAccessHatchPartMachine.init();
        PedestalPartMachine.init();
        AerInputPartMachine.init();
        AquaInputPartMachine.init();
        TerraInputPartMachine.init();
        OrdoInputPartMachine.init();
        IgnisInputPartMachine.init();
        PerditioInputPartMachine.init();
    }

    public static void initHPCAParts(){
        GregEHPCAInit.init();
    }

    public static void initGregERenderRegistries(){
        GregERenederRegistries.init();
    }
}
