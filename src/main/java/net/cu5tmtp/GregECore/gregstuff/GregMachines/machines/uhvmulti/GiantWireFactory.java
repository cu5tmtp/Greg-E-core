package net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.uhvmulti;

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
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.threadParts.ThreadT3PartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregRecipeLogic.MultiThreadedRecipeLogic;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class GiantWireFactory extends WorkableElectricMultiblockMachine {

    public GiantWireFactory(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public boolean canBeThreaded = false;

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        canBeThreaded = false;

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
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new MultiThreadedRecipeLogic(this, 8);
    }

    @Override
    public void onStructureInvalid() {
        canBeThreaded = false;
        if (getRecipeLogic() instanceof MultiThreadedRecipeLogic logic) {
            logic.setMultiThreaded(false);
        }
        super.onStructureInvalid();
    }

    public static final MachineDefinition GIANT_WIRE_FACTORY = REGISTRATE
            .multiblock("giant_wire_factory", GiantWireFactory::new)
            .rotationState(RotationState.ALL)
            .recipeTypes(GTRecipeTypes.WIREMILL_RECIPES)
            .recipeModifiers(GTRecipeModifiers.OC_PERFECT, GTRecipeModifiers.PARALLEL_HATCH)
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("bbaaaaabb", "bbbbcbbbb", "bbbbcbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb")
                    .aisle("badddddab", "bbbbbbbbb", "bbbbbbbbb", "bbbbcbbbb", "bbbbcbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb")
                    .aisle("adadddada", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbcbbbb", "bbbbcbbbb", "bbbbcbbbb", "bbbbcbbbb", "bbbbcbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb")
                    .aisle("addaaadda", "bbbaaabbb", "bbbeeebbb", "bbbeeebbb", "bbbeeebbb", "bbbaaabbb", "bbbeeebbb", "bbbeeebbb", "bbbeeebbb", "bbbaaabbb", "bbbbcbbbb", "bbbbcbbbb", "bbbbbbbbb", "bbbbbbbbb")
                    .aisle("addaaadda", "cbbafabbc", "cbbefebbc", "bcbefebcb", "bcbefebcb", "bbcafacbb", "bbcefecbb", "bbcefecbb", "bbcefecbb", "bbcafacbb", "bbbcdcbbb", "bbbcdcbbb", "bbbbcbbbb", "bbbbcbbbb")
                    .aisle("addaaadda", "bbbaaabbb", "bbbeeebbb", "bbbeeebbb", "bbbeeebbb", "bbbaaabbb", "bbbeeebbb", "bbbeeebbb", "bbbeeebbb", "bbbaaabbb", "bbbbcbbbb", "bbbbcbbbb", "bbbbbbbbb", "bbbbbbbbb")
                    .aisle("adadddada", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbcbbbb", "bbbbcbbbb", "bbbbcbbbb", "bbbbcbbbb", "bbbbcbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb")
                    .aisle("badddddab", "bbbbbbbbb", "bbbbbbbbb", "bbbbcbbbb", "bbbbcbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb")
                    .aisle("bbaahaabb", "bbbbcbbbb", "bbbbcbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb", "bbbbbbbbb")

                    .where("a", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:stress_proof_casing")))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(16).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(1).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(1).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(2))
                            .or(Predicates.abilities(ThreadT3PartMachine.getPartAbility()).setMaxGlobalLimited(1).setPreviewCount(1)))
                    .where("b", Predicates.any())
                    .where("c", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:naquadah_alloy_frame"))))
                    .where("d", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:solid_machine_casing"))))
                    .where("e", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:tempered_glass"))))
                    .where("f", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:steel_gearbox"))))
                    .where('h', Predicates.controller(Predicates.blocks(definition.get())))
                    .build()
            )
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Perfect Overclock, Parallel Hatch and Threading").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Created for ultra-fast wiremilling.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Available Recipe Type: Wiremill").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("Accepts Threading Core T3.").withStyle(ChatFormatting.LIGHT_PURPLE))
            .workableCasingModel(
                    GTCEu.id("block/casings/gcym/stress_proof_casing"),
                    GTCEu.id("block/multiblock/distillation_tower")
            )
            .register();

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        if (getRecipeLogic() instanceof MultiThreadedRecipeLogic logic && logic.isMultiThreaded()) {
            List<MultiThreadedRecipeLogic.RecipeThread> threads = logic.getActiveThreads();
            for (int i = 0; i < threads.size(); i++) {
                var thread = threads.get(i);
                int percent = thread.duration > 0 ? (int) (((float) thread.progress / thread.duration) * 100) : 0;
                textList.add(Component.literal("  Thread " + (i + 1) + ": " + percent + "%").withStyle(ChatFormatting.LIGHT_PURPLE));
            }
        }
    }

    public static void init() {}
}
