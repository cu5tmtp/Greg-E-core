/*
package net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.endgame;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.registrate.MultiblockMachineBuilder;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class BoxMachines extends MultiblockControllerMachine {

    public BoxMachines(IMachineBlockEntity holder) {
        super(holder);
    }

    public static final MachineDefinition MACHINE_1 = create3x3Multiblock(
            "machine_one",
            "gtceu:steel_gearbox",
            "gtceu:stress_proof_casing",
            GTCEu.id("block/casings/gcym/stress_proof_casing"),
            GTCEu.id("block/multiblock/distillation_tower"),
            "Adds Recipe Types: Extruder, Bender, Compressor, Forge Hammer, Forming Press and Wiremill."
    );

    public static final MachineDefinition MACHINE_2 = create3x3Multiblock(
            "machine_two",
            "gtceu:assembly_line_unit",
            "gtceu:large_scale_assembler_casing",
            GTCEu.id("block/casings/gcym/large_scale_assembling_casing"),
            GTCEu.id("block/multiblock/distillation_tower"),
            "Adds Recipe Types: Assembler and Circuit Assembler."
    );

    public static final MachineDefinition MACHINE_3 = create3x3Multiblock(
            "machine_three",
            "gtceu:steel_pipe_casing",
            "gtceu:watertight_casing",
            GTCEu.id("block/casings/gcym/watertight_casing"),
            GTCEu.id("block/multiblock/distillation_tower"),
            "Adds Recipe Types: Fluid Solidifier, Ore Washer, Chemical Bath, Extractor, Canner and Autoclave."
    );

    public static final MachineDefinition MACHINE_4 = create3x3Multiblock(
            "machine_four",
            "gtceu:stainless_steel_gearbox",
            "gtceu:vibration_safe_casing",
            GTCEu.id("block/casings/gcym/vibration_safe_casing"),
            GTCEu.id("block/multiblock/distillation_tower"),
            "Adds Recipe Types: Sifter, Centrifuge and Thermal Centrifuge."
    );

    public static final MachineDefinition MACHINE_5 = create3x3Multiblock(
            "machine_five",
            "gtceu:filter_casing",
            "gtceu:nonconducting_casing",
            GTCEu.id("block/casings/gcym/nonconducting_casing"),
            GTCEu.id("block/multiblock/distillation_tower"),
            "Adds Recipe Types: Electromagnetic Separator, Polarizer and Electrolyzer."
    );

    public static final MachineDefinition MACHINE_6 = create3x3Multiblock(
            "machine_six",
            "gtceu:stainless_steel_gearbox",
            "gtceu:secure_maceration_casing",
            GTCEu.id("block/casings/gcym/secure_maceration_casing"),
            GTCEu.id("block/multiblock/distillation_tower"),
            "Adds Recipe Type: Macerator."
    );

    public static final MachineDefinition MACHINE_7 = create3x3Multiblock(
            "machine_seven",
            "gtceu:ptfe_pipe_casing",
            "gtceu:reaction_safe_mixing_casing",
            GTCEu.id("block/casings/gcym/reaction_safe_mixing_casing"),
            GTCEu.id("block/multiblock/distillation_tower"),
            "Adds Recipe Type: Mixer."
    );

    public static final MachineDefinition MACHINE_8 = create3x3Multiblock(
            "machine_eight",
            "gtceu:laser_hazard_sign_block",
            "gtceu:laser_safe_engraving_casing",
            GTCEu.id("block/casings/gcym/laser_safe_engraving_casing"),
            GTCEu.id("block/multiblock/distillation_tower"),
            "Adds Recipe Type: Laser Engraver."
    );

    public static final MachineDefinition MACHINE_9 = create3x3Multiblock(
            "machine_nine",
            "gtceu:slicing_blades",
            "gtceu:shock_proof_cutting_casing",
            GTCEu.id("block/casings/gcym/shock_proof_cutting_casing"),
            GTCEu.id("block/multiblock/distillation_tower"),
            "Adds Recipe Types: Cutter and Lathe."
    );

    public static final MachineDefinition MACHINE_10 = create3x3Multiblock(
            "machine_ten",
            "minecraft:magma_block",
            "gtceu:high_temperature_smelting_casing",
            GTCEu.id("block/casings/gcym/high_temperature_smelting_casing"),
            GTCEu.id("block/multiblock/distillation_tower"),
            "Adds Recipe Types: Arc Furnace."
    );

    public static final MachineDefinition MACHINE_11 = create3x3Multiblock(
            "machine_eleven",
            "gtceu:steel_pipe_casing",
            "gtceu:corrosion_proof_casing",
            GTCEu.id("block/casings/gcym/corrosion_proof_casing"),
            GTCEu.id("block/multiblock/distillation_tower"),
            "Adds Recipe Types: Brewery, Fermenter, Fluid Heater."
    );

    private static MachineDefinition create3x3Multiblock(
            String name,
            String centerBlock,
            String casingBlock,
            ResourceLocation baseTexture,
            ResourceLocation overlayTexture,
            String tooltip) {

        return REGISTRATE.multiblock(name, BoxMachines::new)
                .rotationState(RotationState.NON_Y_AXIS)
                .appearanceBlock(GCYMBlocks.CASING_ATOMIC)
                .pattern(definition -> FactoryBlockPattern.start()
                        .aisle("XXX", "XXX", "XXX")
                        .aisle("XXX", "XCX", "XXX")
                        .aisle("XXX", "XAX", "XXX")

                        .where('A', Predicates.controller(blocks(definition.getBlock())))
                        .where('C', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(centerBlock))))
                        .where('X', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(casingBlock))))
                        .build())
                .tooltips(Component.literal(tooltip).withStyle(ChatFormatting.LIGHT_PURPLE))
                .workableCasingModel(baseTexture, overlayTexture)
                .register();
    }

    public static final MachineDefinition[] ALL_BOXES = {
            MACHINE_1, MACHINE_2, MACHINE_3, MACHINE_4, MACHINE_5,
            MACHINE_6, MACHINE_7, MACHINE_8, MACHINE_9, MACHINE_10, MACHINE_11
    };

    public static void init() {
    }
}
*/