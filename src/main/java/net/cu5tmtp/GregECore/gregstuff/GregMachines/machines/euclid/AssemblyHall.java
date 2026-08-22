package net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.euclid;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;


public class AssemblyHall extends WorkableElectricMultiblockMachine {

    public AssemblyHall(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {

        assert recipe != null;
        String blockId = recipe.data.getString("cube_block");

        Block requiredBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId));

        if (requiredBlock != null && requiredBlock != Blocks.AIR) {
            Direction back = this.getFrontFacing().getOpposite();
            BlockPos centerPos = this.getPos()
                    .relative(back, 7)
                    .above(3);

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;

                        BlockPos checkPos = centerPos.offset(x, y, z);
                        BlockState state = this.getLevel().getBlockState(checkPos);

                        if (!state.is(requiredBlock)) {
                            return false;
                        }
                    }
                }
            }

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;

                        BlockPos breakPos = centerPos.offset(x, y, z);
                        this.getLevel().removeBlock(breakPos, false);
                    }
                }
            }
        }

        return super.beforeWorking(recipe);
    }

    public static MachineDefinition ASSEMBLYHALL = REGISTRATE
            .multiblock("assemblyhall", AssemblyHall::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(GCYMBlocks.CASING_STRESS_PROOF)
            .recipeTypes(GregERecipeTypes.ASSEMBLYHALL)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("bbbaacaaacaabbb", "bbbbbcbbbcbbbbb", "bbbbbcbbbcbbbbb", "bbbbbcbbbcbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb")
                        .aisle("bbaddceeecddabb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbcbbbcbbbbb", "bbbbbcbbbcbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb")
                        .aisle("badddceeecdddab", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbcccccbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb")
                        .aisle("addddceeecdddda", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbcbbbbbcbbbb", "bbbbbfffffbbbbb", "bbbbbbbbbbbbbbb")
                        .aisle("addddceeecdddda", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbcbbbbbbbcbbb", "bbbbfbbbbbfbbbb", "bbbbbfffffbbbbb")
                        .aisle("ccccccaaacccccc", "cbbbbbbbbbbbbbc", "cbbbbbbbbbbbbbc", "cbbbbbbbbbbbbbc", "bcbbbbbbbbbbbcb", "bcbbbbbbbbbbbcb", "bbcbbbbbbbbbcbb", "bbbfbbbbbbbfbbb", "bbbbfgggggfbbbb")
                        .aisle("aeeeeaeeeaeeeea", "bbbbbbbbbbbbbbb", "bbbbbbhhhbbbbbb", "bbbbbbhhhbbbbbb", "bbbbbbhhhbbbbbb", "bbbbbbbbbbbbbbb", "bbcbbbbbbbbbcbb", "bbbfbbbbbbbfbbb", "bbbbfgiiigfbbbb")
                        .aisle("aeeeeaeeeaeeeea", "bbbbbbbbbbbbbbb", "bbbbbbhhhbbbbbb", "bbbbbbhbhbbbbbb", "bbbbbbhhhbbbbbb", "bbbbbbbbbbbbbbb", "bbcbbbbbbbbbcbb", "bbbfbbbbbbbfbbb", "bbbbfgiiigfbbbb")
                        .aisle("aeeeeaeeeaeeeea", "bbbbbbbbbbbbbbb", "bbbbbbhhhbbbbbb", "bbbbbbhhhbbbbbb", "bbbbbbhhhbbbbbb", "bbbbbbbbbbbbbbb", "bbcbbbbbbbbbcbb", "bbbfbbbbbbbfbbb", "bbbbfgiiigfbbbb")
                        .aisle("ccccccaaacccccc", "cbbbbbbbbbbbbbc", "cbbbbbbbbbbbbbc", "cbbbbbbbbbbbbbc", "bcbbbbbbbbbbbcb", "bcbbbbbbbbbbbcb", "bbcbbbbbbbbbcbb", "bbbfbbbbbbbfbbb", "bbbbfgggggfbbbb")
                        .aisle("addddceeecdddda", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbcbbbbbbbcbbb", "bbbbfbbbbbfbbbb", "bbbbbfffffbbbbb")
                        .aisle("addddceeecdddda", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbcbbbbbcbbbb", "bbbbbfffffbbbbb", "bbbbbbbbbbbbbbb")
                        .aisle("badddceeecdddab", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbcccccbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb")
                        .aisle("bbaddceeecddabb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbcbbbcbbbbb", "bbbbbcbbbcbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb")
                        .aisle("bbbaacazacaabbb", "bbbbbcbbbcbbbbb", "bbbbbcbbbcbbbbb", "bbbbbcbbbcbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbb")

                        .where("a", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:stress_proof_casing")))
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(2)))
                        .where("b", Predicates.any())
                        .where("c", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("kubejs:machine_casing_tiled_very_dark_gray"))))
                        .where("d", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:sturdy_machine_casing"))))
                        .where("e", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:robust_machine_casing"))))
                        .where("f", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:large_scale_assembler_casing"))))
                        .where("g", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("kubejs:machine_casing_tiled_orange"))))
                        .where("h", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:shock_proof_cutting_casing")))
                                .or(Predicates.any()))
                        .where("i", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:white_lamp"))))
                        .where("z", Predicates.controller(Predicates.blocks(definition.get())))
                        .build();
            })
            .workableCasingModel(
                    GTCEu.id("block/casings/gcym/stress_proof_casing"),
                    GTCEu.id("block/multiblock/distillation_tower")
            )
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Pressure Assembly").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("An erratic piece of technology, seemingly incapable of compacting outer machine shells on its own. " +
                    "Providing a physical outline might assist the process.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Each recipe requires a specific 3x3x3 hollow cube to be built inside the structure (indicated by red blocks in the preview). " +
                    "Place the required blocks in this exact location; the machine will consume them upon activation. The recipe can't start without it.").withStyle(style -> style.withColor(0x90EE90)))
            .register();

    public static void init() {
    }
}