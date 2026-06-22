package net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.cleanroom;

import com.gregtechceu.gtceu.api.block.IFilterType;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import org.jetbrains.annotations.NotNull;

public enum DimensionalFilterType implements IFilterType {

    FILTER_CASING_DIMENSIONAL("dimensional_filter_casing", 1, DimensionSimulator.DIMENSIONAL_SIMULATOR_CLEANROOM);

    private final String name;
    private final int tier;
    private final CleanroomType cleanroomType;

    DimensionalFilterType(String name, int tier, CleanroomType cleanroomType) {
        this.name = name;
        this.tier = tier;
        this.cleanroomType = cleanroomType;
    }

    @NotNull
    @Override
    public String getSerializedName() {
        return this.name;
    }

    @NotNull
    @Override
    public String toString() {
        return getSerializedName();
    }

    @Override
    public @NotNull CleanroomType getCleanroomType() {
        return cleanroomType;
    }
}
