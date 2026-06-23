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
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries.GregERenederRegistries;
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
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class AntiMassSpectrometer extends WorkableElectricMultiblockMachine {

    public AntiMassSpectrometer(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public static MachineDefinition ANTIMASSSPECTROMETER = REGISTRATE
            .multiblock("antimass", AntiMassSpectrometer::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.GAS_COLLECTOR_RECIPES)
            .recipeModifiers(GTRecipeModifiers.OC_PERFECT, GTRecipeModifiers.PARALLEL_HATCH)
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
                                .or(Predicates.abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(2)))
                        .where("c", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:large_scale_assembler_casing"))))
                        .where("d", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:laser_safe_engraving_casing"))))
                        .where("e", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:nonconducting_casing"))))
                        .where("f", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:hastelloy_c_276_frame"))))
                        .where("g", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("minecraft:iron_bars"))))
                        .where('h', Predicates.controller(blocks(definition.getBlock())))
                        .build();
            })
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/gcym/shock_proof_cutting_casing"),
                    GTCEu.id("block/multiblock/assembly_line"))
                    .andThen(b -> b.addDynamicRenderer(GregERenederRegistries::createAMSRender))
            )
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Dimension Simulating, Perfect Overclock and Parallel Hatch").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("A multiblock capable of simulating a dimension's atmosphere and subsequently extracting gases from it. Just be careful not to cause a Resonance Cascade.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("If you provide it with Dimensional Simulator Maintenance Hatch, and input correct items into Dimensional Relics Input, the machine will ignore the dimension requirement.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Items and Recipe Unlocks:").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("Eye of Abyss -> Abyssal Air").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Eye of Void -> Enderium Air").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Eye of Mech -> Forge Smoke").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Eye of Storm -> Captured Lightning").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Eye of Curse -> Cursed Air").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Eye of Flame -> Ignitium Infused Lava").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Miniature Twilight Forest Portal -> Twilight Forest Air:").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Raw Demonite -> Demonic Air").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Delerian Burial Mask -> Mars Air").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Nether Star -> Nether Air").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Dragon Egg -> End Air").withStyle(ChatFormatting.LIGHT_PURPLE))
            .register();

    public static void init() {
    }
}