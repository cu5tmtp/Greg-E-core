package net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.cleanroom;

import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import org.jetbrains.annotations.NotNull;

public class DimensionSimulator extends CleanroomType {
    public static final CleanroomType DIMENSIONAL_SIMULATOR_CLEANROOM = new CleanroomType(
            "dimensional_simulator_cleanroom",
            "gtceu.recipe.cleanroom_dimensional.display_name");

    public DimensionSimulator (@NotNull String name, @NotNull String translationKey) {
        // The super() call triggers the registration logic in the parent class.
        super(name, translationKey);
    }
}
