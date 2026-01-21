package net.cu5tmtp.GregECore.gregstuff.GregMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
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
import com.gregtechceu.gtceu.api.transfer.fluid.FluidHandlerList;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.utils.GTTransferUtils;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries.GregERenederRegistries;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.cu5tmtp.GregECore.item.GreggyItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class EnhancedFusionReactor extends WorkableElectricMultiblockMachine implements IRedstoneSignalMachine {

    public EnhancedFusionReactor(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Persisted
    private double heat = 0;
    private TickableSubscription heatSubscription;
    private IFluidHandler coolantHandler;
    private int recipeTemp = 0;

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            EnhancedFusionReactor.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        List<IFluidHandler> coolantContainers = new ArrayList<>();
        Long2ObjectMap<IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap", Long2ObjectMaps::emptyMap);

        for (IMultiPart part : getParts()) {
            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.BOTH);
            if (io == IO.NONE || io == IO.OUT) continue;
            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                if (!handlerList.isValid(io)) continue;
                handlerList.getCapability(FluidRecipeCapability.CAP).stream()
                        .filter(IFluidHandler.class::isInstance)
                        .map(IFluidHandler.class::cast)
                        .forEach(coolantContainers::add);
            }
        }
        this.coolantHandler = new FluidHandlerList(coolantContainers);

        if (heatSubscription != null) {
            heatSubscription.unsubscribe();
            heatSubscription = null;
        }

        heatSubscription = this.subscribeServerTick(this::manageHeat);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (heatSubscription != null) {
            heatSubscription.unsubscribe();
            heatSubscription = null;
        }
    }


    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        assert recipe != null;
        recipeTemp = recipe.data.getInt("heat_level");

        if(heat > recipeTemp || heat < (recipeTemp - 500)) {
            return false;
        }

        return super.beforeWorking(recipe);
    }

    //add redstone output from controller depending on heat
    @Override
    public int getOutputSignal(@Nullable Direction side) {
        if (heat <= 0) return 0;
        int signal = (int) (heat / 500);
        return Math.min(15, signal);
    }

    @Override
    public int getAnalogOutputSignal() {
        return getOutputSignal(null);
    }

    @Override
    public boolean canConnectRedstone(Direction side) {
        return true;
    }

    //heat logic
    private void manageHeat() {
        if (getOffsetTimer() % 20 != 0) return;

        if (getRecipeLogic().isActive()) {
            applyWorkingHeatLogic();
        } else {
            applyIdleHeatLogic();
        }

        if (heat < 0) heat = 0;
        if (heat > 7500) heat = 7500;

        if (!isRemote()) {
            this.self().notifyBlockUpdate();
            this.updateSignal();
            this.self().markDirty();
        }
    }

    private void applyIdleHeatLogic() {
        int amountToDrain = 1000;
        Fluid coolant = GreggyItems.SUPERHEATED_SOLAR.getFluid();
        FluidStack resource = new FluidStack(coolant, amountToDrain);

        FluidStack simulation = GTTransferUtils.drainFluidAccountNotifiableList(
                coolantHandler, resource, IFluidHandler.FluidAction.SIMULATE);

        if (simulation.getAmount() >= amountToDrain) {
            GTTransferUtils.drainFluidAccountNotifiableList(
                    coolantHandler, resource, IFluidHandler.FluidAction.EXECUTE);
            heat += 50;
        } else {
            heat -= 100;
        }
    }

    private void applyWorkingHeatLogic() {
        heat += 60;

        int amountToDrain = 100;
        Fluid coolant = GreggyItems.DEIONIZED_WATER.getFluid();
        FluidStack resource = new FluidStack(coolant, amountToDrain);

        FluidStack simulation = GTTransferUtils.drainFluidAccountNotifiableList(
                coolantHandler,
                resource,
                IFluidHandler.FluidAction.SIMULATE
        );

        if (!simulation.isEmpty() && simulation.getAmount() >= amountToDrain) {
            GTTransferUtils.drainFluidAccountNotifiableList(
                    coolantHandler,
                    resource,
                    IFluidHandler.FluidAction.EXECUTE
            );
            heat -= 60;
        }

        if (heat > recipeTemp || heat < (recipeTemp - 500)) {
            getRecipeLogic().interruptRecipe();
        }

    }

    public static MachineDefinition ENHANCED_FUSION_REACTOR = REGISTRATE
            .multiblock("enhancedfr", EnhancedFusionReactor::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GregERecipeTypes.ADVANCED_FUSION)
            .recipeModifiers(GTRecipeModifiers.OC_PERFECT)
            .appearanceBlock(GCYMBlocks.CASING_ATOMIC)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("             ", "    GGEGG    ", "    HHEHH    ", "    GGEGG    ", "             ")
                        .aisle("    GGEGG    ", "   G     G   ", "   H     H   ", "   G     G   ", "    GGEGG    ")
                        .aisle("   GHHEHHG   ", "  D       D  ", "  H   F   H  ", "  D       D  ", "   GHHEHHG   ")
                        .aisle("  GHGGEGGHG  ", " G         G ", " H         H ", " G         G ", "  GHGGEGGHG  ")
                        .aisle(" GHGG   GGHG ", "G    GEG    G", "H    GEG    H", "G    GEG    G", " GHGG   GGHG ")
                        .aisle(" GHG     GHG ", "G   G   G   G", "H   G   G   H", "G   G   G   G", " GHG     GHG ")
                        .aisle(" EEE     EEE ", "E   E   E   E", "E F E   E F E", "E   E   E   E", " EEE     EEE ")
                        .aisle(" GHG     GHG ", "G   G   G   G", "H   G   G   H", "G   G   G   G", " GHG     GHG ")
                        .aisle(" GHGG   GGHG ", "G    GBG    G", "H    GBG    H", "G    GBG    G", " GHGG   GGHG ")
                        .aisle("  GHGGBGGHG  ", " G         G ", " H         H ", " G         G ", "  GHGGBGGHG  ")
                        .aisle("   GHHBHHG   ", "  D       D  ", "  H   F   H  ", "  D       D  ", "   GHHBHHG   ")
                        .aisle("    GGBGG    ", "   G     G   ", "   H     H   ", "   G     G   ", "    GGBGG    ")
                        .aisle("             ", "    GGAGG    ", "    HHBHH    ", "    GGBGG    ", "             ")
                        .where('A', Predicates.controller(blocks(definition.getBlock())))
                        .where('B', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gregecore:draconiumfusion")))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(4).setPreviewCount(2))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setExactLimit(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(4).setPreviewCount(2)))
                        .where('D', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gregecore:atomic_engine_intake"))))
                        .where('E', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gregecore:draconiumfusion"))))
                        .where('F', Predicates.blocks(GTBlocks.FUSION_COIL.get()))
                        .where('G', Predicates.blocks(GCYMBlocks.CASING_ATOMIC.get()))
                        .where('H', Predicates.blocks(GTBlocks.FUSION_GLASS.get()))
                        .where(' ', Predicates.any())
                        .build();
            })
            .model(createWorkableCasingMachineModel(
                    GregECore.id("block/draconium_fusion"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
                    .andThen(b -> b.addDynamicRenderer(GregERenederRegistries::createEnhancedFusionRingRender)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Heat Management and Perfect Overclock").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Due to an engineering mistake, this reactor needs a ").withStyle(style -> style.withColor(0x90EE90))
                    .append(Component.literal("working recipe").withStyle(ChatFormatting.RED))
                    .append(Component.literal(" or ").withStyle(style -> style.withColor(0x90EE90)))
                    .append(Component.literal("Superheated Solar Plasma").withStyle(ChatFormatting.RED))
                    .append(Component.literal(" to maintain heat. Somehow that heat from Superheated Solar Plasma allows this machine to " +
                            "take in huge amounts of resources and fuse them way faster than conventional fusion reactors.").withStyle(style -> style.withColor(0x90EE90))))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("When the reactor is idle: ").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal("Loses ").withStyle(style -> style.withColor(0x90EE90)))
                    .append(Component.literal("100K/s").withStyle(ChatFormatting.RED))
                    .append(Component.literal(" without Superheated Solar Plasma. Gains ").withStyle(style -> style.withColor(0x90EE90)))
                    .append(Component.literal("50K/s").withStyle(ChatFormatting.RED))
                    .append(Component.literal(" with Superheated Solar Plasma.").withStyle(style -> style.withColor(0x90EE90))))
            .tooltips(Component.literal("When the reactor is working: ").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal("Generates ").withStyle(style -> style.withColor(0x90EE90)))
                    .append(Component.literal("60K/s").withStyle(ChatFormatting.RED))
                    .append(Component.literal(". Use ").withStyle(style -> style.withColor(0x90EE90)))
                    .append(Component.literal("Deionized Water").withStyle(ChatFormatting.RED))
                    .append(Component.literal(" to cool by ").withStyle(style -> style.withColor(0x90EE90)))
                    .append(Component.literal("60K/s").withStyle(ChatFormatting.RED))
                    .append(Component.literal(".").withStyle(style -> style.withColor(0x90EE90))))
            .tooltips(Component.literal("Maximum heat of the reactor is 7500K.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Reactor controller emits redstone: ").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal("1 redstone strength").withStyle(ChatFormatting.RED))
                    .append(Component.literal(" per ").withStyle(style -> style.withColor(0x90EE90)))
                    .append(Component.literal("500K").withStyle(ChatFormatting.RED))
                    .append(Component.literal(" of heat.").withStyle(style -> style.withColor(0x90EE90))))
            .register();

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);

        if (isFormed()) {
            textList.add(Component.translatable("Heat: " + (int) heat + "K").withStyle(ChatFormatting.AQUA));
            textList.add(Component.literal("Redstone Power: " + getOutputSignal(null)).withStyle(ChatFormatting.RED));
        }
    }

    public int getColor() {
        return 0xFFA9A9A9;
    }

    public static void init() {
    }
}
