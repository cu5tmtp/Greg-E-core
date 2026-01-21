package net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import net.minecraft.ChatFormatting;

public class GregERecipeTypes {
    public static GTRecipeType LAUNCH_SAILS = GTRecipeTypes.register("launch_sails", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(3,3,3,3)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.COOLING);

    public static GTRecipeType GET_SOLAR_SAIL_ENERGY = GTRecipeTypes.register("get_solar_sail_energy", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(3,3,3,3)
            .setEUIO(IO.OUT)
            .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.SCIENCE);
    public static GTRecipeType STAR_MAYKR_SINGULARITIES = GTRecipeTypes.register("star_maykr_singularities", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(9,1,0,0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_HAMMER, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.JET_ENGINE)
            .addDataInfo(data -> {
                if (data.contains("weight")) {
                    double weight = data.getDouble("weight");
                    return ChatFormatting.DARK_PURPLE+ "Star weight cost: " + ChatFormatting.WHITE + (int) weight;
                }
                return null;
            });

    public static GTRecipeType ADVANCED_FUSION = GTRecipeTypes.register("advanced_fusion", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(3,3,3,3)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.SCIENCE)
            .addDataInfo(data -> {
                if (data.contains("heat_level")) {
                    int heat = data.getInt("heat_level");
                    return ChatFormatting.RED + "Heat: " + ChatFormatting.WHITE + (heat - 500) + "K - " + heat + "K";
                }
                return null;
            });

    public static void init(){
    }
}
