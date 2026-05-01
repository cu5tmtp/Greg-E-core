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

    public static GTRecipeType FORNAX_UNIVERSI_ACCELERETION = GTRecipeTypes.register("fornax_universi_acceleration", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(9,1,0,0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_HAMMER, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.JET_ENGINE);

    public static GTRecipeType ASCENCION_ALTAR_DONATION = GTRecipeTypes.register("ascention_altar_donation", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(9,1,0,0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.FIRE)
            .addDataInfo(data -> {
                if (data.contains("tome")){
                    int tome = data.getInt("tome");

                    String blue = ChatFormatting.BLUE.toString();

                    String tomeName = switch (tome) {
                        case 1 -> "Forbidden Tome Of" + "\n" + blue + "Extraterrestrial Planets";
                        case 2 -> "Forbidden Tome Of" + "\n" + blue + "Mighty Beings";
                        case 3 -> "Forbidden Tome Of" + "\n" + blue + "Hidden Lifeforms";
                        case 4 -> "Forbidden Tome Of Rare Blocks";
                        case 5 -> "Forbidden Tome Of Rare Items";
                        default -> throw new IllegalStateException("Unexpected value: " + tome);
                    };

                    return ChatFormatting.LIGHT_PURPLE + "Tome Needed: " + "\n" + ChatFormatting.BLUE + tomeName.replace("\n", "\n" + ChatFormatting.BLUE);
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

    public static GTRecipeType OPEN_THE_RIFT = GTRecipeTypes.register("open_rift", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(1,1, 0,0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.SCIENCE);

    public static void init(){
    }
}
