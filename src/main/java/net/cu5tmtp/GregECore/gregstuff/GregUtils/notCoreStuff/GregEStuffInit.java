package net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff;

import net.cu5tmtp.GregECore.gregstuff.GregMachines.*;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.*;
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
    }

    public static void initGregEParts(){
        StarFeederPartMachine.init();
        ParallelBoosterPartMachine.init();
        AdvancedParallelBoosterPartMachine.init();
        AdvancedCoolantInputPartMachine.init();
        BacteriaInputPartMachine.init();
        CoolantInputPartMachine.init();
        RepairPartsInputPartMachine.init();
    }

    public static void initGregERenderRegistries(){
        GregERenederRegistries.init();
    }
}
