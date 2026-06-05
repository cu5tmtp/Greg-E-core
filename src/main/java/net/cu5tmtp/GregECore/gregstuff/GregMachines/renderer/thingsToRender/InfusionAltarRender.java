package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.InfusionAltar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class InfusionAltarRender extends DynamicRender<InfusionAltar, InfusionAltarRender> {

    public static final Codec<InfusionAltarRender> CODEC = Codec.unit(new InfusionAltarRender());
    public static final DynamicRenderType<InfusionAltar, InfusionAltarRender> TYPE = new DynamicRenderType<>(InfusionAltarRender.CODEC);

    private static TextureAtlasSprite RUNIC_MATRIX_SPRITE;
    private static TextureAtlasSprite GLOW_SPRITE;
    private static TextureAtlasSprite CLAW_SPRITE;
    private static final Map<BlockPos, Float> SMOOTH_PROGRESS = new HashMap<>();
    private final Random rand = new Random();

    private static final float[][] ITEM_OFFSETS = {
            {1, 1}, {1, 0}, {1, -1},
            {7, 1}, {7, 0}, {7, -1},
            {3, -3}, {4, -3}, {5, -3},
            {3, 3}, {4, 3}, {5, 3}
    };

    public InfusionAltarRender() {}

    @Override
    public DynamicRenderType<InfusionAltar, InfusionAltarRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(InfusionAltar machine, Vec3 cameraPos) {
        return machine.isFormed();
    }

    @Override
    public void render(InfusionAltar machine, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        if (RUNIC_MATRIX_SPRITE == null) {
            RUNIC_MATRIX_SPRITE = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(new ResourceLocation("gregecore", "block/blankrune"));
        }
        if (GLOW_SPRITE == null) {
            GLOW_SPRITE = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper()
                    .getBlockModel(Blocks.PURPLE_CONCRETE.defaultBlockState()).getParticleIcon();
        }
        if (CLAW_SPRITE == null) {
            CLAW_SPRITE = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper()
                    .getBlockModel(Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState()).getParticleIcon();
        }

        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var back = RelativeDirection.BACK.getRelative(front, upwards, flipped);

        float posX = (back.getStepX() * 4.0F) + 0.5F;
        float posY = (back.getStepY() * 4.0F) + 1.0F;
        float posZ = (back.getStepZ() * 4.0F) + 0.5F;

        float time = machine.getOffsetTimer() + partialTick;

        BlockPos pos = machine.getPos();
        float currentProgress = SMOOTH_PROGRESS.getOrDefault(pos, 0.0F);
        float visualProgress = 0.0F;
        var recipeLogic = machine.getRecipeLogic();

        if (recipeLogic.isWorking() && recipeLogic.getMaxProgress() > 0) {
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

        int magicalLight = 15728880;

        renderClawsAndPedestal(poseStack, buffer, posX, posY, posZ, magicalLight, packedOverlay);

        renderRunicMatrix(poseStack, buffer, posX, posY, posZ, time, magicalLight, packedOverlay);

        renderFixedItems(machine, poseStack, buffer, posX, posY, posZ, time, visualProgress, magicalLight, packedOverlay, machine.itemsForRenderer);

        if (recipeLogic.isWorking()) {
            renderSequentialBeams(machine, poseStack, buffer, posX, posY, posZ, time, visualProgress, machine.itemsForRenderer);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void renderClawsAndPedestal(PoseStack stack, MultiBufferSource bufferSource,
                                        float x, float y, float z, int packedLight, int packedOverlay) {
        VertexConsumer solidBuffer = bufferSource.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));

        renderBox(stack, solidBuffer, x, y + 0.15F, z, 1.0F, 0.3F, 1.0F, CLAW_SPRITE, 1F, 1F, 1F, 1F, packedLight, packedOverlay);
        renderBox(stack, solidBuffer, x, y + 0.55F, z, 0.6F, 0.5F, 0.6F, CLAW_SPRITE, 1F, 1F, 1F, 1F, packedLight, packedOverlay);
        renderBox(stack, solidBuffer, x, y + 0.9F, z, 0.9F, 0.2F, 0.9F, CLAW_SPRITE, 1F, 1F, 1F, 1F, packedLight, packedOverlay);

        for (int i = 0; i < 4; i++) {
            stack.pushPose();
            stack.translate(x, y, z);
            stack.mulPose(Axis.YP.rotationDegrees(45 + i * 90));
            stack.translate(0, 0, 2.6F);

            stack.translate(0, 0.2F, 0);
            renderBox(stack, solidBuffer, 0, 0, 0, 0.9F, 0.4F, 0.9F, CLAW_SPRITE, 1F, 1F, 1F, 1F, packedLight, packedOverlay);

            stack.translate(0, 0.2F, 0);
            stack.mulPose(Axis.XP.rotationDegrees(-20));
            stack.translate(0, 0.4F, 0);
            renderBox(stack, solidBuffer, 0, 0, 0, 0.6F, 0.8F, 0.6F, CLAW_SPRITE, 1F, 1F, 1F, 1F, packedLight, packedOverlay);

            stack.translate(0, 0.4F, 0);
            stack.mulPose(Axis.XP.rotationDegrees(-20));
            stack.translate(0, 0.35F, 0);
            renderBox(stack, solidBuffer, 0, 0, 0, 0.5F, 0.7F, 0.5F, CLAW_SPRITE, 1F, 1F, 1F, 1F, packedLight, packedOverlay);

            stack.translate(0, 0.35F, 0);
            stack.mulPose(Axis.XP.rotationDegrees(-20));
            stack.translate(0, 0.3F, 0);
            renderBox(stack, solidBuffer, 0, 0, 0, 0.4F, 0.6F, 0.4F, CLAW_SPRITE, 1F, 1F, 1F, 1F, packedLight, packedOverlay);

            stack.popPose();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void renderRunicMatrix(PoseStack stack, MultiBufferSource bufferSource,
                                   float x, float y, float z, float time,int packedLight, int packedOverlay) {
        float bobbing = Mth.sin(time * 0.05F) * 0.12F;
        float matrixY = y + 2.53F + bobbing;

        stack.pushPose();
        stack.translate(x, matrixY, z);

        stack.mulPose(Axis.YP.rotationDegrees(time * 1.2F));
        stack.mulPose(Axis.XP.rotationDegrees(35.0F));
        stack.mulPose(Axis.ZP.rotationDegrees(25.0F));

        VertexConsumer glowBuffer = bufferSource.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS));
        float corePulse = 0.35F + Mth.sin(time * 0.1F) * 0.05F;
        renderBox(stack, glowBuffer, 0, 0, 0, corePulse, corePulse, corePulse, GLOW_SPRITE, 0.75F, 0.0F, 1.0F, 0.9F, packedLight, packedOverlay);

        float d = 0.22F;
        float size = 0.38F;

        VertexConsumer solidBuffer = bufferSource.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));

        renderBox(stack, solidBuffer, -d, -d, -d, size, size, size, RUNIC_MATRIX_SPRITE, 1F, 1F, 1F, 1F, packedLight, packedOverlay);
        renderBox(stack, solidBuffer, d, -d, -d, size, size, size, RUNIC_MATRIX_SPRITE, 1F, 1F, 1F, 1F, packedLight, packedOverlay);
        renderBox(stack, solidBuffer, -d, d, -d, size, size, size, RUNIC_MATRIX_SPRITE, 1F, 1F, 1F, 1F, packedLight, packedOverlay);
        renderBox(stack, solidBuffer, d, d, -d, size, size, size, RUNIC_MATRIX_SPRITE, 1F, 1F, 1F, 1F, packedLight, packedOverlay);
        renderBox(stack, solidBuffer, -d, -d, d, size, size, size, RUNIC_MATRIX_SPRITE, 1F, 1F, 1F, 1F, packedLight, packedOverlay);
        renderBox(stack, solidBuffer, d, -d, d, size, size, size, RUNIC_MATRIX_SPRITE, 1F, 1F, 1F, 1F, packedLight, packedOverlay);
        renderBox(stack, solidBuffer, -d, d, d, size, size, size, RUNIC_MATRIX_SPRITE, 1F, 1F, 1F, 1F, packedLight, packedOverlay);
        renderBox(stack, solidBuffer, d, d, d, size, size, size, RUNIC_MATRIX_SPRITE, 1F, 1F, 1F, 1F, packedLight, packedOverlay);

        stack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderFixedItems(InfusionAltar machine, PoseStack stack, MultiBufferSource bufferSource,
                                  float matrixX, float matrixY, float matrixZ, float time, float visualProgress,
                                  int packedLight, int packedOverlay, List<ItemStack> items) {
        if (items == null || items.isEmpty()) return;

        int count = items.size();

        int consumedCount = 0;
        if (visualProgress > 0.0F) {
            float clampedProgress = Math.min(visualProgress, 0.5F);
            consumedCount = (int) Math.floor((clampedProgress / 0.5F) * count);
        }

        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();

        var backDir = RelativeDirection.BACK.getRelative(front, upwards, flipped);
        var leftDir = RelativeDirection.LEFT.getRelative(front, upwards, flipped);
        var upDir = RelativeDirection.UP.getRelative(front, upwards, flipped);

        for (int i = 0; i < count; i++) {
            if (i < consumedCount && visualProgress > 0.0F) continue;

            ItemStack itemStack = items.get(i);
            if (itemStack.isEmpty()) continue;

            float offsetBack = ITEM_OFFSETS[i % 12][0];
            float offsetLeft = ITEM_OFFSETS[i % 12][1];

            float bobbing = Mth.sin(time * 0.08F + i) * 0.08F;
            float offsetUp = 1.5F + bobbing;

            float itemX = 0.5F + (backDir.getStepX() * offsetBack) + (leftDir.getStepX() * offsetLeft) + (upDir.getStepX() * offsetUp);
            float itemY = 0.5F + (backDir.getStepY() * offsetBack) + (leftDir.getStepY() * offsetLeft) + (upDir.getStepY() * offsetUp);
            float itemZ = 0.5F + (backDir.getStepZ() * offsetBack) + (leftDir.getStepZ() * offsetLeft) + (upDir.getStepZ() * offsetUp);

            stack.pushPose();
            stack.translate(itemX, itemY, itemZ);

            stack.mulPose(Axis.YP.rotationDegrees(time * 2.0F));
            stack.scale(0.55F, 0.55F, 0.55F);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    itemStack,
                    ItemDisplayContext.GROUND,
                    packedLight,
                    packedOverlay,
                    stack,
                    bufferSource,
                    machine.getLevel(),
                    0
            );
            stack.popPose();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void renderSequentialBeams(InfusionAltar machine, PoseStack stack, MultiBufferSource bufferSource,
                                       float matrixX, float matrixY, float matrixZ, float time, float visualProgress, List<ItemStack> items) {
        if (items == null || items.isEmpty()) return;

        if (visualProgress <= 0.0F || visualProgress >= 0.5F) return;

        int count = items.size();

        int currentIndex = (int) Math.floor((visualProgress / 0.5F) * count);

        if (currentIndex >= count) currentIndex = count - 1;

        if (!items.get(currentIndex).isEmpty()) {
            VertexConsumer beamBuffer = bufferSource.getBuffer(RenderType.lightning());

            var front = machine.getFrontFacing();
            var upwards = machine.getUpwardsFacing();
            var flipped = machine.isFlipped();

            var backDir = RelativeDirection.BACK.getRelative(front, upwards, flipped);
            var leftDir = RelativeDirection.LEFT.getRelative(front, upwards, flipped);
            var upDir = RelativeDirection.UP.getRelative(front, upwards, flipped);

            float offsetBack = ITEM_OFFSETS[currentIndex % 12][0];
            float offsetLeft = ITEM_OFFSETS[currentIndex % 12][1];
            float bobbing = Mth.sin(time * 0.08F + currentIndex) * 0.08F;
            float offsetUp = 1.5F + bobbing;

            float startX = 0.5F + (backDir.getStepX() * offsetBack) + (leftDir.getStepX() * offsetLeft) + (upDir.getStepX() * offsetUp);
            float startY = 0.5F + (backDir.getStepY() * offsetBack) + (leftDir.getStepY() * offsetLeft) + (upDir.getStepY() * offsetUp);
            float startZ = 0.5F + (backDir.getStepZ() * offsetBack) + (leftDir.getStepZ() * offsetLeft) + (upDir.getStepZ() * offsetUp);

            float targetY = matrixY + 2.53F + (Mth.sin(time * 0.05F) * 0.12F);

            drawLightningBeam(stack, beamBuffer, startX, startY, startZ, matrixX, targetY, matrixZ, time, currentIndex);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void drawLightningBeam(PoseStack stack, VertexConsumer buffer,
                                   float sx, float sy, float sz,
                                   float ex, float ey, float ez, float time, int index) {
        Matrix4f mat = stack.last().pose();

        int segments = 6;
        float curX = sx;
        float curY = sy;
        float curZ = sz;

        rand.setSeed((long) (index * 1000 + (time * 0.4F)));

        for (int i = 1; i <= segments; i++) {
            float pct = (float) i / segments;

            float targetX = sx + (ex - sx) * pct;
            float targetY = sy + (ey - sy) * pct;
            float targetZ = sz + (ez - sz) * pct;

            if (i < segments) {
                targetX += (rand.nextFloat() - 0.5F) * 0.15F;
                targetY += (rand.nextFloat() - 0.5F) * 0.15F;
                targetZ += (rand.nextFloat() - 0.5F) * 0.15F;
            }

            float thickness = 0.03F;
            buffer.vertex(mat, curX, curY, curZ).color(0.8F, 0.1F, 1.0F, 0.8F).uv(0, 0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
            buffer.vertex(mat, targetX, targetY, targetZ).color(0.8F, 0.1F, 1.0F, 0.8F).uv(1, 1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();

            buffer.vertex(mat, curX, curY + thickness, curZ).color(0.9F, 0.3F, 1.0F, 0.8F).uv(0, 0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(1, 0, 0).endVertex();
            buffer.vertex(mat, targetX, targetY + thickness, targetZ).color(0.9F, 0.3F, 1.0F, 0.8F).uv(1, 1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(1, 0, 0).endVertex();

            curX = targetX;
            curY = targetY;
            curZ = targetZ;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void renderBox(PoseStack poseStack, VertexConsumer buffer,
                           float x, float y, float z, float width, float height, float depth,
                           TextureAtlasSprite sprite, float r, float g, float b, float a,
                           int light, int overlay) {
        Matrix4f mat = poseStack.last().pose();
        Matrix3f normalMat = poseStack.last().normal();

        float hw = width / 2.0F;
        float hh = height / 2.0F;
        float hd = depth / 2.0F;

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        addVertex(buffer, mat, normalMat, x - hw, y - hh, z - hd, u0, v0, r, g, b, a, light, overlay, 0, -1, 0);
        addVertex(buffer, mat, normalMat, x + hw, y - hh, z - hd, u1, v0, r, g, b, a, light, overlay, 0, -1, 0);
        addVertex(buffer, mat, normalMat, x + hw, y - hh, z + hd, u1, v1, r, g, b, a, light, overlay, 0, -1, 0);
        addVertex(buffer, mat, normalMat, x - hw, y - hh, z + hd, u0, v1, r, g, b, a, light, overlay, 0, -1, 0);

        addVertex(buffer, mat, normalMat, x - hw, y + hh, z - hd, u0, v0, r, g, b, a, light, overlay, 0, 1, 0);
        addVertex(buffer, mat, normalMat, x - hw, y + hh, z + hd, u0, v1, r, g, b, a, light, overlay, 0, 1, 0);
        addVertex(buffer, mat, normalMat, x + hw, y + hh, z + hd, u1, v1, r, g, b, a, light, overlay, 0, 1, 0);
        addVertex(buffer, mat, normalMat, x + hw, y + hh, z - hd, u1, v0, r, g, b, a, light, overlay, 0, 1, 0);

        addVertex(buffer, mat, normalMat, x - hw, y - hh, z - hd, u0, v0, r, g, b, a, light, overlay, 0, 0, -1);
        addVertex(buffer, mat, normalMat, x - hw, y + hh, z - hd, u0, v1, r, g, b, a, light, overlay, 0, 0, -1);
        addVertex(buffer, mat, normalMat, x + hw, y + hh, z - hd, u1, v1, r, g, b, a, light, overlay, 0, 0, -1);
        addVertex(buffer, mat, normalMat, x + hw, y - hh, z - hd, u1, v0, r, g, b, a, light, overlay, 0, 0, -1);

        addVertex(buffer, mat, normalMat, x - hw, y - hh, z + hd, u0, v0, r, g, b, a, light, overlay, 0, 0, 1);
        addVertex(buffer, mat, normalMat, x + hw, y - hh, z + hd, u1, v0, r, g, b, a, light, overlay, 0, 0, 1);
        addVertex(buffer, mat, normalMat, x + hw, y + hh, z + hd, u1, v1, r, g, b, a, light, overlay, 0, 0, 1);
        addVertex(buffer, mat, normalMat, x - hw, y + hh, z + hd, u0, v1, r, g, b, a, light, overlay, 0, 0, 1);

        addVertex(buffer, mat, normalMat, x - hw, y - hh, z - hd, u0, v0, r, g, b, a, light, overlay, -1, 0, 0);
        addVertex(buffer, mat, normalMat, x - hw, y - hh, z + hd, u1, v0, r, g, b, a, light, overlay, -1, 0, 0);
        addVertex(buffer, mat, normalMat, x - hw, y + hh, z + hd, u1, v1, r, g, b, a, light, overlay, -1, 0, 0);
        addVertex(buffer, mat, normalMat, x - hw, y + hh, z - hd, u0, v1, r, g, b, a, light, overlay, -1, 0, 0);

        addVertex(buffer, mat, normalMat, x + hw, y - hh, z - hd, u0, v0, r, g, b, a, light, overlay, 1, 0, 0);
        addVertex(buffer, mat, normalMat, x + hw, y + hh, z - hd, u0, v1, r, g, b, a, light, overlay, 1, 0, 0);
        addVertex(buffer, mat, normalMat, x + hw, y + hh, z + hd, u1, v1, r, g, b, a, light, overlay, 1, 0, 0);
        addVertex(buffer, mat, normalMat, x + hw, y - hh, z + hd, u1, v0, r, g, b, a, light, overlay, 1, 0, 0);
    }

    @OnlyIn(Dist.CLIENT)
    private void addVertex(VertexConsumer buffer, Matrix4f mat, Matrix3f normalMat,
                           float x, float y, float z, float u, float v,
                           float r, float g, float b, float a, int light, int overlay,
                           float nx, float ny, float nz) {
        buffer.vertex(mat, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normalMat, nx, ny, nz)
                .endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull InfusionAltar machine) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(InfusionAltar machine) {
        return new AABB(machine.getPos()).inflate(12.0D);
    }

    public static void init() {}
}