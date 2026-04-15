package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.GTRenderTypes;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.client.util.RenderBufferHelper;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.DysonSwarmEnergyCollector;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;

public class DysonSwarmEnergyCollectorRender extends DynamicRender<DysonSwarmEnergyCollector, DysonSwarmEnergyCollectorRender> {

    public static final Codec<DysonSwarmEnergyCollectorRender> CODEC = Codec.unit(DysonSwarmEnergyCollectorRender::new);
    public static final DynamicRenderType<DysonSwarmEnergyCollector, DysonSwarmEnergyCollectorRender> TYPE = new DynamicRenderType<>(DysonSwarmEnergyCollectorRender.CODEC);

    private static final float TARGET_R = 0.7f;
    private static final float TARGET_G = 0.65f;
    private static final float TARGET_B = 0.2f;

    private static final float SPEED = 0.15f;
    private static final float MAX_DISTANCE = 10.0f;
    private static final int RING_COUNT = 4;
    private static final float FADE_ZONE = 1.5f;

    private static final float MIN_RADIUS = 0.5f;
    private static final float MAX_RADIUS = 4.5f;
    private static final float RING_THICKNESS = 0.2f;

    public DysonSwarmEnergyCollectorRender() {}

    @Override
    public DynamicRenderType<DysonSwarmEnergyCollector, DysonSwarmEnergyCollectorRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(DysonSwarmEnergyCollector machine, Vec3 cameraPos) {
        return machine.getRecipeLogic().isWorking();
    }

    @Override
    public void render(DysonSwarmEnergyCollector machine, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        if (!machine.getRecipeLogic().isWorking()) {
            return;
        }

        renderEnergyRings(machine, partialTick, poseStack, buffer);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderEnergyRings(DysonSwarmEnergyCollector machine, float partialTicks, PoseStack stack,
                                   MultiBufferSource bufferSource) {

        float baseAlpha = 1.0f;

        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();

        var up = RelativeDirection.UP.getRelative(front, upwards, flipped);
        var left = RelativeDirection.LEFT.getRelative(front, upwards, flipped);
        var back = RelativeDirection.BACK.getRelative(front, upwards, flipped);

        var axis = up.getAxis();

        float time = machine.getOffsetTimer() + partialTicks;

        for (int i = 0; i < RING_COUNT; i++) {
            float currentDist = (time * SPEED + i * (MAX_DISTANCE / RING_COUNT)) % MAX_DISTANCE;

            float ringAlpha = baseAlpha;
            if (currentDist < FADE_ZONE) {
                ringAlpha *= (currentDist / FADE_ZONE);
            } else if (currentDist > MAX_DISTANCE - FADE_ZONE) {
                ringAlpha *= ((MAX_DISTANCE - currentDist) / FADE_ZONE);
            }

            if (ringAlpha <= 0.01f) continue;

            float progress = currentDist / MAX_DISTANCE;

            float currentRadius = MIN_RADIUS + (MAX_RADIUS - MIN_RADIUS) * progress;

            float r = 1.0f + (TARGET_R - 1.0f) * progress;
            float g = 1.0f + (TARGET_G - 1.0f) * progress;
            float b = 1.0f + (TARGET_B - 1.0f) * progress;

            float posX = 0.5F + (up.getStepX() * (16.0F - currentDist)) + (back.getStepX() * 3.0F) + (left.getStepX() * 3.0F);
            float posY = 0.5F + (up.getStepY() * (16.0F - currentDist)) + (back.getStepY() * 3.0F) + (left.getStepY() * 3.0F);
            float posZ = 0.5F + (up.getStepZ() * (16.0F - currentDist)) + (back.getStepZ() * 3.0F) + (left.getStepZ() * 3.0F);

            stack.pushPose();
            stack.translate(posX, posY, posZ);

            VertexConsumer ringBuffer = bufferSource.getBuffer(GTRenderTypes.getLightRing());

            RenderBufferHelper.renderRing(stack, ringBuffer,
                    0.0f, 0.0f, 0.0f,
                    currentRadius, RING_THICKNESS, 10, 20,
                    r, g, b, ringAlpha, axis);

            stack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(DysonSwarmEnergyCollector machine) {
        return machine.getRecipeLogic().isWorking();
    }

    @Override
    public int getViewDistance() {
        return 64;
    }

    @Override
    public AABB getRenderBoundingBox(DysonSwarmEnergyCollector machine) {
        return new AABB(machine.getPos()).inflate(32.0D);
    }

    public static void init() {}
}