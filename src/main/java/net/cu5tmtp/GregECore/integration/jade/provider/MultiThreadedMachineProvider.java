package net.cu5tmtp.GregECore.integration.jade.provider;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import net.cu5tmtp.GregECore.gregstuff.GregRecipeLogic.MultiThreadedRecipeLogic;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;

@SuppressWarnings("all")
public class MultiThreadedMachineProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final ResourceLocation UID = new ResourceLocation("gregecore", "multithread_info");

    public MultiThreadedMachineProvider() {
    }

    @Override
    public int getDefaultPriority() {
        return -10;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {

        CompoundTag data = accessor.getServerData();

        if (data.contains("MultiThreadData")) {

            tooltip.remove(new ResourceLocation("gtceu", "workable_provider"));

            if (data.contains("gtceu:workable_provider")) {
                data.remove("gtceu:workable_provider");
            }

            CompoundTag threadData = data.getCompound("MultiThreadData");
            int active = threadData.getInt("Active");
            int max = threadData.getInt("Max");

            tooltip.add(Component.literal("§5Threads Active: §f" + active + " / " + max));

            if (active > 0) {
                ListTag list = threadData.getList("ProgressList", Tag.TAG_COMPOUND);
                var helper = tooltip.getElementHelper();

                for (int i = 0; i < list.size(); i++) {
                    CompoundTag tag = list.getCompound(i);
                    int currentProgress = tag.getInt("Progress");
                    int maxProgress = tag.getInt("Duration");

                    ItemStack outputItem = ItemStack.of(tag.getCompound("OutputItem"));

                    Component text;

                    if (maxProgress < 20) {
                        text = Component.translatable("gtceu.jade.progress_tick", currentProgress, maxProgress);
                    } else {
                        text = Component.translatable("gtceu.jade.progress_sec", Math.round(currentProgress / 20.0F), Math.round(maxProgress / 20.0F));
                    }

                    if (maxProgress > 0) {
                        float progressRatio = (float) currentProgress / maxProgress;
                        int color = 0xFFd32ccb;

                        var progressBar = helper.progress(
                                progressRatio,
                                text,
                                helper.progressStyle().color(color).textColor(-1),
                                Util.make(BoxStyle.DEFAULT, style -> style.borderColor = 0xFF555555),
                                true
                        );

                        progressBar.translate(new Vec2(0, 3.0F));

                        if (!outputItem.isEmpty()) {
                            tooltip.add(helper.item(outputItem));
                            tooltip.append(helper.spacer(3, 0));
                            tooltip.append(progressBar);
                        } else {
                            tooltip.add(progressBar);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof MetaMachineBlockEntity be) {
            MetaMachine machine = be.getMetaMachine();

            if (machine instanceof IRecipeLogicMachine rlm) {

                int activeCount = -1;
                int maxCount = -1;
                Iterable<?> threads = null;

                if (rlm.getRecipeLogic() instanceof MultiThreadedRecipeLogic logic && logic.isMultiThreaded()) {
                    activeCount = logic.getActiveThreads().size();
                    maxCount = logic.getMaxThreads();
                    threads = logic.getActiveThreads();
                }

                if (threads != null) {
                    CompoundTag threadData = new CompoundTag();

                    threadData.putInt("Active", activeCount);
                    threadData.putInt("Max", maxCount);

                    ListTag list = new ListTag();
                    for (Object obj : threads) {
                        CompoundTag tag = new CompoundTag();
                        com.gregtechceu.gtceu.api.recipe.GTRecipe recipe = null;

                        if (obj instanceof MultiThreadedRecipeLogic.RecipeThread thread) {
                            tag.putInt("Progress", thread.progress);
                            tag.putInt("Duration", thread.duration);
                            recipe = thread.recipe;
                        }

                        ItemStack outputItem = ItemStack.EMPTY;
                        if (recipe != null) {
                            var itemOutputs = recipe.getOutputContents(GTRecipeCapabilities.ITEM);
                            if (itemOutputs != null && !itemOutputs.isEmpty()) {
                                Object content = itemOutputs.get(0).getContent();
                                if (content instanceof ItemStack stack) {
                                    outputItem = stack.copy();
                                } else if (content instanceof SizedIngredient sized) {
                                    ItemStack[] stacks = sized.getItems();
                                    if (stacks.length > 0) {
                                        outputItem = stacks[0].copy();
                                        outputItem.setCount((int) sized.getAmount());
                                    }
                                }
                            }
                        }
                        tag.put("OutputItem", outputItem.save(new CompoundTag()));

                        list.add(tag);
                    }
                    threadData.put("ProgressList", list);
                    data.put("MultiThreadData", threadData);
                }
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}