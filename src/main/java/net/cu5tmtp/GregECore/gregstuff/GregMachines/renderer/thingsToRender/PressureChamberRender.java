package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender;

import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.misc.PressureChamber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
@OnlyIn(Dist.CLIENT)
public class PressureChamberRender extends DynamicRender<PressureChamber, PressureChamberRender> {

    public static final Codec<PressureChamberRender> CODEC = Codec.unit(new PressureChamberRender());
    public static final DynamicRenderType<PressureChamber, PressureChamberRender> TYPE = new DynamicRenderType<>(PressureChamberRender.CODEC);

    private static final Map<BlockPos, Float> SMOOTH_PROGRESS = new HashMap<>();
    private static final Map<BlockPos, Float> LAST_RENDER_TIME = new HashMap<>();
    public PressureChamberRender() {}

    @Override
    public DynamicRenderType<PressureChamber, PressureChamberRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(PressureChamber machine, Vec3 cameraPos) {
        boolean formed = machine.isFormed();
        if (!formed) {
            BlockPos pos = machine.getPos();
            SMOOTH_PROGRESS.remove(pos);
            LAST_RENDER_TIME.remove(pos);
        }
        return formed;
    }

    @Override
    public void render(PressureChamber machine, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        float time = machine.getOffsetTimer() + partialTick;
        BlockPos pos = machine.getPos();

        var recipeLogic = machine.getRecipeLogic();
        boolean isWorking = recipeLogic.isWorking();

        float currentProgress = SMOOTH_PROGRESS.getOrDefault(pos, 0.0F);
        float visualProgress = 0.0F;

        if (isWorking && recipeLogic.getMaxProgress() > 0) {
            float serverProgress = (float) recipeLogic.getProgress() / (float) recipeLogic.getMaxProgress();
            float tickIncrement = 1.0F / (float) recipeLogic.getMaxProgress();

            float targetProgress = Mth.clamp(serverProgress + (partialTick * tickIncrement), 0.0F, 1.0F);

            if (Math.abs(currentProgress - targetProgress) > 0.8F) {
                currentProgress = targetProgress;
            } else {
                currentProgress = currentProgress + (targetProgress - currentProgress) * 0.15F;
            }
            SMOOTH_PROGRESS.put(pos, currentProgress);
            visualProgress = currentProgress;
        } else {
            if (currentProgress > 0.0F) {
                currentProgress -= 0.05F * partialTick;
                if (currentProgress < 0.0F) {
                    currentProgress = 0.0F;
                    SMOOTH_PROGRESS.remove(pos);
                } else {
                    SMOOTH_PROGRESS.put(pos, currentProgress);
                }
            } else {
                SMOOTH_PROGRESS.remove(pos);
            }
            visualProgress = currentProgress;
        }

        List<ItemStack> items = new ArrayList<>();

        if (isWorking && recipeLogic.getLastRecipe() != null) {
            var inputContents = recipeLogic.getLastRecipe().getInputContents(ItemRecipeCapability.CAP);
            if (inputContents != null) {
                for (var content : inputContents) {
                    if (content.getContent() instanceof SizedIngredient sizedIngredient) {
                        ItemStack[] stacks = sizedIngredient.getInner().getItems();
                        if (stacks != null && stacks.length > 0) {
                            items.add(stacks[0]);
                        }
                    }
                }
            }
        }

        if (items.isEmpty()) return;

        Direction front = machine.getFrontFacing();
        Direction right = front.getCounterClockWise();
        Direction back = front.getOpposite();

        float centerX = 0.5f + right.getStepX() * 1.5f + back.getStepX() * 1.5f;
        float centerY = 1.0f + (5.0f / 16.0f);
        float centerZ = 0.5f + right.getStepZ() * 1.5f + back.getStepZ() * 1.5f;

        int count = items.size();

        // Poloměr zmenšen z 1.2 na 0.75, aby nezasahoval do skla
        float maxRadius = 0.75F;
        float minRadius = 0.1F;
        float currentRadius = maxRadius - (maxRadius - minRadius) * visualProgress;

        for (int i = 0; i < count; i++) {
            ItemStack itemStack = items.get(i);

            float angle = (time * 2.0F) + ((float) i / count) * 360.0F;
            float rad = angle * Mth.DEG_TO_RAD;

            float offsetX = currentRadius * Mth.cos(rad);
            float offsetZ = currentRadius * Mth.sin(rad);

            float itemX = centerX + offsetX;
            float itemY = centerY;
            float itemZ = centerZ + offsetZ;

            poseStack.pushPose();
            poseStack.translate(itemX, itemY, itemZ);

            poseStack.mulPose(Axis.YP.rotationDegrees(time * 3.0F));
            poseStack.scale(0.8F, 0.8F, 0.8F);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    itemStack,
                    ItemDisplayContext.GROUND,
                    LightTexture.FULL_BRIGHT,
                    packedOverlay,
                    poseStack,
                    buffer,
                    machine.getLevel(),
                    0
            );
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull PressureChamber machine) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(PressureChamber machine) {
        return new AABB(machine.getPos()).inflate(4.0D);
    }

    public static void init() {}
}