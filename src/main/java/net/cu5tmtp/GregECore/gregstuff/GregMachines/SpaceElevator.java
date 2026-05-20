package net.cu5tmtp.GregECore.gregstuff.GregMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.IRedstoneSignalMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries.GregERenederRegistries;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class SpaceElevator extends WorkableElectricMultiblockMachine implements IRedstoneSignalMachine {

    public SpaceElevator(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            SpaceElevator.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @DescSynced
    @Persisted
    protected int targetHeight = 0;

    private int recipeHeight = 0;

    @Override
    public Widget createUIWidget() {
        Widget widget = super.createUIWidget();

        if (widget instanceof WidgetGroup group) {

            group.addWidget(new LabelWidget(-80, 125, Component.literal("Target Height:").withStyle(ChatFormatting.GOLD)));

            var frequencyInput = new TextFieldWidget(
                    -70, 140, 60, 12,
                    () -> this.targetHeight == 0 ? "" : String.valueOf(this.targetHeight),
                    val -> {
                        if (val.isEmpty()) {
                            this.targetHeight = 0;
                        } else {
                            try {
                                this.targetHeight = Integer.parseInt(val);
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
            );
            group.addWidget(frequencyInput);
        }
        return widget;
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {

        assert recipe != null;
        recipeHeight = recipe.data.getInt("height_level");

        if(targetHeight > recipeHeight || targetHeight < (recipeHeight - 10)) {
            return false;
        }

        return super.beforeWorking(recipe);
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
                        .where("c", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:stress_proof_casing")))
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(2)))
                        .where('d', Predicates.controller(blocks(definition.getBlock())))
                        .where("g", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:solid_machine_casing"))))
                        .where("h", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:high_temperature_smelting_casing"))))
                        .where("i", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:tungsten_steel_frame"))))
                        .where("j", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:secure_maceration_casing"))))
                        .where("k", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:hastelloy_c_276_frame"))))
                        .build();
            })
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/gcym/stress_proof_casing"),
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
