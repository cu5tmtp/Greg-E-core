package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.misc.InfusionAltar;
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
import org.joml.Vector3f;
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
    private static TextureAtlasSprite WHITE_SPRITE;

    private static final Map<BlockPos, Float> SMOOTH_PROGRESS = new HashMap<>();
    private static final Map<BlockPos, Float> ROTATION_ANGLE = new HashMap<>();
    private static final Map<BlockPos, Float> LAST_RENDER_TIME = new HashMap<>();
    private static final Map<BlockPos, Float> SMOOTH_SPEED = new HashMap<>();

    private final Random rand = new Random();

    private static final float[][] ITEM_OFFSETS = {
            {1, 1}, {1, 0}, {1, -1},
            {7, 1}, {7, 0}, {7, -1},
            {3, -3}, {4, -3}, {5, -3},
            {3, 3}, {4, 3}, {5, 3}
    };

    private static final float[][] ESSENTIA_OFFSETS = {
            {3, -4, 0.0F, 0.0F, 1.0F},
            {4, -4, 0.0F, 1.0F, 0.0F},
            {5, -4, 1.0F, 1.0F, 0.0F},
            {3, 4, 1.0F, 0.0F, 0.0F},
            {4, 4, 0.25F, 0.25F, 0.25F},
            {5, 4, 0.5F, 0.5F, 0.5F}
    };

    public InfusionAltarRender() {}

    @Override
    public DynamicRenderType<InfusionAltar, InfusionAltarRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(InfusionAltar machine, Vec3 cameraPos) {
        boolean formed = machine.isFormed();
        if (!formed) {
            BlockPos pos = machine.getPos();
            SMOOTH_PROGRESS.remove(pos);
            ROTATION_ANGLE.remove(pos);
            LAST_RENDER_TIME.remove(pos);
            SMOOTH_SPEED.remove(pos);
        }
        return formed;
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
        if (WHITE_SPRITE == null) {
            WHITE_SPRITE = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper()
                    .getBlockModel(Blocks.WHITE_CONCRETE.defaultBlockState()).getParticleIcon();
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

        var recipeLogic = machine.getRecipeLogic();
        boolean isWorking = recipeLogic.isWorking();

        float lastTime = LAST_RENDER_TIME.getOrDefault(pos, time);
        float deltaTime = time - lastTime;
        if (deltaTime < 0.0F || deltaTime > 5.0F) {
            deltaTime = 0.0F;
        }
        LAST_RENDER_TIME.put(pos, time);

        float targetSpeed = isWorking ? 4.0F : 1.0F;
        float currentSpeed = SMOOTH_SPEED.getOrDefault(pos, 1.0F);

        currentSpeed = currentSpeed + (targetSpeed - currentSpeed) * 0.08F;
        SMOOTH_SPEED.put(pos, currentSpeed);

        float currentAngle = ROTATION_ANGLE.getOrDefault(pos, 0.0F);
        currentAngle += deltaTime * currentSpeed * 1.2F;
        currentAngle %= 360.0F;
        ROTATION_ANGLE.put(pos, currentAngle);

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

        int magicalLight = 15728880;

        renderClawsAndPedestal(poseStack, buffer, posX, posY, posZ, magicalLight, packedOverlay);

        renderRunicMatrix(poseStack, buffer, posX, posY, posZ, time, currentAngle, magicalLight, packedOverlay);

        renderFixedItems(machine, poseStack, buffer, posX, posY, posZ, time, visualProgress, magicalLight, packedOverlay, machine.itemsForRenderer);

        if (isWorking) {
            renderSequentialBeams(machine, poseStack, buffer, posX, posY, posZ, time, visualProgress, machine.itemsForRenderer);

            if (visualProgress > 0.5F) {
                renderEssentiaArcs(machine, poseStack, buffer, posX, posY, posZ, time, visualProgress, magicalLight, packedOverlay);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void renderEssentiaArcs(InfusionAltar machine, PoseStack stack, MultiBufferSource bufferSource,
                                    float matrixX, float matrixY, float matrixZ, float time, float visualProgress,
                                    int packedLight, int packedOverlay) {
        if (machine.essentiaToRender == null) return;

        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();

        var backDir = RelativeDirection.BACK.getRelative(front, upwards, flipped);
        var leftDir = RelativeDirection.LEFT.getRelative(front, upwards, flipped);
        var upDir = RelativeDirection.UP.getRelative(front, upwards, flipped);

        VertexConsumer glowBuffer = bufferSource.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS));

        float targetY = matrixY + 2.53F + (Mth.sin(time * 0.05F) * 0.12F);

        float phaseAlpha = Mth.clamp((visualProgress - 0.5F) * 4.0F, 0.0F, 1.0F);

        for (int i = 0; i < 6; i++) {
            if (i >= machine.essentiaToRender.length) break;
            if (!machine.essentiaToRender[i]) continue;

            float offsetBack = ESSENTIA_OFFSETS[i][0];
            float offsetLeft = ESSENTIA_OFFSETS[i][1];

            float r = ESSENTIA_OFFSETS[i][2];
            float g = ESSENTIA_OFFSETS[i][3];
            float b = ESSENTIA_OFFSETS[i][4];

            float offsetUp = 1.0F;

            float startX = 0.5F + (backDir.getStepX() * offsetBack) + (leftDir.getStepX() * offsetLeft) + (upDir.getStepX() * offsetUp);
            float startY = (backDir.getStepY() * offsetBack) + (leftDir.getStepY() * offsetLeft) + (upDir.getStepY() * offsetUp);
            float startZ = 0.5F +(backDir.getStepZ() * offsetBack) + (leftDir.getStepZ() * offsetLeft) + (upDir.getStepZ() * offsetUp);

            int particlesCount = 10;
            for (int p = 0; p < particlesCount; p++) {
                float t = (time * 0.035F + (float) p / particlesCount) % 1.0F;

                float easeT = t * t;

                float px = Mth.lerp(easeT, startX, matrixX);
                float pz = Mth.lerp(easeT, startZ, matrixZ);
                float py = Mth.lerp(easeT, startY, targetY) + Mth.sin(easeT * (float)Math.PI) * 1.5F;

                stack.pushPose();
                stack.translate(px, py, pz);

                stack.mulPose(Axis.YP.rotationDegrees(time * 5F + p * 45F));
                stack.mulPose(Axis.XP.rotationDegrees(time * 3F + p * 30F));

                float baseScale = Mth.sin(t * (float)Math.PI) * 0.12F;
                float scale = baseScale * phaseAlpha;

                if (scale > 0.01F) {
                    renderBox(stack, glowBuffer, 0, 0, 0, scale, scale, scale, WHITE_SPRITE, r, g, b, 0.8F * phaseAlpha, packedLight, packedOverlay);
                }
                stack.popPose();
            }
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
                                   float x, float y, float z, float time, float customAngle, int packedLight, int packedOverlay) {
        float bobbing = Mth.sin(time * 0.05F) * 0.12F;
        float matrixY = y + 2.53F + bobbing;

        stack.pushPose();
        stack.translate(x, matrixY, z);

        stack.mulPose(Axis.YP.rotationDegrees(customAngle));
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
            stack.scale(1.2F, 1.2F, 1.2F);

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

        ItemStack outputItem = machine.outputItem;
        if (!outputItem.isEmpty()) {
            float outputBack = 4.0F;
            float outputUp = 1.5F;

            float outX = 0.5F + (backDir.getStepX() * outputBack) + (upDir.getStepX() * outputUp);
            float outY = 0.75F + (backDir.getStepY() * outputBack) + (upDir.getStepY() * outputUp);
            float outZ = 0.5F + (backDir.getStepZ() * outputBack) + (upDir.getStepZ() * outputUp);

            stack.pushPose();
            stack.translate(outX, outY, outZ);

            stack.mulPose(Axis.YP.rotationDegrees(time * 1.5F));
            stack.scale(1.5F, 1.5F, 1.5F);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    outputItem,
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
            float startY = 0.75F + (backDir.getStepY() * offsetBack) + (leftDir.getStepY() * offsetLeft) + (upDir.getStepY() * offsetUp);
            float startZ = 0.5F + (backDir.getStepZ() * offsetBack) + (leftDir.getStepZ() * offsetLeft) + (upDir.getStepZ() * offsetUp);

            float targetY = matrixY + 2.53F + (Mth.sin(time * 0.05F) * 0.12F);

            Vec3 start = new Vec3(startX, startY, startZ);
            Vec3 end = new Vec3(matrixX, targetY, matrixZ);

            renderPrismLightning(stack, beamBuffer, start, end, time, currentIndex, 0.8F, 0.1F, 1.0F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void renderPrismLightning(PoseStack poseStack, VertexConsumer buffer, Vec3 start, Vec3 end, float time, int index, float r, float g, float b) {
        Matrix4f matrix = poseStack.last().pose();
        Vec3 current = start;
        int segments = 8;

        Vec3 diff = end.subtract(start);
        double length = diff.length();
        Vec3 direction = diff.normalize();
        float segmentLength = (float) (length / segments);

        float deviation = 0.15f;
        float thickness = 0.035f;

        rand.setSeed((long) (index * 1000 + (time * 0.4F)));

        for (int i = 0; i < segments; i++) {
            Vec3 nextBase = start.add(direction.scale((i + 1) * segmentLength));
            Vec3 next;
            if (i < segments - 1) {
                next = nextBase.add(
                        (rand.nextFloat() - 0.5f) * deviation,
                        (rand.nextFloat() - 0.5f) * deviation,
                        (rand.nextFloat() - 0.5f) * deviation
                );
            } else {
                next = nextBase;
            }

            drawLightningPrism(buffer, matrix, current, next, thickness, r, g, b, 0.8f);
            current = next;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void drawLightningPrism(VertexConsumer buffer, Matrix4f matrix, Vec3 start, Vec3 end, float thickness, float r, float g, float b, float a) {
        Vector3f dir = new Vector3f((float)(end.x - start.x), (float)(end.y - start.y), (float)(end.z - start.z));

        Vector3f up = new Vector3f(0, 1, 0);
        up.cross(dir);
        if (up.lengthSquared() < 0.001f) up.set(1, 0, 0);
        up.normalize().mul(thickness);

        Vector3f side = new Vector3f(up);
        side.cross(dir);
        side.normalize().mul(thickness);

        Vector3f s1 = new Vector3f((float)start.x, (float)start.y, (float)start.z).add(up).add(side);
        Vector3f s2 = new Vector3f((float)start.x, (float)start.y, (float)start.z).add(up).sub(side);
        Vector3f s3 = new Vector3f((float)start.x, (float)start.y, (float)start.z).sub(up).sub(side);
        Vector3f s4 = new Vector3f((float)start.x, (float)start.y, (float)start.z).sub(up).add(side);

        Vector3f e1 = new Vector3f((float)end.x, (float)end.y, (float)end.z).add(up).add(side);
        Vector3f e2 = new Vector3f((float)end.x, (float)end.y, (float)end.z).add(up).sub(side);
        Vector3f e3 = new Vector3f((float)end.x, (float)end.y, (float)end.z).sub(up).sub(side);
        Vector3f e4 = new Vector3f((float)end.x, (float)end.y, (float)end.z).sub(up).add(side);

        addFace(buffer, matrix, s1, s2, e2, e1, r, g, b, a);
        addFace(buffer, matrix, s2, s3, e3, e2, r, g, b, a);
        addFace(buffer, matrix, s3, s4, e4, e3, r, g, b, a);
        addFace(buffer, matrix, s4, s1, e1, e4, r, g, b, a);
    }

    @OnlyIn(Dist.CLIENT)
    private void addFace(VertexConsumer buffer, Matrix4f matrix, Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4, float r, float g, float b, float a) {
        addVertexWithData(buffer, matrix, v1.x, v1.y, v1.z, 0, 0, r, g, b, a);
        addVertexWithData(buffer, matrix, v2.x, v2.y, v2.z, 1, 0, r, g, b, a);
        addVertexWithData(buffer, matrix, v3.x, v3.y, v3.z, 1, 1, r, g, b, a);
        addVertexWithData(buffer, matrix, v4.x, v4.y, v4.z, 0, 1, r, g, b, a);
    }

    @OnlyIn(Dist.CLIENT)
    private void addVertexWithData(VertexConsumer buffer, Matrix4f matrix, float x, float y, float z, float u, float v, float r, float g, float b, float a) {
        buffer.vertex(matrix, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(0, 1, 0)
                .endVertex();
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