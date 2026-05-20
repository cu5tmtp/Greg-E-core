package net.cu5tmtp.GregECore.gregstuff.GregMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
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
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.StarFeederPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries.GregERenederRegistries;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.cu5tmtp.GregECore.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class SpaceElevator extends WorkableElectricMultiblockMachine implements IRedstoneSignalMachine {

    public SpaceElevator(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public static MachineDefinition SPACE_ELEVATOR = REGISTRATE
            .multiblock("spaceelevator", SpaceElevator::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GregERecipeTypes.SEND_UP_THE_MATS)
            .recipeModifiers(GTRecipeModifiers.OC_PERFECT)
            .appearanceBlock(GCYMBlocks.CASING_ATOMIC)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("bbbbbcccccccbbbbb", "bbbbbcbbbbbcbbbbb", "bbbbbcbbbbbcbbbbb", "bbbbbcbbbbbcbbbbb", "bbbbbcbbbbbcbbbbb", "bbbbbcbbbbbcbbbbb", "bbbbbbcbbbcbbbbbb", "bbbbbbbcccbbbbbbb", "bbbbbbbbcbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb")
                        .aisle("bbbccggghgggccbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbcbbbbbbbb", "bbbbbbbbcbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb")
                        .aisle("bbcggggghgggggcbb", "bbibbbbbbbbbbbibb", "bbibbbbbbbbbbbibb", "bbibbbbbbbbbbbibb", "bbibbbbbbbbbbbibb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbiiiiibbbbbb", "bbbbbbbbcbbbbbbbb", "bbbbbbbbcbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb")
                        .aisle("bcghgggghgggghgcb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbibbbbbbbbbibbb", "bbbibbbbbbbbbibbb", "bbbibbbbbbbbbibbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbibbbbbibbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbcbbbbbbbb", "bbbbbbbbcbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb")
                        .aisle("bcgghghhjhhghggcb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbibbbbbbbibbbb", "bbbbibbbbbbbibbbb", "bbbbibbbbbbbibbbb", "bbbbibbbbbbbibbbb", "bbbbibbbbbbbibbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbcccbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb")
                        .aisle("cgggghjjjjjhggggc", "cbbbbbbbkbbbbbbbc", "cbbbbbbbkbbbbbbbc", "cbbbbbbbkbbbbbbbc", "cbbbbbbbkbbbbbbbc", "cbbbbbbbbbbbbbbbc", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbibbbbbbbbbibbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbibbbbbibbbbb", "bbbbbibbbbbibbbbb", "bbbbbicbbbcibbbbb", "bbbbbibbbbbibbbbb", "bbbbbibbbbbibbbbb", "bbbbbibbbbbibbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb")
                        .aisle("cggghjjjjjjjhgggc", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbkbbbbbbbb", "bbbbbbbbbbbbbbbbb", "cbbbbbbbbbbbbbbbc", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbibbbbbbbbbbbibb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbcbbbbbcbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbibbbibbbbbb", "bbbbbbibbbibbbbbb", "bbbbbbibbbibbbbbb", "bbbbbbbiiibbbbbbb")
                        .aisle("cggghjjjjjjjhgggc", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "cbbbbbbbbbbbbbbbc", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbibbbbbbbbbbbibb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbcbbbbbbbcbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbibbbibbbbbb")
                        .aisle("chhhjjjjjjjjjhhhc", "bbbbbkbbjbbkbbbbb", "bbbbbkbbjbbkbbbbb", "bbbbbkbbbbbkbbbbb", "bbbbbkkbbbkkbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "cbbbbbbbbbbbbbbbc", "cbbbbbbbbbbbbbbbc", "bcbbbbbbbbbbbbbcb", "bcibbbbbbbbbbbicb", "bbcbbbbbbbbbbbcbb", "bbcbbbbbbbbbbbcbb", "bbbcbbbbbbbbbcbbb", "bbbcbbbbbbbbbcbbb", "bbbbcbbbbbbbcbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbibbbibbbbbb")
                        .aisle("cggghjjjjjjjhgggc", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "cbbbbbbbbbbbbbbbc", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbibbbbbbbbbbbibb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbcbbbbbbbcbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbibbbibbbbbb")
                        .aisle("cggghjjjjjjjhgggc", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbkbbbbbbbb", "bbbbbbbbbbbbbbbbb", "cbbbbbbbbbbbbbbbc", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbibbbbbbbbbbbibb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbcbbbbbcbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbibbbibbbbbb", "bbbbbbibbbibbbbbb", "bbbbbbibbbibbbbbb", "bbbbbbbiiibbbbbbb")
                        .aisle("cgggghjjjjjhggggc", "cbbbbbbbkbbbbbbbc", "cbbbbbbbkbbbbbbbc", "cbbbbbbbkbbbbbbbc", "cbbbbbbbkbbbbbbbc", "cbbbbbbbbbbbbbbbc", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbibbbbbbbbbibbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbibbbbbibbbbb", "bbbbbibbbbbibbbbb", "bbbbbicbbbcibbbbb", "bbbbbibbbbbibbbbb", "bbbbbibbbbbibbbbb", "bbbbbibbbbbibbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb")
                        .aisle("bcgghghhjhhghggcb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbibbbbbbbibbbb", "bbbbibbbbbbbibbbb", "bbbbibbbbbbbibbbb", "bbbbibbbbbbbibbbb", "bbbbibbbbbbbibbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbcccbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb")
                        .aisle("bcghgggghgggghgcb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbibbbbbbbbbibbb", "bbbibbbbbbbbbibbb", "bbbibbbbbbbbbibbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbibbbbbibbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbcbbbbbbbb", "bbbbbbbbcbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb")
                        .aisle("bbcggggghgggggcbb", "bbibbbbbbbbbbbibb", "bbibbbbbbbbbbbibb", "bbibbbbbbbbbbbibb", "bbibbbbbbbbbbbibb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbiiiiibbbbbb", "bbbbbbbbcbbbbbbbb", "bbbbbbbbcbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb")
                        .aisle("bbbccggghgggccbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbcbbbbbbbb", "bbbbbbbbcbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb")
                        .aisle("bbbbbcccdcccbbbbb", "bbbbbcbbbbbcbbbbb", "bbbbbcbbbbbcbbbbb", "bbbbbcbbbbbcbbbbb", "bbbbbcbbbbbcbbbbb", "bbbbbcbbbbbcbbbbb", "bbbbbbcbbbcbbbbbb", "bbbbbbbcccbbbbbbb", "bbbbbbbbcbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbb")

                        .where("b", Predicates.any())
                        .where("c", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:vibration_safe_casing")))
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(2)))
                        .where('d', Predicates.controller(blocks(definition.getBlock())))
                        .where("e", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:solid_machine_casing"))))
                        .where("f", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:stress_proof_casing"))))
                        .where("g", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:europium_frame"))))
                        .where("h", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:secure_maceration_casing"))))
                        .where("i", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:hastelloy_c_276_frame"))))
                        .build();
            })
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/gcym/vibration_safe_casing"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
                    .andThen(b -> b.addDynamicRenderer(GregERenederRegistries::createSpaceElevatorRender)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .register();

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
    }

    public static void init() {
    }
}
