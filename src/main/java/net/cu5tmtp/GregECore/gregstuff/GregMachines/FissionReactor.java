package net.cu5tmtp.GregECore.gregstuff.GregMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.cu5tmtp.GregECore.block.ModBlocks;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries.GregERenederRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

@SuppressWarnings("removal")
public class FissionReactor extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            FissionReactor.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    private TickableSubscription heatSubscription;

    @DescSynced
    public float controlRodInsertion = 0.0F;

    @Persisted
    private float controlRodInsertionInternal = 0.0F;

    @DescSynced
    public float heatLevel = 0.0F;

    @Persisted
    private float heatLevelInternal = 0.0F;

    public FissionReactor(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public void onStructureFormed() {
        this.controlRodInsertion = this.controlRodInsertionInternal;
        this.heatLevel = this.heatLevelInternal;

        if (heatSubscription != null) {
            heatSubscription.unsubscribe();
            heatSubscription = null;
        }

        heatSubscription = this.subscribeServerTick(this::manageHeat);

        super.onStructureFormed();
    }

    private void manageHeat() {
        
    }

    @Override
    public Widget createUIWidget() {
        Widget widget = super.createUIWidget();

        if (widget instanceof WidgetGroup group) {

            group.addWidget(new LabelWidget(-120, 125, Component.literal("Control Rod Insertion:").withStyle(ChatFormatting.GOLD)));

            var frequencyInput = new TextFieldWidget(
                    -70, 140, 60, 12,
                    () -> {
                        float current = this.controlRodInsertionInternal;
                        float percent = 100f - ((current - 0.1f) / (5.2f - 0.1f)) * 100f;
                        return String.format("%.0f%%", Math.max(0, Math.min(100, percent)));
                    },
                    val -> {
                        String cleanVal = val.replace("%", "").trim();
                        if (!cleanVal.isEmpty()) {
                            try {
                                float percent = Float.parseFloat(cleanVal);
                                float clampedPercent = Math.max(0, Math.min(100, percent));

                                this.controlRodInsertionInternal = 0.1f + ((100f - clampedPercent) / 100f) * (5.3f - 0.1f);

                                copyInternal();
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
            );
            group.addWidget(frequencyInput);
        }
        return widget;
    }

    private void copyInternal(){
        this.controlRodInsertion = this.controlRodInsertionInternal;
        this.heatLevel = this.heatLevelInternal;
    }

    public static MachineDefinition FISSIONREACTOR = REGISTRATE
            .multiblock("fissionreactor", FissionReactor::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.VACUUM_RECIPES)
            .appearanceBlock(CASING_INVAR_HEATPROOF)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("ggggg", "accca", "accca", "accca", "accca", "accca", "accca", "accca", "accca", "hhhhh")
                        .aisle("gaaag", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "heeeh")
                        .aisle("gaaag", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "heeeh")
                        .aisle("gaaag", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "heeeh")
                        .aisle("ggfgg", "accca", "accca", "accca", "accca", "accca", "accca", "accca", "accca", "hhhhh")

                        .where("a", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:tungstensteel_turbine_casing"))))
                        .where("b", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:ptfe_pipe_casing"))))
                        .where("c", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:laminated_glass"))))
                        .where("d", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("minecraft:air"))))
                        .where("e", Predicates.blocks(ModBlocks.NOZZLE.get()))
                        .where('f', Predicates.controller(blocks(definition.getBlock())))
                        .where('g', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:tungstensteel_turbine_casing")))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(2))
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(2))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(2)))
                        .where('h', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:tungstensteel_turbine_casing")))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(2))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(2)))
                        .build();
            })
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/mechanic/machine_casing_turbine_tungstensteel"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
                    .andThen(b -> b.addDynamicRenderer(GregERenederRegistries::createFissionRodRender)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Improved Cooling and Perfect Overclock").withStyle(style -> style.withColor(0xFFD700)))
            .register();

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
    }

    public static void init() {}
}
