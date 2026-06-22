package net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.misc;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IRedstoneSignalMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.transfer.fluid.FluidHandlerList;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ibm.icu.text.MessagePattern;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.misc.PressurePartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.CheckForDim;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregEModifiers;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class AntiMassSpectrometer extends WorkableElectricMultiblockMachine {

    public AntiMassSpectrometer(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public static MachineDefinition ANTIMASSSPECTROMETER = REGISTRATE
            .multiblock("antimass", AntiMassSpectrometer::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.GAS_COLLECTOR_RECIPES)
            .recipeModifier(GTRecipeModifiers.PARALLEL_HATCH)
            .appearanceBlock(GCYMBlocks.CASING_ATOMIC)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("aaaabbbaaaa", "aaaaacaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaacaaaaa", "aaaabbbaaaa")
                        .aisle("aabbdddbbaa", "aaaaaeaaaaa", "aaaaacaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaacaaaaa", "aaaaaeaaaaa", "aabbdddbbaa")
                        .aisle("abdddddddba", "aafaaeaafaa", "aafaaeaafaa", "aaaaacaaaaa", "aaaaacaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaacaaaaa", "aaaaacaaaaa", "aaaaacaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaacaaaaa", "aaaaacaaaaa", "aafaaeaafaa", "aafaaeaafaa", "abdddddddba")
                        .aisle("abdddddddba", "aaaaaeaaaaa", "aaaaaeaaaaa", "aaafaeafaaa", "aaafaeafaaa", "aaaaacaaaaa", "aaaaacaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaacaaaaa", "aaaaacaaaaa", "aaafaeafaaa", "aaafaeafaaa", "aaaaaeaaaaa", "aaaaaeaaaaa", "abdddddddba")
                        .aisle("bdddddddddb", "aaaaaaaaaaa", "aaaaaeaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaafefaaaa", "aaaafefaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaafefaaaa", "aaaafefaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaeaaaaa", "aaaaaaaaaaa", "bdddddddddb")
                        .aisle("bdddddddddb", "ceeeaaaeeec", "aceeeeeeeca", "aaceaaaecaa", "aaceaaaecaa", "aaaceaecaaa", "aaaceeecaaa", "aacaagaacaa", "aacaaaaacaa", "aacaagaacaa", "aaaceeecaaa", "aaaceaecaaa", "aaceaaaecaa", "aaceaaaecaa", "aceeeeeeeca", "ceeeaaaeeec", "bdddddddddb")
                        .aisle("bdddddddddb", "aaaaaaaaaaa", "aaaaaeaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaafefaaaa", "aaaafefaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaafefaaaa", "aaaafefaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaeaaaaa", "aaaaaaaaaaa", "bdddddddddb")
                        .aisle("abdddddddba", "aaaaaeaaaaa", "aaaaaeaaaaa", "aaafaeafaaa", "aaafaeafaaa", "aaaaacaaaaa", "aaaaacaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaacaaaaa", "aaaaacaaaaa", "aaafaeafaaa", "aaafaeafaaa", "aaaaaeaaaaa", "aaaaaeaaaaa", "abdddddddba")
                        .aisle("abdddddddba", "aafaaeaafaa", "aafaaeaafaa", "aaaaacaaaaa", "aaaaacaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaacaaaaa", "aaaaacaaaaa", "aaaaacaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaacaaaaa", "aaaaacaaaaa", "aafaaeaafaa", "aafaaeaafaa", "abdddddddba")
                        .aisle("aabbdddbbaa", "aaaaaeaaaaa", "aaaaacaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaacaaaaa", "aaaaaeaaaaa", "aabbdddbbaa")
                        .aisle("aaaabhbaaaa", "aaaaacaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaa", "aaaaacaaaaa", "aaaabbbaaaa")

                        .where("a", Predicates.any())
                        .where("b", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:shock_proof_cutting_casing")))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.PASSTHROUGH_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                                .or(Predicates.abilities(PartAbility.PARALLEL_HATCH).setExactLimit(1))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(2)))
                        .where("c", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:large_scale_assembler_casing"))))
                        .where("d", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:laser_safe_engraving_casing"))))
                        .where("e", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:nonconducting_casing"))))
                        .where("f", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:hastelloy_c_276_frame"))))
                        .where("g", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("minecraft:iron_bars"))))
                        .where('h', Predicates.controller(blocks(definition.getBlock())))
                        .build();
            })
            .workableCasingModel(
                    GTCEu.id("block/casings/gcym/shock_proof_cutting_casing"),
                    GTCEu.id("block/multiblock/assembly_line"))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Assembly Line, Perfect Overclock and Threading").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .register();

    public static void init() {
    }
}