package net.cu5tmtp.GregECore.gregstuff.GregMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.transfer.fluid.FluidHandlerList;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.utils.GTTransferUtils;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.AdvancedParallelBoosterPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.CoolantInputPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.ParallelBoosterPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregEModifiers;
import net.minecraft.ChatFormatting;
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
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

@SuppressWarnings("removal")
public class BigFreezer extends WorkableElectricMultiblockMachine {

    public BigFreezer(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    private IFluidHandler coolantHandler;

    public int parallelBooster = 0;

    @Override
    public void onStructureFormed() {

        super.onStructureFormed();
        List<IFluidHandler> coolantContainers = new ArrayList<>();

        for (IMultiPart part : getParts()) {

            if(part instanceof ParallelBoosterPartMachine){
                parallelBooster = 1;
            }

            if(part instanceof AdvancedParallelBoosterPartMachine){
                parallelBooster = 2;
            }

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

        this.coolantHandler = new FluidHandlerList(coolantContainers);


    }

    @Override
    public boolean onWorking() {

        int amountToDrain = 100;
        Fluid coolant = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("gtceu:ice"));
        FluidStack resource = new FluidStack(coolant, amountToDrain);

        if (getOffsetTimer() % 10 == 0) {

            FluidStack simulation = GTTransferUtils.drainFluidAccountNotifiableList(
                    coolantHandler,
                    resource,
                    IFluidHandler.FluidAction.SIMULATE
            );

            if(simulation.isEmpty()) {
                getRecipeLogic().interruptRecipe();
                return false;
            }

            GTTransferUtils.drainFluidAccountNotifiableList(
                    coolantHandler,
                    resource,
                    IFluidHandler.FluidAction.EXECUTE
            );
        }
        return super.onWorking();
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {

        int amountToDrain = 100;
        Fluid coolant = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("gtceu:ice"));
        FluidStack resource = new FluidStack(coolant, amountToDrain);

        FluidStack simulation = GTTransferUtils.drainFluidAccountNotifiableList(
                coolantHandler,
                resource,
                IFluidHandler.FluidAction.SIMULATE
        );

        if(simulation.isEmpty()) {
            return false;
        }

        return super.beforeWorking(recipe);
    }

    public static MachineDefinition BIGFREEZER = REGISTRATE
            .multiblock("bigfreezer", BigFreezer::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.VACUUM_RECIPES)
            .recipeModifiers(GregEModifiers::bigFreezerBoost, GTRecipeModifiers.OC_PERFECT)
            .appearanceBlock(CASING_INVAR_HEATPROOF)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("BBBBB", "GCCCG", "GCCCG", "GCCCG", "BBBBB")
                        .aisle("BBBBB", "C   C", "C   C", "C   C", "BBBBB")
                        .aisle("BBBBB", "C D C", "C D C", "C D C", "BBBBB")
                        .aisle("BBBBB", "C   C", "C   C", "C   C", "BBBBB")
                        .aisle("BEAFB", "GCCCG", "GCCCG", "GCCCG", "BBBBB")
                        .where('A', Predicates.controller(blocks(definition.getBlock())))
                        .where('B', Predicates.blocks(CASING_ALUMINIUM_FROSTPROOF.get())
                                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2))
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(2))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2)))
                        .where('C', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:tempered_glass"))))
                        .where('D', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:extreme_engine_intake_casing"))))
                        .where('E', Predicates.blocks(CASING_ALUMINIUM_FROSTPROOF.get())
                                .or(Predicates.abilities(ParallelBoosterPartMachine.getPartAbility()).setMaxGlobalLimited(1))
                                .or(Predicates.abilities(AdvancedParallelBoosterPartMachine.getPartAbility()).setMaxGlobalLimited(1).setPreviewCount(1)))
                        .where('F', Predicates.abilities(CoolantInputPartMachine.getPartAbility()).setMaxGlobalLimited(1))
                        .where('G', Predicates.blocks(CASING_ALUMINIUM_FROSTPROOF.get()))
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_frost_proof"),
                                 GTCEu.id("block/multiblock/distillation_tower"))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Improved Cooling and Perfect Overclock").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("This freezer can cool inputs faster than the base one, but requires a bit of help with it.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Due to the older model of the freezing device, it requires a").withStyle(style -> style.withColor(0x90EE90))
                    .append(Component.literal(" 100mb of Liquid Ice per 10 ticks.").withStyle(style -> style.withColor(0xFF0000))))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("This machine scales with parallels:").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("Base version: 4 parallels and 10% recipe time reduction.").withStyle(style -> style.withColor(0xBF40BF)))
            .tooltips(Component.literal("With parallel multiplication part: 8 parallels and 20% recipe time reduction.").withStyle(style -> style.withColor(0xBF40BF)))
            .tooltips(Component.literal("With enhanced parallel multiplication part: 16 parallels and 30% recipe time reduction.").withStyle(style -> style.withColor(0xBF40BF)))
            .register();

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);

        switch (parallelBooster)
        {
            case 1 -> textList.add(Component.literal("Parallels: 16, Recipe time reduction: 30%").withStyle(ChatFormatting.AQUA));
            case 2 -> textList.add(Component.literal("Parallels: 8, Recipe time reduction: 20%").withStyle(ChatFormatting.AQUA));
            default -> textList.add(Component.literal("Parallels: 4, Recipe time reduction: 10%").withStyle(ChatFormatting.AQUA));
        }
    }

    public int getParallelBooster() {
        return parallelBooster == 0 ? 1 : parallelBooster * 2;
    }

    public static void init() {}
}
