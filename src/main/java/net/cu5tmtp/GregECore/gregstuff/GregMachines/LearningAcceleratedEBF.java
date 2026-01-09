package net.cu5tmtp.GregECore.gregstuff.GregMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.managers.DysonSwarmManager;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.managers.LearningAcceleratedEBFManager;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregEModifiers;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_LASER_SAFE_ENGRAVING;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_SHOCK_PROOF;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class LearningAcceleratedEBF extends WorkableElectricMultiblockMachine {
    public LearningAcceleratedEBF(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            LearningAcceleratedEBF.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER
    );
    @Persisted
    protected int tier1 = 0;
    @Persisted
    protected int tier2 = 0;
    @Persisted
    protected int tier3 = 0;
    @Persisted
    protected int tier4 = 0;
    @Persisted
    protected int tier5 = 0;

    int recipeTemp = 0;
    static int currentBoost = 0;
    int coilTemp = 12000;

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        LearningAcceleratedEBFManager.setTierBoosts(tier1, tier2, tier3, tier4, tier5);
        super.onLoad();
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        assert recipe != null;
        recipeTemp = recipe.data.contains("ebf_temp") ? recipe.data.getInt("ebf_temp") : 0;
        return super.beforeWorking(recipe);
    }

    @Override
    public void afterWorking() {

        if (recipeTemp >= 9000) {
            if (tier5 <= 1000) {
                tier5 = Math.min(1000, tier5 + currentBoost);
                LearningAcceleratedEBFManager.addTierProgress(5, currentBoost);
            }
        } else if (recipeTemp >= 7000) {
            if (tier4 <= 2000) {
                tier4 = Math.min(2000, tier4 + currentBoost);
                LearningAcceleratedEBFManager.addTierProgress(4, currentBoost);
            }
        } else if (recipeTemp >= 5000) {
            if (tier3 <= 4000) {
                tier3 = Math.min(4000, tier3 + currentBoost);
                LearningAcceleratedEBFManager.addTierProgress(3, currentBoost);
            }
        } else if (recipeTemp >= 3000) {
            if (tier2 <= 10000) {
                tier2 = Math.min(10000, tier2 + currentBoost);
                LearningAcceleratedEBFManager.addTierProgress(2, currentBoost);
            }
        } else if (recipeTemp >= 1) {
            if (tier1 <= 15000) {
                tier1 = Math.min(15000, tier1 + currentBoost);
                LearningAcceleratedEBFManager.addTierProgress(1, currentBoost);
            }
        }
        super.afterWorking();
    }

    public static MachineDefinition LEARNING_ACC_EBF = REGISTRATE
            .multiblock("learning_acc_ebf", LearningAcceleratedEBF::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.BLAST_RECIPES)
            .recipeModifiers(GregEModifiers::learningEBF, GTRecipeModifiers.OC_PERFECT)
            .appearanceBlock(CASING_SHOCK_PROOF)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("   BBBBB   ", "     H     ", "     H     ", "     H     ", "     H     ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ")
                        .aisle("  BHGGGHB  ", "           ", "           ", "           ", "           ", "     H     ", "     H     ", "     H     ", "   HHDHH   ", "     H     ", "     H     ", "     H     ", "     H     ", "           ", "           ", "           ", "           ", "           ", "           ", "           ")
                        .aisle(" BGHGGGHGB ", "  I     I  ", "  I     I  ", "  I     I  ", "  I     I  ", "  I     I  ", "  I     I  ", "  I     I  ", "  HGGGGGH  ", "           ", "           ", "           ", "           ", "     H     ", "     H     ", "    HDH    ", "           ", "           ", "           ", "    JJJ    ")
                        .aisle("BHHHHHHHHHB", "   FCCCF   ", "   FCCCF   ", "   FCCCF   ", "   EEEEE   ", "   FCCCF   ", "   FCCCF   ", "   FCCCF   ", " HGGHHHGGH ", "   I   I   ", "   I   I   ", "   I   I   ", "   IEEEI   ", "   I   I   ", "   I   I   ", "   HGGGH   ", "   I   I   ", "   I   I   ", "   I   I   ", "   JHHHJ   ")
                        .aisle("BGGHHHHHGGB", "   C   C   ", "   C   C   ", "   C   C   ", "   E   E   ", "   C   C   ", "   C   C   ", "   C   C   ", " HGH   HGH ", "    FCF    ", "    FCF    ", "    FCF    ", "   E   E   ", "    FCF    ", "    FCF    ", "  HGHHHGH  ", "    FCF    ", "    FEF    ", "    FCF    ", "  JHHHHHJ  ")
                        .aisle("BGGHHHHHGGB", "H  C   C  H", "H  C   C  H", "H  C   C  H", "H  E   E  H", " H C   C H ", " H C   C H ", " H C   C H ", " DGH   HGD ", " H  C C  H ", " H  C C  H ", " H  C C  H ", " H E   E H ", "  H C C H  ", "  H C C H  ", "  HGH HGH  ", "    C C    ", "    E E    ", "    C C    ", "  JHHKHHJ  ")
                        .aisle("BGGHHHHHGGB", "   C   C   ", "   C   C   ", "   C   C   ", "   E   E   ", "   C   C   ", "   C   C   ", "   C   C   ", " HGH   HGH ", "    FCF    ", "    FCF    ", "    FCF    ", "   E   E   ", "    FCF    ", "    FCF    ", "  HGHHHGH  ", "    FCF    ", "    FEF    ", "    FCF    ", "  JHHHHHJ  ")
                        .aisle("BHHHHHHHHHB", "   FCCCF   ", "   FCCCF   ", "   FCCCF   ", "   EEEEE   ", "   FCCCF   ", "   FCCCF   ", "   FCCCF   ", " HGGHHHGGH ", "   I   I   ", "   I   I   ", "   I   I   ", "   IEEEI   ", "   I   I   ", "   I   I   ", "   HGGGH   ", "   I   I   ", "   I   I   ", "   I   I   ", "   JHHHJ   ")
                        .aisle(" BGHGGGHGB ", "  I     I  ", "  I     I  ", "  I     I  ", "  I     I  ", "  I     I  ", "  I     I  ", "  I     I  ", "  HGGGGGH  ", "           ", "           ", "           ", "           ", "     H     ", "     H     ", "    HDH    ", "           ", "           ", "           ", "    JJJ    ")
                        .aisle("  BHGGGHB  ", "           ", "           ", "           ", "           ", "     H     ", "     H     ", "     H     ", "   HHDHH   ", "     H     ", "     H     ", "     H     ", "     H     ", "           ", "           ", "           ", "           ", "           ", "           ", "           ")
                        .aisle("   BBABB   ", "     H     ", "     H     ", "     H     ", "     H     ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ")
                        .where('A', Predicates.controller(blocks(definition.getBlock())))
                        .where('B', Predicates.blocks(CASING_SHOCK_PROOF.get())
                                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(2)))
                        .where('C', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gregecore:awakened_draconium_coil"))))
                        .where('D', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gregecore:shockproof_engine"))))
                        .where('E', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gregecore:ptfe_engine_intake"))))
                        .where('F', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gregecore:ptfe_firebox_casing"))))
                        .where('G', Predicates.blocks(CASING_HSSE_STURDY.get()))
                        .where('H', Predicates.blocks(CASING_SHOCK_PROOF.get()))
                        .where('I', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:hsss_frame"))))
                        .where('J', Predicates.blocks(CASING_LASER_SAFE_ENGRAVING.get()))
                        .where('K', Predicates.abilities(PartAbility.MUFFLER).setExactLimit(1).setPreviewCount(1))
                        .where(' ', Predicates.any())
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/gcym/shock_proof_cutting_casing"),
                    GTCEu.id("block/multiblock/distillation_tower"))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Perfect Overclock and Learning").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("This machine is able to optimize its processes, " +
                    "greatly increasing its capabilities by reducing the energy usage, speeding the recipes up and adding more parallels.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("T1: Blast 15000 items with 1K or higher blasting temperature.").withStyle(style -> style.withColor(0xFF8C00)))
            .tooltips(Component.literal("Reward: 50% Energy reduction.").withStyle(style -> style.withColor(0xFF8C00)))
            .tooltips(Component.literal("T2: Blast 10000 items with 3000K or higher blasting temperature.").withStyle(style -> style.withColor(0xFFEE00)))
            .tooltips(Component.literal("Reward: 30% Energy reduction and 50% speed boost.").withStyle(style -> style.withColor(0xFFEE00)))
            .tooltips(Component.literal("T3: Blast 4000 items with 5000K or higher blasting temperature.").withStyle(style -> style.withColor(0x4DE94C)))
            .tooltips(Component.literal("Reward: 15% Energy reduction, 30% speed boost and 16 parallels.").withStyle(style -> style.withColor(0x4DE94C)))
            .tooltips(Component.literal("T4: Blast 2000 items with 7000K or higher blasting temperature.").withStyle(style -> style.withColor(0x3783FF)))
            .tooltips(Component.literal("Reward: 15% speed boost and 80 parallels.").withStyle(style -> style.withColor(0x3783FF)))
            .tooltips(Component.literal("T5: Blast 1000 items with 9000K or higher blasting temperature.").withStyle(style -> style.withColor(0x4815AA)))
            .tooltips(Component.literal("Reward: 160 parallels.").withStyle(style -> style.withColor(0x4815AA)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("You can check your progress in the GUI of the machine controller.").withStyle(style -> style.withColor(0x90EE90)))
            .register();

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        if (isFormed()) {
            textList.add(Component.literal("T1 items smelted: ").withStyle(ChatFormatting.AQUA)
                    .append(tier1 >= 15000 ? Component.literal("DONE!").withStyle(ChatFormatting.GREEN) : Component.literal(tier1 + "/" + 15000).withStyle(ChatFormatting.AQUA)));

            textList.add(Component.literal("T2 items smelted: ").withStyle(ChatFormatting.AQUA)
                    .append(tier2 >= 10000 ? Component.literal("DONE!").withStyle(ChatFormatting.GREEN) : Component.literal(tier2 + "/" + 10000).withStyle(ChatFormatting.AQUA)));

            textList.add(Component.literal("T3 items smelted: ").withStyle(ChatFormatting.AQUA)
                    .append(tier3 >= 4000 ? Component.literal("DONE!").withStyle(ChatFormatting.GREEN) : Component.literal(tier3 + "/" + 4000).withStyle(ChatFormatting.AQUA)));

            textList.add(Component.literal("T4 items smelted: ").withStyle(ChatFormatting.AQUA)
                    .append(tier4 >= 2000 ? Component.literal("DONE!").withStyle(ChatFormatting.GREEN) : Component.literal(tier4 + "/" + 2000).withStyle(ChatFormatting.AQUA)));

            textList.add(Component.literal("T5 items smelted: ").withStyle(ChatFormatting.AQUA)
                    .append(tier5 >= 1000 ? Component.literal("DONE!").withStyle(ChatFormatting.GREEN) : Component.literal(tier5 + "/" + 1000).withStyle(ChatFormatting.AQUA)));
        }
        super.addDisplayText(textList);
    }

    public int getMaxTemp() {
        return this.coilTemp + (100 * Math.max(0, getTier() - GTValues.MV));
    }

    public static void setCurrentBoost(int currentBoostEBF) {
        currentBoost = currentBoostEBF;
    }

    public static void init() {}
}
