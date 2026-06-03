package net.cu5tmtp.GregECore.gregstuff.GregMachines.hpca;

import com.gregtechceu.gtceu.api.capability.IHPCAComputationProvider;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.part.hpca.HPCAComputationPartMachine;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;

public class GregEComputationPart extends HPCAComputationPartMachine implements IHPCAComputationProvider {

    public GregEComputationPart(IMachineBlockEntity holder) {
        super(holder, true);
    }

    @Override
    public ResourceTexture getComponentIcon() {
        if (isDamaged()) {
            return GuiTextures.HPCA_ICON_DAMAGED_COMPUTATION_COMPONENT;
        }
        return GuiTextures.HPCA_ICON_ADVANCED_COMPUTATION_COMPONENT;
    }

    @Override
    public boolean isAdvanced() {
        return false;
    }

    @Override
    public int getUpkeepEUt() {
        return 150000;
    }

    @Override
    public int getMaxEUt() {
        return 2500000;
    }

    @Override
    public int getCWUPerTick() {
        if (isDamaged()) return 16;
        return 128;
    }

    @Override
    public int getCoolingPerTick() {
        return 32;
    }
}
