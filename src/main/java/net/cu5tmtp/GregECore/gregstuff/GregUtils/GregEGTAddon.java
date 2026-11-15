package net.cu5tmtp.GregECore.gregstuff.GregUtils;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.addon.events.KJSRecipeKeyEvent;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import net.cu5tmtp.GregECore.block.GreggyBlocks;
import net.cu5tmtp.GregECore.item.GreggyItems;
import net.minecraft.world.item.Items;
import net.minecraft.data.recipes.FinishedRecipe;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.endgame.IntentionHatchPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.endgame.MEDemandHatchPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregRecipeLogic.WorldEaterReport;
import net.cu5tmtp.GregECore.gregstuff.GregRecipeLogic.WorldEaterScanDomain;
import net.cu5tmtp.GregECore.gregstuff.GregRecipeLogic.WorldEaterTamerRecipeLogic;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.recipe.GregERecipeTypes;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unused")
@GTAddon
public class GregEGTAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return GregECore.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        GreggyBlocks.init();
    }

    @Override
    public String addonModId() {
        return GregECore.MOD_ID;
    }

    @Override
    public void registerTagPrefixes() {}

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
    }

    @Override
    public void registerElements() {
        IGTAddon.super.registerElements();
    }

    @Override
    public void registerRecipeCapabilities() {
    }

    @Override
    public void registerRecipeKeys(KJSRecipeKeyEvent event) {
    }
}
