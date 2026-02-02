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


    public static MachineDefinition FORNAX_UNIVERSI = REGISTRATE
            .multiblock("fornaxuniversi", FornaxUniversi::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GregERecipeTypes.FORNAX_UNIVERSI_ACCELERETION)
            .recipeModifiers(GTRecipeModifiers.OC_PERFECT)
            .appearanceBlock(GCYMBlocks.CASING_ATOMIC)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("  CCCCCCC  ", " CCDDDDDCC ", "CCDFFFFFDCC", "CDFFFFFFFDC", "CDFFFFFFFDC", "CDFFFFFFFDC", "CDFFFFFFFDC", "CDFFFFFFFDC", "CCDFFFFFDCC", " CCDDDDDCC ", "  CCCCCCC  ")
                        .aisle(" CCDDDDDCC ", "CEE     EEC", "CE       EC", "D         D", "D         D", "D         D", "D         D", "D         D", "CE       EC", "CEE     EEC", " CCDDDDDCC ")
                        .aisle("CCDFFFFFDCC", "CE       EC", "D         D", "F         F", "F         F", "F         F", "F         F", "F         F", "D         D", "CE       EC", "CCDFFFFFDCC")
                        .aisle("CDFFFFFFFDC", "D         D", "F         F", "F         F", "F         F", "F         F", "F         F", "F         F", "F         F", "D         D", "CDFFFFFFFDC")
                        .aisle("CDFFFFFFFDC", "D         D", "F         F", "F         F", "F         F", "F         F", "F         F", "F         F", "F         F", "D         D", "CDFFFFFFFDC")
                        .aisle("CDFFFFFFFDC", "D         D", "F         F", "F         F", "F         F", "F         F", "F         F", "F         F", "F         F", "D         D", "CDFFFFFFFDC")
                        .aisle("CDFFFFFFFDC", "D         D", "F         F", "F         F", "F         F", "F         F", "F         F", "F         F", "F         F", "D         D", "CDFFFFFFFDC")
                        .aisle("CDFFFFFFFDC", "D         D", "F         F", "F         F", "F         F", "F         F", "F         F", "F         F", "F         F", "D         D", "CDFFFFFFFDC")
                        .aisle("CCDFFFFFDCC", "CE       EC", "D         D", "F         F", "F         F", "F         F", "F         F", "F         F", "D         D", "CE       EC", "CCDFFFFFDCC")
                        .aisle(" CCDDDDDCC ", "CEE     EEC", "CE       EC", "D         D", "D         D", "D         D", "D         D", "D         D", "CE       EC", "CEE     EEC", " CCDDDDDCC ")
                        .aisle("  BBBABBB  ", " CCDDDDDCC ", "CCDFFFFFDCC", "CDFFFFFFFDC", "CDFFFFFFFDC", "CDFFFFFFFDC", "CDFFFFFFFDC", "CDFFFFFFFDC", "CCDFFFFFDCC", " CCDDDDDCC ", "  CCCCCCC  ")
                        .where('A', Predicates.controller(blocks(definition.getBlock())))
                        .where('B', Predicates.blocks(GCYMBlocks.CASING_ATOMIC.get())
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(2))
                                .or(Predicates.abilities(StarFeederPartMachine.getPartAbility()).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1)))
                        .where('C', Predicates.blocks(GCYMBlocks.CASING_ATOMIC.get()))
                        .where('D', Predicates.blocks(GCYMBlocks.CASING_VIBRATION_SAFE.get()))
                        .where('E', Predicates.blocks(GCYMBlocks.CASING_INDUSTRIAL_STEAM.get()))
                        .where('F', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("connectedglass:clear_glass"))))
                        .where(' ', Predicates.air())
                        .build();
            })
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/gcym/atomic_casing"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
                    .andThen(b -> b.addDynamicRenderer(GregERenederRegistries::createFornaxUniversiRender)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Singularity Forging").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("You have done it. You made a machine capable of making and sustaining stars. While some might say its useful for energy," +
                    " you have different ideas. Using the immense gravitational forces inside a stars core, you just might forge unthinkable items.").withStyle(style -> style.withColor(0x90EE90)))
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
