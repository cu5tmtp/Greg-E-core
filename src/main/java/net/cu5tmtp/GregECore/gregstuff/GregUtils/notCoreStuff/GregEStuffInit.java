package net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff;

import net.cu5tmtp.GregECore.gregstuff.GregMachines.*;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.AdvancedParallelBoosterPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.ParallelBoosterPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.StarFeederPartMachine;
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
        GregERenederRegistries.init();
    }

    public static void initGregParts(){
        StarFeederPartMachine.init();
        ParallelBoosterPartMachine.init();
        AdvancedParallelBoosterPartMachine.init();
    }
}
