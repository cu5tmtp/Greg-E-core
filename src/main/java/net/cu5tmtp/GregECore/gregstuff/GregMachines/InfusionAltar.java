package net.cu5tmtp.GregECore.gregstuff.GregMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.PedestalPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.essentiaParts.*;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries.GregERenederRegistries;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class InfusionAltar extends WorkableElectricMultiblockMachine {


    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            InfusionAltar.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER
    );

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    protected final List<IFluidHandler> cachedEssentiaHandler = new ArrayList<>();
    protected final List<IItemHandler> cachedPedestalHandler = new ArrayList<>();

    @DescSynced
    public final List<ItemStack> itemsForRenderer = new ArrayList<>();

    public InfusionAltar(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        List<ItemStack> currentInputItems = new ArrayList<>();

        for (IItemHandler handler : cachedPedestalHandler) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stackInSlot = handler.getStackInSlot(i);

                if (!stackInSlot.isEmpty()) {
                    ItemStack renderStack = stackInSlot.copy();
                    renderStack.setCount(1);
                    currentInputItems.add(renderStack);
                }
            }
        }

        boolean changed = currentInputItems.size() != this.itemsForRenderer.size();
        if (!changed) {
            for (int i = 0; i < currentInputItems.size(); i++) {
                if (!ItemStack.isSameItemSameTags(currentInputItems.get(i), this.itemsForRenderer.get(i))) {
                    changed = true;
                    break;
                }
            }
        }

        if (changed) {
            this.itemsForRenderer.clear();
            this.itemsForRenderer.addAll(currentInputItems);
        }

        return super.beforeWorking(recipe);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        this.cachedEssentiaHandler.clear();
        this.cachedPedestalHandler.clear();

        for (IMultiPart part : getParts()) {
            if (!(part instanceof AerInputPartMachine
                    || part instanceof AquaInputPartMachine
                    || part instanceof IgnisInputPartMachine
                    || part instanceof OrdoInputPartMachine
                    || part instanceof PerditioInputPartMachine
                    || part instanceof TerraInputPartMachine
            )) {
                continue;
            }

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                handlerList.getCapability(FluidRecipeCapability.CAP).stream()
                        .filter(IFluidHandler.class::isInstance)
                        .map(IFluidHandler.class::cast)
                        .forEach(this.cachedEssentiaHandler::add);
            }
        }

        for (IMultiPart part : getParts()) {
            if (!(part instanceof PedestalPartMachine)) {
                continue;
            }

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                handlerList.getCapability(ItemRecipeCapability.CAP).stream()
                        .filter(IItemHandler.class::isInstance)
                        .map(IItemHandler.class::cast)
                        .forEach(this.cachedPedestalHandler::add);
            }
        }
    }

    @Override
    public void onStructureInvalid() {
        this.cachedEssentiaHandler.clear();
        this.itemsForRenderer.clear();
        this.cachedPedestalHandler.clear();
        super.onStructureInvalid();
    }

    @Override
    public void afterWorking() {
        this.itemsForRenderer.clear();
        super.afterWorking();
    }

    public static MachineDefinition INFUSION_ALTAR = REGISTRATE
            .multiblock("infusion_altar", InfusionAltar::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GregERecipeTypes.INFUSION_ALTAR_INFUSING)
            .appearanceBlock(GCYMBlocks.CASING_ATOMIC)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("aabbbbbaa", "aaaaaaaaa")
                        .aisle("abbbbbbba", "aaadddaaa")
                        .aisle("bbbbbbbbb", "aaaaaaaaa")
                        .aisle("ebbbbbbbi", "adaaaaada")
                        .aisle("gbbbbbbbj", "adaaaaada")
                        .aisle("hbbbbbbbk", "adaaaaada")
                        .aisle("bbbbbbbbb", "aaaaaaaaa")
                        .aisle("abbbbbbba", "aaadddaaa")
                        .aisle("aabfcfbaa", "aaaaaaaaa")
                        .where("a", Predicates.any())
                        .where("b", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:solid_machine_casing"))))
                        .where("d", Predicates.abilities(PedestalPartMachine.getPartAbility()))
                        .where("e", Predicates.abilities(AerInputPartMachine.getPartAbility()))
                        .where('c', Predicates.controller(blocks(definition.getBlock())))
                        .where('f', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:solid_machine_casing")))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1)))
                        .where("g", Predicates.abilities(TerraInputPartMachine.getPartAbility()))
                        .where("h", Predicates.abilities(AquaInputPartMachine.getPartAbility()))
                        .where("i", Predicates.abilities(OrdoInputPartMachine.getPartAbility()))
                        .where("j", Predicates.abilities(PerditioInputPartMachine.getPartAbility()))
                        .where("k", Predicates.abilities(IgnisInputPartMachine.getPartAbility()))
                        .where(' ', Predicates.air())
                        .build();
            })
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
                    .andThen(b -> b.addDynamicRenderer(GregERenederRegistries::createInfusionAltarRender)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Material Compressing").withStyle(style -> style.withColor(0xFFD700)))
            .register();

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
    }

    public static void init() {
    }
}
