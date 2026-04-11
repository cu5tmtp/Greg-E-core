package net.cu5tmtp.GregECore.gregstuff.GregMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.IRedstoneSignalMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.AscencionPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.RepairPartsInputPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.StarFeederPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries.GregERenederRegistries;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.cu5tmtp.GregECore.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

public class AscencionAltar extends WorkableElectricMultiblockMachine implements IRedstoneSignalMachine {

    public AscencionAltar(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    private final List<IItemHandler> cachedAscencionHandler = new ArrayList<>();

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        this.cachedAscencionHandler.clear();

        for (IMultiPart part : getParts()) {

            if (!(part instanceof StarFeederPartMachine)) {
                continue;
            }

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                handlerList.getCapability(ItemRecipeCapability.CAP).stream()
                        .filter(IItemHandler.class::isInstance)
                        .map(IItemHandler.class::cast)
                        .forEach(this.cachedAscencionHandler::add);
            }
        }
    }

    public static MachineDefinition ASCENCION_ALTAR = REGISTRATE
            .multiblock("ascencionaltar", AscencionAltar::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GregERecipeTypes.ASCENCION_ALTAR_DONATION)
            .recipeModifiers(GTRecipeModifiers.OC_PERFECT)
            .appearanceBlock(GCYMBlocks.CASING_ATOMIC)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaacaa", "aaaaaaaiaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa")
                        .aisle("aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaacaa", "aaaaaaacaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa")
                        .aisle("aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaacccga", "aaaacfccaa", "aaaacaaaaa", "aaacaaaaaa", "aaaiaaaaaa", "aaaaaaaaaa")
                        .aisle("aaaaacccaa", "aaaaacccaa", "aaaaacecaa", "aaaaacccaa", "aaaaccccca", "aaccccaaaa", "afcaaaaaaa", "caaaaaaaaa", "iaaaaaaaaa", "aaaaaaaaaa")
                        .aisle("aaaagcccca", "aaaacaaafa", "aaaacaaaca", "aaaaccccca", "aaaccccaaa", "aaadcaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa")
                        .aisle("aaaaccccca", "aaaacaaaca", "aaaacaaaca", "aaaaecccca", "aaaccchaaa", "aacccaaaaa", "accaaaaaaa", "caaaaaaaaa", "caaaaaaaaa", "iaaaaaaaaa")
                        .aisle("aaaaacccaa", "aaaaaeccaa", "aaaaaccfaa", "aaaaacccga", "aaacccgaaa", "aaaccaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa")
                        .aisle("aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaacccaa", "aaaaccaaaa", "aaacdaaaaa", "aaecaaaaaa", "acaaaaaaaa", "aiaaaaaaaa", "aaaaaaaaaa")
                        .aisle("aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa", "aaaaaaaaaa")
                        .where("a", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("minecraft:air"))))
                        .where("c", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("bloodmagic:blankrune"))))
                        .where("d", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("bloodmagic:dislocationrune"))))
                        .where("e", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("bloodmagic:sacrificerune"))))
                        .where("f", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("bloodmagic:bettercapacityrune"))))
                        .where("g", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("bloodmagic:altarcapacityrune"))))
                        .where('h', Predicates.controller(blocks(definition.getBlock())))
                        .where('i', Predicates.abilities(AscencionPartMachine.getPartAbility()).setMaxGlobalLimited(5).setPreviewCount(5))
                        .build();
            })
            .model(createWorkableCasingMachineModel(
                    GregECore.id("block/blankrune"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
                    .andThen(b -> b.addDynamicRenderer(GregERenederRegistries::createAscencionAltarRender)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Ascencion").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Last hurdle stands between you and godhood. The ancient skyblock gods say to give them their toll.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Find the lost tomes of skyblock knowledge. Each shard has its own riddle to solve. You can get clues on them in EMI. " +
                    "Then place them in the altars, one on each finger.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Once you place the items in, step back, and enjoy the coolest animation you have ever seen in Minecraft.").withStyle(style -> style.withColor(0x90EE90)))
            .register();

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
    }

    public static void init() {
    }
}
