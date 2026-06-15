package net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.endgame;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.threadParts.ThreadT3PartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries.GregERenederRegistries;
import net.cu5tmtp.GregECore.gregstuff.GregRecipeLogic.MultiThreadedRecipeLogic;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class CartridgeCase extends WorkableElectricMultiblockMachine {

    public boolean canBeThreaded = false;

    public CartridgeCase(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new MultiThreadedRecipeLogic(this, 8);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        for (IMultiPart part : getParts()) {

            if (part instanceof ThreadT3PartMachine) {
                canBeThreaded = true;
                if (getRecipeLogic() instanceof MultiThreadedRecipeLogic logic) {
                    logic.setMultiThreaded(canBeThreaded);
                }
            }
        }
    }

    @Override
    public void onStructureInvalid() {
        canBeThreaded = false;
        if (getRecipeLogic() instanceof MultiThreadedRecipeLogic logic) {
            logic.setMultiThreaded(false);
        }
        super.onStructureInvalid();
    }

    public static MachineDefinition CARTRIDGECASE = REGISTRATE
            .multiblock("cartridgecase", CartridgeCase::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeModifiers(GTRecipeModifiers.PARALLEL_HATCH, GTRecipeModifiers.OC_PERFECT)
            .recipeType(GregERecipeTypes.OPEN_THE_RIFT)
            .appearanceBlock(GCYMBlocks.CASING_ATOMIC)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("aaaaaaaaaaaaa", "acccacccaccca", "acccacccaccca", "acccacccaccca", "aaaaaaaaaaaaa", "acccacccaccca", "acccacccaccca", "acccacccaccca", "aaaaaaaaaaaaa", "acccacccaccca", "acccacccaccca", "acccacccaccca", "aaaaaaaaaaaaa")
                        .aisle("acccacccaccca", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "acccacccaccca", "cbbbcdddcbbbc", "cbbbcdddcbbbc", "cbbbcdddcbbbc", "acccacccaccca", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "acccacccaccca")
                        .aisle("acccacccaccca", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "acccacccaccca", "cbbbcdddcbbbc", "cbbbcdedcbbbc", "cbbbcdddcbbbc", "acccacccaccca", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "acccacccaccca")
                        .aisle("acccacccaccca", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "acccacccaccca", "cbbbcdddcbbbc", "cbbbcdfdcbbbc", "cbbbcdddcbbbc", "acccacccaccca", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "acccacccaccca")
                        .aisle("aaaaaaaaaaaaa", "abbbabbbabbba", "abbbabbbabbba", "abbbabbbabbba", "aaaaaaaaaaaaa", "abbbabbbabbba", "abbbabbbabbba", "abbbabbbabbba", "aaaaaaaaaaaaa", "abbbabbbabbba", "abbbabbbabbba", "abbbabbbabbba", "aaaaaaaaaaaaa")

                        .where("a", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:large_scale_assembler_casing")))
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(32).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(32).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(ThreadT3PartMachine.getPartAbility()).setMaxGlobalLimited(1).setPreviewCount(1)))
                        .where("b", Predicates.any())
                        .where("c", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:sturdy_machine_casing"))))
                        .where("d", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:atomic_casing"))))
                        .where("e", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("ae2:controller"))))
                        .where("f", Predicates.controller(blocks(definition.getBlock())))
                        .build();
            })
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/gcym/atomic_casing"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
                    .andThen(b -> b.addDynamicRenderer(GregERenederRegistries::createRealityEngineRender)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Cartridges, Perfect Overclock, Parallel Hatch and Threading").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Make the 3 following items:").withStyle(style -> style.withColor(0x90EE90)))
            .register();

    public static void init() {
    }
}
