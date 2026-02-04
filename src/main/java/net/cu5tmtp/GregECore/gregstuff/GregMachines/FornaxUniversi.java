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
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.RepairPartsInputPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries.GregERenederRegistries;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

public class FornaxUniversi extends WorkableElectricMultiblockMachine implements IRedstoneSignalMachine {

    public FornaxUniversi(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    private TickableSubscription logicSubscription;

    private int repairTimer = 0;
    private int recipeTicks = 0;
    private boolean needsRepair = false;
    private boolean hasTriggeredFailureThisRecipe = false;
    private static final int REPAIR_TIME_LIMIT = 100;
    private static final int FAILURE_TRIGGER_TICK = 100;
    private static final ItemStack REPAIR_ITEM = new ItemStack(Items.IRON_INGOT);

    private final List<IItemHandler> cachedRepairPartHandler = new ArrayList<>();

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        this.cachedRepairPartHandler.clear();

        for (IMultiPart part : getParts()) {

            if (!(part instanceof RepairPartsInputPartMachine)) {
                continue;
            }

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                handlerList.getCapability(ItemRecipeCapability.CAP).stream()
                        .filter(IItemHandler.class::isInstance)
                        .map(IItemHandler.class::cast)
                        .forEach(this.cachedRepairPartHandler::add);
            }
        }

        if (logicSubscription != null) {
            logicSubscription.unsubscribe();
            logicSubscription = null;
        }
        logicSubscription = this.subscribeServerTick(this::manageLogic);
    }

    private void manageLogic() {

        if (needsRepair) {

            if (repairTimer > 0) {
                repairTimer--;
                if (consumeRepairItem()) {
                    this.needsRepair = false;
                    this.repairTimer = 0;
                    this.recipeLogic.setStatus(RecipeLogic.Status.WORKING);
                }
            } else {
                this.explodeMachine();
            }
            return;
        }

        if (this.recipeLogic.isWorking()) {

            recipeTicks++;

            if (!hasTriggeredFailureThisRecipe && recipeTicks >= FAILURE_TRIGGER_TICK) {
                this.needsRepair = true;
                this.hasTriggeredFailureThisRecipe = true;
                this.repairTimer = REPAIR_TIME_LIMIT;
                this.recipeLogic.setStatus(RecipeLogic.Status.SUSPEND);
            }
        } else {
            if (recipeTicks != 0) {
                recipeTicks = 0;
                hasTriggeredFailureThisRecipe = false;
            }
        }
    }

    @Override
    public void afterWorking() {
        hasTriggeredFailureThisRecipe = false;
        super.afterWorking();
    }

    private boolean consumeRepairItem() {
        for (IItemHandler handler : cachedRepairPartHandler) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stackInSlot = handler.getStackInSlot(i);
                if (ItemStack.isSameItem(stackInSlot, REPAIR_ITEM) && stackInSlot.getCount() >= 1) {
                    handler.extractItem(i, 1, false);
                    return true;
                }
            }
        }
        return false;
    }


    private void explodeMachine() {
        Level level = getLevel();
        if (level != null && !level.isClientSide) {
            var pos = getPos();
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 15.0F, Level.ExplosionInteraction.BLOCK);
        }
    }

    @Override
    public int getOutputSignal(@Nullable Direction side) {
        if (this.needsRepair) {
            return 15;
        }
        return 0;
    }

    @Override
    public int getAnalogOutputSignal() {
        return getOutputSignal(null);
    }

    @Override
    public boolean canConnectRedstone(Direction side) {
        return true;
    }




    public static MachineDefinition FORNAX_UNIVERSI = REGISTRATE
            .multiblock("fornaxuniversi", FornaxUniversi::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GregERecipeTypes.FORNAX_UNIVERSI_ACCELERETION)
            .recipeModifiers(GTRecipeModifiers.OC_PERFECT)
            .appearanceBlock(GCYMBlocks.CASING_ATOMIC)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "C           C", "E           E", "C           C", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
                        .aisle("             ", "             ", "             ", "             ", "             ", "             ", " C         C ", " E         E ", "             ", "             ", "             ", " E         E ", " C         C ", "             ", "             ", "             ", "             ", "             ", "             ")
                        .aisle("  DDD   DDD  ", "             ", "             ", "             ", "  C       C  ", "  E       E  ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "  E       E  ", "  C       C  ", "             ", "             ", "             ", "  DDD   DDD  ")
                        .aisle("  DDDBBBDDD  ", "   C     C   ", "   C     C   ", "   E     E   ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "   E     E   ", "   C     C   ", "   C     C   ", "  DDDCCCDDD  ")
                        .aisle("  DDDBBBDDD  ", "     C C     ", "     C C     ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "     C C     ", "     C C     ", "  DDDCCCDDD  ")
                        .aisle("   BBBBBBB   ", "    C   C    ", "    C   C    ", "     C C     ", "     C C     ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "     C C     ", "     C C     ", "    C   C    ", "    C   C    ", "   CCCCCCC   ")
                        .aisle("   IBBBBBB   ", "      C      ", "      G      ", "      G      ", "      C      ", "      C      ", "      F      ", "             ", "             ", "             ", "             ", "             ", "      F      ", "      C      ", "      C      ", "      G      ", "      G      ", "      C      ", "   CCCCCCC   ")
                        .aisle("   BBBBBBB   ", "    C   C    ", "    C   C    ", "     C C     ", "     C C     ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "     C C     ", "     C C     ", "    C   C    ", "    C   C    ", "   CCCCCCC   ")
                        .aisle("  DDDBBBDDD  ", "     C C     ", "     C C     ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "     C C     ", "     C C     ", "  DDDCCCDDD  ")
                        .aisle("  DDDBABDDD  ", "   C     C   ", "   C     C   ", "   E     E   ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "   E     E   ", "   C     C   ", "   C     C   ", "  DDDCCCDDD  ")
                        .aisle("  DDD   DDD  ", "             ", "             ", "             ", "  C       C  ", "  E       E  ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "  E       E  ", "  C       C  ", "             ", "             ", "             ", "  DDD   DDD  ")
                        .aisle("             ", "             ", "             ", "             ", "             ", "             ", " C         C ", " E         E ", "             ", "             ", "             ", " E         E ", " C         C ", "             ", "             ", "             ", "             ", "             ", "             ")
                        .aisle("             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "C           C", "E           E", "C           C", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
                        .where('A', Predicates.controller(blocks(definition.getBlock())))
                        .where('B', Predicates.blocks(GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING.get())
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(2)))
                        .where('C', Predicates.blocks(GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING.get()))
                        .where('D', Predicates.blocks(GCYMBlocks.CASING_STRESS_PROOF.get()))
                        .where('E', Predicates.blocks(GTBlocks.CASING_HSSE_STURDY.get()))
                        .where('F', Predicates.blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                        .where('G', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("minecraft:chain"))))
                        .where('I', Predicates.abilities(RepairPartsInputPartMachine.getPartAbility()).setMaxGlobalLimited(1).setPreviewCount(1))
                        .where(' ', Predicates.any())
                        .build();
            })
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/gcym/high_temperature_smelting_casing"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
                    .andThen(b -> b.addDynamicRenderer(GregERenederRegistries::createFornaxUniversiRender)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Singularity Forging").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Star starts with 1 x 10³⁰ tons. If your star drops below 0 or over 500 x 10³⁰ tons, the multiblock explodes. Each crafting recipe has some sort of weight cost," +
                    " due to the star fusing some of its own weight into it whenever it forms a singularity. " +
                    "You can increase the weight of the star if you feed it correct items. The correct items are shown below with their weight value.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Cobblestone: 0.01 x 10³⁰ tons").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Reactor controller emits redstone: ").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal("1 redstone strength").withStyle(ChatFormatting.RED))
                    .append(Component.literal(" per ").withStyle(style -> style.withColor(0x90EE90)))
                    .append(Component.literal("30 x 10³⁰").withStyle(ChatFormatting.RED))
                    .append(Component.literal(" tons.").withStyle(style -> style.withColor(0x90EE90))))
            .register();

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);

    }

    public static void init() {
    }
}
