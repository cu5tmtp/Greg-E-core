package net.cu5tmtp.GregECore.gregstuff.GregMachines;

import com.gregtechceu.gtceu.GTCEu;
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
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.PedestalPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.StarFeederPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries.GregERenederRegistries;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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

    protected final List<IItemHandler> cachedInfusionAltarPedestalHandler = new ArrayList<>();

    @Persisted
    public final List<ItemStack> itemsForRenderer = new ArrayList<>();

    public InfusionAltar(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        List<ItemStack> currentInputItems = new ArrayList<>();

        for (IItemHandler handler : cachedInfusionAltarPedestalHandler) {
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
            this.markDirty();
        }

        return super.beforeWorking(recipe);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        this.cachedInfusionAltarPedestalHandler.clear();

        for (IMultiPart part : getParts()) {
            if (!(part instanceof PedestalPartMachine)) {
                continue;
            }

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                handlerList.getCapability(ItemRecipeCapability.CAP).stream()
                        .filter(IItemHandler.class::isInstance)
                        .map(IItemHandler.class::cast)
                        .forEach(this.cachedInfusionAltarPedestalHandler::add);
            }
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.cachedInfusionAltarPedestalHandler.clear();
        this.itemsForRenderer.clear();
    }

    public static MachineDefinition INFUSION_ALTAR = REGISTRATE
            .multiblock("infusion_altar", InfusionAltar::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GregERecipeTypes.INFUSION_ALTAR_INFUSING)
            .recipeModifiers(GTRecipeModifiers.OC_PERFECT)
            .appearanceBlock(GCYMBlocks.CASING_ATOMIC)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("aabbbbbaa", "aaaaaaaaa")
                        .aisle("abbbbbbba", "aaadddaaa")
                        .aisle("bbbbbbbbb", "aaaaaaaaa")
                        .aisle("ebbbbbbbe", "adaaaaada")
                        .aisle("ebbbbbbbe", "adaaaaada")
                        .aisle("ebbbbbbbe", "adaaaaada")
                        .aisle("bbbbbbbbb", "aaaaaaaaa")
                        .aisle("abbbbbbba", "aaadddaaa")
                        .aisle("aabfcfbaa", "aaaaaaaaa")
                        .where("a", Predicates.any())
                        .where("b", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:solid_machine_casing"))))
                        .where("d", Predicates.abilities(PedestalPartMachine.getPartAbility()))
                        .where("e", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:watertight_casing"))))
                        .where('c', Predicates.controller(blocks(definition.getBlock())))
                        .where('f', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:solid_machine_casing")))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1)))
                        .where(' ', Predicates.air())
                        .build();
            })
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
                    .andThen(b -> b.addDynamicRenderer(GregERenederRegistries::createInfusionAltarRender)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Material Compressing").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("You have done it. You made a machine capable of making and sustaining stars. While some might say its useful for energy," +
                    " you have different ideas. Using the immense gravitational forces inside a stars core, you just might forge unthinkable items.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Star starts with 1 x 10³⁰ tons. If your star drops below 0 or over 500 x 10³⁰ tons, the multiblock explodes. Each crafting recipe has some sort of weight cost," +
                    " due to the star fusing some of its own weight into it whenever it forms a singularity. " +
                    "You can increase the weight of the star if you feed it correct items. The correct items are shown below with their weight value. Place them in the Star Feeder.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Brass Pellet: 0.1 x 10³⁰ tons").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Americium Pellet: 3 x 10³⁰ tons").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Neutronium Pellet: 10 x 10³⁰ tons").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Controller emits redstone: ").withStyle(ChatFormatting.GOLD)
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
