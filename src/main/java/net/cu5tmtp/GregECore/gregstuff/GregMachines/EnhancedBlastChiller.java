package net.cu5tmtp.GregECore.gregstuff.GregMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregEModifiers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class EnhancedBlastChiller extends WorkableElectricMultiblockMachine {

    public EnhancedBlastChiller(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public int numberOfCores = 0;

    @Override
    public void onStructureFormed() {
        checkHowManyCores();
        super.onStructureFormed();
    }

    public void checkHowManyCores(){
        Level level = getLevel();
        if (level == null || level.isClientSide) return;
        var back = getFrontFacing().getOpposite();

        int maxDistance = 14;
        for (int i = 1; i <= maxDistance; i++) {

            BlockPos currentPos = getPos().relative(back, i);
            Block block = level.getBlockState(currentPos).getBlock();
            String blockId = block.toString();

            if (blockId.equals("Block{gtceu:frostproof_machine_casing}")) {
                return;
            }

            if (blockId.equals("Block{gregecore:frostcore}")) {
                this.numberOfCores++;
            }
        }
    }

    public static MachineDefinition ENHANCEDBLASTCHILLER = REGISTRATE
            .multiblock("enhancedblastchiller", EnhancedBlastChiller::new)
            .rotationState(RotationState.ALL)
            .recipeType(GTRecipeTypes.VACUUM_RECIPES)
            .recipeModifiers(GregEModifiers::enhancedBlastChillerCoreBoost, GTRecipeModifiers.OC_PERFECT)
            .appearanceBlock(CASING_INVAR_HEATPROOF)
            .pattern(definition -> {
                return FactoryBlockPattern.start(RIGHT, UP, BACK)
                        .aisle("BBBBB", "BDBDB", "BBABB", "BDBDB", "BBBBB")
                        .aisle("BBBBB", "CD DC", "C E C", "CD DC", "BBBBB").setRepeatable(3, 14)
                        .aisle("BBBBB", "BDBDB", "BBBBB", "BDBDB", "BBBBB")
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
                        .where('E', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gregecore:frostcore"))))
                        .where(' ', Predicates.any())
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_frost_proof"),
                                 GTCEu.id("block/multiblock/distillation_tower"))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Glacial Core and Perfect Overclock").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("This freezer gets better with every core you supply it. " +
                    "Each core improves the speed at which the machine is chilling the inputs.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("For every Glacial Core in this machine, it gets 16 parallels and 6.5% faster recipes. " +
                    "Maximum Glacial Cores in a machine is 14, minimum is 3.").withStyle(style -> style.withColor(0x90EE90)))
            .register();

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);

        textList.add(Component.literal("Glacial Cores: " + this.numberOfCores).withStyle(ChatFormatting.AQUA));
        textList.add(Component.literal("Recipe time reduction: " + this.numberOfCores * 6.5 + "%").withStyle(ChatFormatting.AQUA));
        textList.add(Component.literal("Parallels: " + this.numberOfCores * 16).withStyle(ChatFormatting.AQUA));
    }

    public int getNumberOfCores() {
        return this.numberOfCores;
    }

    public static void init() {}
}
