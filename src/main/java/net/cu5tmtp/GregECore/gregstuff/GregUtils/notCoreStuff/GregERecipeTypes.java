package net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.ui.GTRecipeTypeUI;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;

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

    public static GTRecipeType INFUSION_ALTAR_INFUSING = GTRecipeTypes.register("infusion_altar_infusing", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(9,3,6,0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.FIRE);
    
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
            .setSound(GTSoundEntries.SCIENCE)
            .addDataInfo(data -> ChatFormatting.RED + "Needs all 3 eyes inserted.");

    public static GTRecipeType SEND_UP_THE_MATS = GTRecipeTypes.register("send_up_the_mats", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(6,6, 3,3)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.SCIENCE)
            .addDataInfo(data -> {
                if (data.contains("height_level")) {
                    int height = data.getInt("height_level");
                    return ChatFormatting.GREEN + "Height: " + ChatFormatting.GOLD + (height - 10) + ChatFormatting.GREEN + " KM - " + ChatFormatting.GOLD + height + ChatFormatting.GREEN + " KM";
                }
                return null;
            });

    public static GTRecipeType DEEP_SPACE_EXPLORE = GTRecipeTypes.register("deep_space_explore", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(6,6, 6,6)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.SCIENCE)
            .addDataInfo(data -> {
                List<String> info = new ArrayList<>();

                if (data.contains("drone")) {
                    int drone = data.getInt("drone");
                    String droneName = switch (drone) {
                        case 1 -> "Milano";
                        case 2 -> "Bebop";
                        case 3 -> "Cepheus";
                        default -> "Unknown";
                    };
                    info.add(ChatFormatting.GOLD + "Drone: " + ChatFormatting.GREEN + droneName);
                }

                if (data.contains("system")) {
                    int system = data.getInt("system");
                    String systemName = switch (system) {
                        case 1 -> "61 Cygni";
                        case 2 -> "Struve 2398";
                        case 3 -> "Lacaille 8760";
                        case 4 -> "Gliese 1";
                        case 5 -> "70 Ophiuchi";
                        case 6 -> "Stein 2051";
                        default -> "Unknown";
                    };
                    info.add(ChatFormatting.GOLD + "Solar System: " + ChatFormatting.GREEN + systemName);
                }

                return info.isEmpty() ? null : String.join("\n", info);
            });

    public static void init(){
    }
}
