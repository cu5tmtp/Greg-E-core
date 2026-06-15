package net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.endgame;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.threadParts.ThreadT3PartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregRecipeLogic.MultiThreadedRecipeLogic;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class CartridgeCase extends WorkableElectricMultiblockMachine {

    public static final GTRecipeType[] ALL_POSSIBLE_RECIPES = new GTRecipeType[]{
            GTRecipeTypes.EXTRUDER_RECIPES, GTRecipeTypes.BENDER_RECIPES, GTRecipeTypes.COMPRESSOR_RECIPES,
            GTRecipeTypes.FORGE_HAMMER_RECIPES, GTRecipeTypes.FORMING_PRESS_RECIPES, GTRecipeTypes.WIREMILL_RECIPES,
            GTRecipeTypes.ASSEMBLER_RECIPES, GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES,
            GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES, GTRecipeTypes.ORE_WASHER_RECIPES, GTRecipeTypes.CHEMICAL_BATH_RECIPES,
            GTRecipeTypes.EXTRACTOR_RECIPES, GTRecipeTypes.CANNER_RECIPES, GTRecipeTypes.AUTOCLAVE_RECIPES,
            GTRecipeTypes.SIFTER_RECIPES, GTRecipeTypes.THERMAL_CENTRIFUGE_RECIPES, GTRecipeTypes.CENTRIFUGE_RECIPES,
            GTRecipeTypes.POLARIZER_RECIPES, GTRecipeTypes.ELECTROMAGNETIC_SEPARATOR_RECIPES, GTRecipeTypes.ELECTROLYZER_RECIPES,
            GTRecipeTypes.MACERATOR_RECIPES, GTRecipeTypes.MIXER_RECIPES, GTRecipeTypes.LASER_ENGRAVER_RECIPES,
            GTRecipeTypes.CUTTER_RECIPES, GTRecipeTypes.LATHE_RECIPES, GTRecipeTypes.ARC_FURNACE_RECIPES,
            GTRecipeTypes.BREWING_RECIPES, GTRecipeTypes.FERMENTING_RECIPES, GTRecipeTypes.FLUID_HEATER_RECIPES
    };

    public boolean canBeThreaded = false;
    private TickableSubscription logicSubscription;
    private Set<GTRecipeType> allowedRecipeTypes = new LinkedHashSet<>(Set.of(GregERecipeTypes.CARTRIDGECASENEEDSTOBEEMPTY));

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

        boolean hasThreadPart = false;

        for (IMultiPart part : getParts()) {

            if (part instanceof ThreadT3PartMachine) {
                hasThreadPart = true;
                break;
            }
        }

        this.canBeThreaded = hasThreadPart;

        if (getRecipeLogic() instanceof MultiThreadedRecipeLogic logic) {
            logic.setMultiThreaded(this.canBeThreaded);
        }

        updateAllowedRecipeTypes();

        if (this.logicSubscription == null) {
            this.logicSubscription = this.subscribeServerTick(this::manageLogic);
        }
    }

    @Override
    public void onStructureInvalid() {

        this.canBeThreaded = false;

        if (getRecipeLogic() instanceof MultiThreadedRecipeLogic logic) {
            logic.setMultiThreaded(false);
        }

        this.allowedRecipeTypes = new LinkedHashSet<>(Set.of(GregERecipeTypes.CARTRIDGECASENEEDSTOBEEMPTY));

        if (this.logicSubscription != null) {
            this.logicSubscription.unsubscribe();
            this.logicSubscription = null;
        }
        super.onStructureInvalid();
    }

    private void manageLogic() {
        if (isFormed && getOffsetTimer() % 100 == 0) {
            updateAllowedRecipeTypes();
        }
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);

        if (isFormed()) {
            textList.add(Component.literal("Active Cartridge:").withStyle(ChatFormatting.AQUA));
            boolean foundAny = false;

            if (getLevel() != null) {
                BlockPos center = getPos();
                Direction left = getFrontFacing().getClockWise();
                Direction right = left.getOpposite();
                BlockPos[] checkPositions = new BlockPos[]{
                        center.relative(left, 4),
                        center.relative(right, 4),
                        center.above(4),
                        center.below(4),
                        center.relative(left, 4).above(4),
                        center.relative(right, 4).above(4),
                        center.relative(left, 4).below(4),
                        center.relative(right, 4).below(4)
                };

                for (BlockPos checkPos : checkPositions) {
                    if (getLevel().getBlockEntity(checkPos) instanceof IMachineBlockEntity mbe) {
                        if (mbe.getMetaMachine() instanceof BoxMachines cartridge) {
                            if (cartridge.isFormed()) {
                                textList.add(Component.literal(" - ")
                                        .append(Component.translatable("block.gregecore." + cartridge.getDefinition().getName()))
                                        .withStyle(ChatFormatting.GRAY));
                                foundAny = true;
                            }
                        }
                    }
                }
            }

            if (!foundAny) {
                textList.add(Component.literal(" - None").withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    private void updateAllowedRecipeTypes() {
        Set<GTRecipeType> newTypes = new LinkedHashSet<>();

        if (getLevel() != null) {

            BlockPos center = getPos();
            Direction left = getFrontFacing().getClockWise();
            Direction right = left.getOpposite();

            BlockPos[] checkPositions = new BlockPos[]{
                    center.relative(left, 4),
                    center.relative(right, 4),
                    center.above(4),
                    center.below(4),
                    center.relative(left, 4).above(4),
                    center.relative(right, 4).above(4),
                    center.relative(left, 4).below(4),
                    center.relative(right, 4).below(4)
            };

            for (BlockPos checkPos : checkPositions) {
                if (getLevel().getBlockEntity(checkPos) instanceof IMachineBlockEntity mbe) {
                    if (mbe.getMetaMachine() instanceof BoxMachines cartridge) {
                        if (cartridge.isFormed()) {
                            addRecipeTypesForCartridge(cartridge.getDefinition(), newTypes);
                        }
                    }
                }
            }
        }

        if (newTypes.isEmpty()) {
            newTypes.add(GregERecipeTypes.CARTRIDGECASENEEDSTOBEEMPTY);
        }

        if (!newTypes.equals(this.allowedRecipeTypes)) {

            this.allowedRecipeTypes = newTypes;

            if (getRecipeLogic() != null) {

                getRecipeLogic().markLastRecipeDirty();

                GTRecipe currentRecipe = getRecipeLogic().getLastRecipe();

                if (getRecipeLogic().isWorking() && currentRecipe != null) {
                    if (!this.allowedRecipeTypes.contains(currentRecipe.getType())) {
                        getRecipeLogic().setProgress(0);
                    }
                }
            }
        }
    }

    private void addRecipeTypesForCartridge(MachineDefinition def, Set<GTRecipeType> types) {
        switch (def.getName()) {
            case "machine_one":
                types.add(GTRecipeTypes.EXTRUDER_RECIPES);
                types.add(GTRecipeTypes.BENDER_RECIPES);
                types.add(GTRecipeTypes.COMPRESSOR_RECIPES);
                types.add(GTRecipeTypes.FORGE_HAMMER_RECIPES);
                types.add(GTRecipeTypes.FORMING_PRESS_RECIPES);
                types.add(GTRecipeTypes.WIREMILL_RECIPES);
                break;
            case "machine_two":
                types.add(GTRecipeTypes.ASSEMBLER_RECIPES);
                types.add(GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES);
                break;
            case "machine_three":
                types.add(GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES);
                types.add(GTRecipeTypes.ORE_WASHER_RECIPES);
                types.add(GTRecipeTypes.CHEMICAL_BATH_RECIPES);
                types.add(GTRecipeTypes.EXTRACTOR_RECIPES);
                types.add(GTRecipeTypes.CANNER_RECIPES);
                types.add(GTRecipeTypes.AUTOCLAVE_RECIPES);
                break;
            case "machine_four":
                types.add(GTRecipeTypes.SIFTER_RECIPES);
                types.add(GTRecipeTypes.THERMAL_CENTRIFUGE_RECIPES);
                types.add(GTRecipeTypes.CENTRIFUGE_RECIPES);
                break;
            case "machine_five":
                types.add(GTRecipeTypes.POLARIZER_RECIPES);
                types.add(GTRecipeTypes.ELECTROMAGNETIC_SEPARATOR_RECIPES);
                types.add(GTRecipeTypes.ELECTROLYZER_RECIPES);
                break;
            case "machine_six":
                types.add(GTRecipeTypes.MACERATOR_RECIPES);
                break;
            case "machine_seven":
                types.add(GTRecipeTypes.MIXER_RECIPES);
                break;
            case "machine_eight":
                types.add(GTRecipeTypes.LASER_ENGRAVER_RECIPES);
                break;
            case "machine_nine":
                types.add(GTRecipeTypes.CUTTER_RECIPES);
                types.add(GTRecipeTypes.LATHE_RECIPES);
                break;
            case "machine_ten":
                types.add(GTRecipeTypes.ARC_FURNACE_RECIPES);
                break;
            case "machine_eleven":
                types.add(GTRecipeTypes.BREWING_RECIPES);
                types.add(GTRecipeTypes.FERMENTING_RECIPES);
                types.add(GTRecipeTypes.FLUID_HEATER_RECIPES);
                break;
        }
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, @NotNull GTRecipe recipe) {
        if (machine instanceof CartridgeCase cartridgeCase) {
            if (!cartridgeCase.allowedRecipeTypes.contains(recipe.getType())) {
                return r -> null;
            }
        }
        return r -> r;
    }

    public static MachineDefinition CARTRIDGECASE = REGISTRATE.multiblock("cartridgecase", CartridgeCase::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeModifiers(GTRecipeModifiers.PARALLEL_HATCH, GTRecipeModifiers.OC_PERFECT, CartridgeCase::recipeModifier)
            .recipeTypes(ALL_POSSIBLE_RECIPES)
            .appearanceBlock(GCYMBlocks.CASING_ATOMIC)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("aaaaaaaaaaaaa", "acccacccaccca", "acccacccaccca", "acccacccaccca", "aaaaaaaaaaaaa", "acccacccaccca", "acccacccaccca", "acccacccaccca", "aaaaaaaaaaaaa", "acccacccaccca", "acccacccaccca", "acccacccaccca", "aaaaaaaaaaaaa")
                        .aisle("acccacccaccca", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "acccacccaccca", "cbbbcdddcbbbc", "cbbbcdddcbbbc", "cbbbcdddcbbbc", "acccacccaccca", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "acccacccaccca")
                        .aisle("acccacccaccca", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "acccacccaccca", "cbbbcdddcbbbc", "cbbbcdedcbbbc", "cbbbcdddcbbbc", "acccacccaccca", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "acccacccaccca")
                        .aisle("acccacccaccca", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "acccacccaccca", "cbbbcdddcbbbc", "cbbbcdfdcbbbc", "cbbbcdddcbbbc", "acccacccaccca", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "cbbbcbbbcbbbc", "acccacccaccca")
                        .aisle("aaaaaaaaaaaaa", "abbbabbbabbba", "abbbabbbabbba", "abbbabbbabbba", "aaaaaaaaaaaaa", "abbbabbbabbba", "abbbabbbabbba", "abbbabbbabbba", "aaaaaaaaaaaaa", "abbbabbbabbba", "abbbabbbabbba", "abbbabbbabbba", "aaaaaaaaaaaaa")

                        .where("a", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:large_scale_assembler_casing")))
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(16).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(16).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(2))
                                .or(Predicates.abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(ThreadT3PartMachine.getPartAbility()).setMaxGlobalLimited(1).setPreviewCount(1)))
                        .where("b", Predicates.any())
                        .where("c", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:sturdy_machine_casing"))))
                        .where("d", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:atomic_casing"))))
                        .where("e", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("ae2:controller"))))
                        .where("f", Predicates.controller(blocks(definition.getBlock())))
                        .build();
            })
            .workableCasingModel(
                    GTCEu.id("block/casings/gcym/large_scale_assembling_casing"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Cartridges, Perfect Overclock, Parallel Hatch and Threading").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("Dynamic Recipes: Insert a Cartridge to unlock its specific recipes!").withStyle(style -> style.withColor(0x00FFFF)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Make the 3 following items:").withStyle(style -> style.withColor(0x90EE90)))

            .register();



    public static void init() {

    }

}