package net.cu5tmtp.GregECore.gregstuff.GregMachines.hpca;

import com.gregtechceu.gtceu.api.capability.IHPCACoolantProvider;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.part.hpca.HPCAComponentPartMachine;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;

public class GregECoolerPart extends HPCAComponentPartMachine implements IHPCACoolantProvider {

    public GregECoolerPart(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public boolean isAdvanced() {
        return false;
    }

    @Override
    public ResourceTexture getComponentIcon() {
        return GuiTextures.HPCA_ICON_ACTIVE_COOLER_COMPONENT;
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
    public boolean canBeDamaged() {
        return false;
    }

    @Override
    public int getCoolingAmount() {
        return 17;
    }

    @Override
    public boolean isActiveCooler() {
        return true;
    }

    @Override
    public int getMaxCoolantPerTick() {
        return 32;
    }

}
