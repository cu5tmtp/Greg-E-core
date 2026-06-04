package net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.essentiaParts;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public abstract class EssentiaInputPartMachine extends FluidHatchPartMachine {

    public EssentiaInputPartMachine(IMachineBlockEntity holder, int tier, IO io, int initialCapacity, int slots, Object... args) {
        super(holder, tier, io, initialCapacity, slots, args);
    }

    protected abstract Fluid getAcceptedFluid();

    @Override
    public Predicate<FluidStack> getFluidCapFilter(@Nullable Direction side, IO io) {
        Predicate<FluidStack> parent = super.getFluidCapFilter(side, io);

        return fluid -> parent.test(fluid) && fluid.getFluid().isSame(getAcceptedFluid());
    }
}
