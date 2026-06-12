package net.cu5tmtp.GregECore.gregstuff.GregMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.transfer.fluid.FluidHandlerList;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.cu5tmtp.GregECore.block.ModBlocks;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.AdvancedCoolantInputPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.AdvancedHeaterInputPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.CoolantInputPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.CoolantOutputPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries.GregERenederRegistries;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregEModifiers;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;
import static net.minecraft.client.gui.screens.Screen.hasShiftDown;

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

    private int recipeTemp;

    private int hullDmg = 100;

    private IFluidHandler coolantHandlerInput;
    private IFluidHandler coolantHandlerOutput;

    private static final float MAX_HEAT = 50000.0F;

    public FissionReactor(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public void onStructureFormed() {
        this.controlRodInsertion = this.controlRodInsertionInternal;
        this.heatLevel = this.heatLevelInternal;

        super.onStructureFormed();

        List<IFluidHandler> coolantContainers = new ArrayList<>();

        for (IMultiPart part : getParts()) {

            if(!(part instanceof CoolantInputPartMachine)){
                continue;
            }

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                handlerList.getCapability(FluidRecipeCapability.CAP).stream()
                        .filter(IFluidHandler.class::isInstance)
                        .map(IFluidHandler.class::cast)
                        .forEach(coolantContainers::add);
            }
        }
        this.coolantHandlerInput = new FluidHandlerList(coolantContainers);

        coolantContainers.clear();

        for (IMultiPart part : getParts()) {

            if(!(part instanceof CoolantOutputPartMachine)){
                continue;
            }

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                handlerList.getCapability(FluidRecipeCapability.CAP).stream()
                        .filter(IFluidHandler.class::isInstance)
                        .map(IFluidHandler.class::cast)
                        .forEach(coolantContainers::add);
            }
        }
        this.coolantHandlerOutput = new FluidHandlerList(coolantContainers);

        if (heatSubscription != null) {
            heatSubscription.unsubscribe();
            heatSubscription = null;
        }

        heatSubscription = this.subscribeServerTick(this::manageHeat);

        super.onStructureFormed();
    }

    private void manageHeat() {
        if (this.getLevel() != null && this.getLevel().isClientSide) return;

        boolean stateChanged = false;

        if (this.getRecipeLogic().isWorking()) {
            float generatedHeat = this.recipeTemp * this.controlRodInsertionInternal;
            this.heatLevelInternal += generatedHeat;
            stateChanged = true;
        }

        if (this.heatLevelInternal > 0 && this.coolantHandlerInput != null && this.coolantHandlerOutput != null) {

            int maxDrainAmount = 1000;
            var simulatedDrain = this.coolantHandlerInput.drain(maxDrainAmount, IFluidHandler.FluidAction.SIMULATE);

            if (!simulatedDrain.isEmpty()) {
                float coolingPower = getFluidCoolingPower(simulatedDrain);
                FluidStack hotFluidOut = getHeatedCoolant(simulatedDrain);

                if (coolingPower > 0 && hotFluidOut != null) {
                    int heatToDissipate = (int) Math.min(this.heatLevelInternal, simulatedDrain.getAmount() * coolingPower);
                    int fluidToConsume = (int) Math.ceil(heatToDissipate / coolingPower);

                    hotFluidOut.setAmount(fluidToConsume);

                    int acceptedFill = this.coolantHandlerOutput.fill(hotFluidOut, IFluidHandler.FluidAction.SIMULATE);

                    if (acceptedFill > 0) {
                        var actualDrained = this.coolantHandlerInput.drain(acceptedFill, IFluidHandler.FluidAction.EXECUTE);
                        hotFluidOut.setAmount(actualDrained.getAmount());
                        this.coolantHandlerOutput.fill(hotFluidOut, IFluidHandler.FluidAction.EXECUTE);

                        this.heatLevelInternal -= (actualDrained.getAmount() * coolingPower);
                        if (this.heatLevelInternal < 0) this.heatLevelInternal = 0;
                        stateChanged = true;
                    }
                }
            }
        }

        if (this.heatLevelInternal > MAX_HEAT) {
            this.hullDmg--;
            stateChanged = true;

            if (this.hullDmg <= 0) {
                triggerExplosion();
                return;
            }
        }

        if (getOffsetTimer() % 100 != 0) {
            if(this.hullDmg < 100) {
                hullDmg++;
            }
        }

        if (stateChanged) {
            copyInternal();
        }
    }

    private void triggerExplosion() {
        if (this.getLevel() != null && this.getPos() != null) {
            this.getLevel().explode(
                    null,
                    this.getPos().getX(),
                    this.getPos().getY(),
                    this.getPos().getZ(),
                    30.0f,
                    net.minecraft.world.level.Level.ExplosionInteraction.BLOCK
            );
        }
    }

    private float getFluidCoolingPower(FluidStack fluidStack) {
        String fluidKey = ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).toString();

        switch (fluidKey) {
            case "minecraft:water": return 1.5f;          // Normální chlazení
            case "gtceu:distilled_water": return 3.0f;    // Lepší chlazení
            case "gtceu:liquid_helium": return 15.0f;     // Extrémní chlazení
            default: return 0.0f;
        }
    }

    private FluidStack getHeatedCoolant(FluidStack inputCoolant) {
        String fluidKey = net.minecraftforge.registries.ForgeRegistries.FLUIDS.getKey(inputCoolant.getFluid()).toString();
        net.minecraft.world.level.material.Fluid outputFluid = null;

        if (fluidKey.equals("minecraft:water") || fluidKey.equals("gtceu:distilled_water")) {
            outputFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("gtceu:steam"));
        }

        if (fluidKey.equals("gtceu:liquid_helium")) {
        }

        if (outputFluid != null && outputFluid != net.minecraft.world.level.material.Fluids.EMPTY) {
            return new net.minecraftforge.fluids.FluidStack(outputFluid, 1);
        }
        return null;
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        assert recipe != null;
        recipeTemp = recipe.data.getInt("heatgen");
        return super.beforeWorking(recipe);
    }

    @Override
    public Widget createUIWidget() {
        Widget widget = super.createUIWidget();

        if (widget instanceof WidgetGroup group) {

            WidgetGroup buttonGroup = new WidgetGroup(-70, 140, 60, 14);

            LabelWidget percentLabel = new LabelWidget(36, 3, () -> String.valueOf(Component.literal(Math.round(getInsertionPercent()) + "%").withStyle(ChatFormatting.BLACK))) {
                @Override
                public void updateScreen() {
                    super.updateScreen();
                    this.setComponent(Component.literal(Math.round(getInsertionPercent()) + "%").withStyle(ChatFormatting.BLACK));
                    if(Math.round(getInsertionPercent()) == 100){
                        setSelfPosition(36, 3);
                    } else if(Math.round(getInsertionPercent()) == 0) {
                        setSelfPosition(42, 3);
                    } else{
                        setSelfPosition(40, 3);
                    }
                }
            };


            percentLabel.setDropShadow(false);

            ButtonWidget interactButton = new ButtonWidget(32, -8, 31, 28, ResourceBorderTexture.BORDERED_BACKGROUND, click -> {}) {
                @Override
                public boolean mouseClicked(double mouseX, double mouseY, int button) {
                    if (this.isMouseOverElement(mouseX, mouseY)) {
                        float current = getInsertionPercent();

                        boolean isShift = false;
                        try {
                            isShift = hasShiftDown();
                        } catch (Throwable ignored) {}

                        float delta = isShift ? 10f : 1f;

                        if (button == 0) {
                            setInsertionPercent(current + delta);
                            playButtonClickSound();
                            return true;
                        } else if (button == 1) {
                            setInsertionPercent(current - delta);
                            playButtonClickSound();
                            return true;
                        }
                    }
                    return super.mouseClicked(mouseX, mouseY, button);
                }
            };

            interactButton.appendHoverTooltips(Component.literal("Control Rod Insertion Percentage Value").withStyle(ChatFormatting.GOLD));
            interactButton.appendHoverTooltips(Component.literal("Left Click: +1% | Shift + Left Click: +10%"));
            interactButton.appendHoverTooltips(Component.literal("Right Click: -1% | Shift + Right Click: -10%"));

            buttonGroup.addWidget(interactButton);
            buttonGroup.addWidget(percentLabel);

            group.addWidget(buttonGroup);
        }
        return widget;
    }

    private void copyInternal(){
        this.controlRodInsertion = this.controlRodInsertionInternal;
        this.heatLevel = this.heatLevelInternal;
    }

    private float getInsertionPercent() {
        float percent = 100f - ((this.controlRodInsertionInternal - 0.1f) / 5.2f) * 100f;
        return Math.max(0f, Math.min(100f, percent));
    }

    private void setInsertionPercent(float percent) {
        float clampedPercent = Math.max(0f, Math.min(100f, percent));
        this.controlRodInsertionInternal = 0.1f + ((100f - clampedPercent) / 100f) * 5.2f;
        copyInternal();
    }

    public static MachineDefinition FISSIONREACTOR = REGISTRATE
            .multiblock("fissionreactor", FissionReactor::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GregERecipeTypes.FISSION_REACTION)
            .recipeModifiers(GregEModifiers::fissionBoost)
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
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(2))
                                .or(Predicates.abilities(CoolantInputPartMachine.COOLANT_INPUT).setMaxGlobalLimited(1).setPreviewCount(1)))
                        .where('h', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:tungstensteel_turbine_casing")))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(2))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(2))
                                .or(Predicates.abilities(CoolantOutputPartMachine.COOLANT_OUTPUT).setMaxGlobalLimited(1).setPreviewCount(1)))
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
        if (isFormed()) {
            textList.add(Component.literal("Hull Health: " + hullDmg + "%").withStyle(ChatFormatting.AQUA));
        }
        super.addDisplayText(textList);
    }

    public static void init() {}
}
