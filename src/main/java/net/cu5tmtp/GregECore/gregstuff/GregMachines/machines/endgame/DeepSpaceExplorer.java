package net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.endgame;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
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
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.DroneAccessHatchPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries.GregERenederRegistries;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregEModifiers;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.cu5tmtp.GregECore.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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

public class DeepSpaceExplorer extends WorkableElectricMultiblockMachine{

    public DeepSpaceExplorer(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    private final List<IItemHandler> cachedDroneHandler = new ArrayList<>();

    private int neededDrone;

    public int droneIn;

    private TickableSubscription checkingSubscription;

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        this.cachedDroneHandler.clear();

        for (IMultiPart part : getParts()) {

            if (!(part instanceof DroneAccessHatchPartMachine)) {
                continue;
            }

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                handlerList.getCapability(ItemRecipeCapability.CAP).stream()
                        .filter(IItemHandler.class::isInstance)
                        .map(IItemHandler.class::cast)
                        .forEach(this.cachedDroneHandler::add);
            }
        }

        if (checkingSubscription != null) {
            checkingSubscription.unsubscribe();
            checkingSubscription = null;
        }
        checkingSubscription = this.subscribeServerTick(this::checkWhichDroneIsIn);
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {

        assert recipe != null;
        neededDrone = recipe.data.getInt("drone");
        if (neededDrone > droneIn){
            return false;
        }
        return super.beforeWorking(recipe);
    }

    @Override
    public boolean onWorking() {
        if (neededDrone > droneIn){
            return false;
        }
        return super.onWorking();
    }

    @Override
    public void onStructureInvalid() {
        if (checkingSubscription != null) {
            checkingSubscription.unsubscribe();
            checkingSubscription = null;
        }
        super.onStructureInvalid();
        droneIn = 0;
    }

    private void checkWhichDroneIsIn() {

        if (getOffsetTimer() % 20 == 0){
            return;
        }

        for (IItemHandler handler : this.cachedDroneHandler) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                Item item = stack.getItem();

                if (item == ModItems.SPACESHIP1.get()) droneIn = 1;
                else if(item == ModItems.SPACESHIP2.get()) droneIn = 2;
                else if(item == ModItems.SPACESHIP3.get()) droneIn = 3;
                else droneIn = 0;

            }
        }
    }

    public static MachineDefinition DEEPSPACEEXPLORER = REGISTRATE
            .multiblock("deep_space_explorer", DeepSpaceExplorer::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GregERecipeTypes.DEEP_SPACE_EXPLORE)
            .recipeModifiers(GregEModifiers::deepSpaceExplorerRocketBoost)
            .appearanceBlock(GCYMBlocks.CASING_ATOMIC)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("bbbbbbbcccb", "bbbbbbbcdcb", "bbbbbbbcdcb", "bbbbbbbcdcb", "bbbbbbbcdcb", "bbbbbbbcdcb", "bbbbbbbcdcb", "bbbbbbbcccb", "bbbbbbbeeeb")
                        .aisle("aaaaabceeec", "ahhhabcbbbc", "ahhhabcbbbc", "ahhhabcbbbc", "aaaaabcbbbc", "bbbbbbcbbbc", "bbbbbbcbbbc", "bbbbbbcbbbc", "bbbbbbebbbe")
                        .aisle("aaaaabceeec", "aggggggbbbd", "agggabcbbbd", "aggggggbbbd", "aaaaabcbbbd", "bbbbbbcbbbd", "bbbbbbcbbbd", "bbbbbbcbbbc", "bbbbbbebbbe")
                        .aisle("aaaaabceeec", "affgabcbbbc", "affgabcbbbc", "agggabcbbbc", "aaaaabcbbbc", "bbbbbbcbbbc", "bbbbbbcbbbc", "bbbbbbcbbbc", "bbbbbbebbbe")
                        .aisle("bbbbbbbcicb", "bbbbbbbcdcb", "bbbbbbbcdcb", "bbbbbbbcdcb", "bbbbbbbcdcb", "bbbbbbbcdcb", "bbbbbbbcdcb", "bbbbbbbcccb", "bbbbbbbeeeb")
                        .where("e", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:vibration_safe_casing"))))
                        .where("b", Predicates.any())
                        .where("c", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:atomic_casing")))
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(DroneAccessHatchPartMachine.getPartAbility()).setMaxGlobalLimited(1).setPreviewCount(1)))
                        .where("d", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:laminated_glass"))))
                        .where("a", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:computer_casing"))))
                        .where("f", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:monitor"))))
                        .where("g", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:advanced_computer_casing"))))
                        .where("h", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:computer_heat_vent"))))
                        .where('i', Predicates.controller(blocks(definition.getBlock())))
                        .build();
            })
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/gcym/atomic_casing"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
                    .andThen(b -> b.addDynamicRenderer(GregERenederRegistries::createDeepSpaceRender)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Deep Space Exploration").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Due to the immense strain put on your body by traveling at near-light speeds, you decided it is better to send unmanned drones into deep space. " +
                    "These autonomous units can travel vast distances without requiring maintenance or refueling, routinely sending back recovery probes with their findings. " +
                    "Utilize these returned exotic materials to manufacture revolutionary new technologies").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Avaible drones & their avaible solar systems:").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Milano: 61 Cygni, Struve 2398, 100% recipe time").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Bebop: Lacaille 8760, Gliese 1, 80% recipe time").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Cepheus: 70 Ophiuchi, Stein 2051, 60% recipe time").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Put the drones in the drone access hatch.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("Better drones can also reach the systems reachable by the previous drones.").withStyle(style -> style.withColor(0x90EE90)))
            .register();

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);

        switch (droneIn){
            case 1 -> textList.add(Component.literal("Drone in: Milano").withStyle(ChatFormatting.GOLD));
            case 2 -> textList.add(Component.literal("Drone in: Bebop").withStyle(ChatFormatting.GOLD));
            case 3 -> textList.add(Component.literal("Drone in: Cepheus").withStyle(ChatFormatting.GOLD));
            default -> textList.add(Component.literal("No drone inserted!").withStyle(ChatFormatting.RED));
        }
    }

    public int getDroneIn() {
        return droneIn;
    }

    public static void init() {
    }
}
