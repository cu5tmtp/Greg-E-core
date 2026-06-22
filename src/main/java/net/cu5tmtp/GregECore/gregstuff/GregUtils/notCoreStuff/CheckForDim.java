package net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff;

import com.gregtechceu.gtceu.api.capability.ICleanroomReceiver;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.machine.multiblock.part.CleaningMaintenanceHatchPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.cleanroom.DimensionSimulator;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.misc.DimensionalRelicsPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.single.DimensionalCleaningMaintenance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CheckForDim {
    private static final Map<String, String> DIMENSION_KEYS = new HashMap<>();

    static {
        DIMENSION_KEYS.put("twilightforest:twilight_portal_miniature_structure", "gettwair");
        DIMENSION_KEYS.put("cataclysm:abyss_eye", "abyssal");
        DIMENSION_KEYS.put("cataclysm:void_eye", "enderium");
        DIMENSION_KEYS.put("minecraft:nether_star", "nether");
        DIMENSION_KEYS.put("cataclysm:mech_eye", "forge_smoke");
        DIMENSION_KEYS.put("cataclysm:storm_eye", "lightning");
        DIMENSION_KEYS.put("cataclysm:cursed_eye", "cursed");
        DIMENSION_KEYS.put("cataclysm:flame_eye", "ignitium");
        DIMENSION_KEYS.put("minecraft:dragon_egg", "ender_air");
        DIMENSION_KEYS.put("bloodmagic:rawdemonite", "getdemoinicaroi");
        DIMENSION_KEYS.put("kubejs:burialmask", "getmarsairrrrrr");
    }

    public static GTRecipe applyDimensionalBypass(MetaMachine metaMachine, GTRecipe r, RecipeModifier oldModifier) {
        var bypassedRecipe = r;
        Set<String> unlockedDimensions = new HashSet<>();
        boolean hasDimensionCleanroom = false;
        MultiblockControllerMachine targetController = null;

        if (metaMachine instanceof ICleanroomReceiver receiver) {
            var physicalCleanroom = receiver.getCleanroom();
            if (physicalCleanroom != null && physicalCleanroom.getTypes().contains(DimensionSimulator.DIMENSIONAL_SIMULATOR_CLEANROOM)) {
                hasDimensionCleanroom = true;
                if (physicalCleanroom instanceof MultiblockControllerMachine physicalController) {
                    targetController = physicalController;
                } else if (metaMachine instanceof MultiblockControllerMachine metaController) {
                    targetController = metaController;
                }
            }
        }

        if (!hasDimensionCleanroom && metaMachine instanceof MultiblockControllerMachine controller) {
            for (IMultiPart part : controller.getParts()) {
                if (part instanceof CleaningMaintenanceHatchPartMachine hatch) {
                    if (hatch.getDefinition() == DimensionalCleaningMaintenance.DIMENSIONAL_CLEANING_HATCH) {
                        hasDimensionCleanroom = true;
                        targetController = controller;
                        break;
                    }
                }
            }
        }

        if (hasDimensionCleanroom && targetController != null) {
            for (IMultiPart part : targetController.getParts()) {
                if (part instanceof DimensionalRelicsPartMachine) {
                    for (var handlerList : part.getRecipeHandlers()) {
                        for (Object handler : handlerList.getCapability(ItemRecipeCapability.CAP)) {
                            if (handler instanceof IItemHandler itemHandler) {
                                for (int i = 0; i < itemHandler.getSlots(); i++) {
                                    net.minecraft.world.item.ItemStack stack = itemHandler.getStackInSlot(i);
                                    if (!stack.isEmpty()) {
                                        ResourceLocation itemIdRes = ForgeRegistries.ITEMS.getKey(stack.getItem());
                                        if (itemIdRes != null) {
                                            String itemId = itemIdRes.toString();
                                            if (DIMENSION_KEYS.containsKey(itemId)) {
                                                unlockedDimensions.add(DIMENSION_KEYS.get(itemId));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!unlockedDimensions.isEmpty()) {
            bypassedRecipe = r.copy();

            bypassedRecipe.conditions.removeIf(condition -> {
                if (condition.getClass().getSimpleName().toLowerCase().contains("dimension")) {
                    String conditionData = condition.toString().toLowerCase();
                    String recipeId = r.getId().toString().toLowerCase();

                    for (String dim : unlockedDimensions) {
                        if (conditionData.contains(dim) || recipeId.contains(dim)) {
                            return true;
                        }
                    }
                }
                return false;
            });
        }

        if (oldModifier != null && bypassedRecipe != null) {
            return oldModifier.applyModifier(metaMachine, bypassedRecipe);
        }
        return bypassedRecipe;
    }
}