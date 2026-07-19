package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.misc.BloodCathedral;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@SuppressWarnings("all")
public class BloodCathedralRender extends DynamicRender<BloodCathedral, BloodCathedralRender> {

    public static final Codec<BloodCathedralRender> CODEC = Codec.unit(new BloodCathedralRender());
    public static final DynamicRenderType<BloodCathedral, BloodCathedralRender> TYPE = new DynamicRenderType<>(BloodCathedralRender.CODEC);
    protected float delta = 0;

    private static final ResourceLocation BLOODBALL_TEXTURE = new ResourceLocation("gregecore", "textures/block/bloodball.png");
    private static final ResourceLocation BLOODFLOW_TEXTURE = new ResourceLocation("gregecore", "textures/block/bloodflow.png");

    public BloodCathedralRender() {}

    @Override
    public DynamicRenderType<BloodCathedral, BloodCathedralRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(BloodCathedral machine, Vec3 cameraPos) {
        return machine.isFormed();
    }

    @Override
    public void render(BloodCathedral machine, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        VertexConsumer ballBuffer = buffer.getBuffer(RenderType.entityCutout(BLOODBALL_TEXTURE));
        VertexConsumer flowBuffer = buffer.getBuffer(RenderType.entityCutout(BLOODFLOW_TEXTURE));

        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var back = RelativeDirection.BACK.getRelative(front, upwards, flipped);
        var up = RelativeDirection.UP.getRelative(front, upwards, flipped);

        float posX = (back.getStepX() * 3.0F) + (up.getStepX() * 11.0F) + 0.5F;
        float posY = (back.getStepY() * 3.0F) + (up.getStepY() * 11.0F) + 0.5F;
        float posZ = (back.getStepZ() * 3.0F) + (up.getStepZ() * 11.0F) + 0.5F;

        int bloodAmount = machine.getBlood();
        float baseRadius = 0.96F;
        float radius = baseRadius + (bloodAmount * 0.0001f);

        renderBloodSphere(machine, partialTick, poseStack, ballBuffer, posX, posY, posZ, radius);
        renderBloodStreams(machine, partialTick, poseStack, flowBuffer, posX, posY, posZ, radius, packedLight);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderBloodSphere(BloodCathedral machine, float partialTicks, PoseStack stack,
                                   VertexConsumer buffer, float x, float y, float z, float radius) {
        var alpha = 1f;
        int fullBright = 15728880;
        float time = (machine.getOffsetTimer() + partialTicks);

        stack.pushPose();
        stack.translate(x, y, z);
        stack.mulPose(Axis.YP.rotationDegrees(time * 0.5f));
        stack.mulPose(Axis.XP.rotationDegrees(time * 0.25f));

        renderTexturedSphere(stack, buffer, 0, 0, 0, radius, 24, 24, 1.0f, 1.0f, 1.0f, alpha, fullBright);

        stack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderBloodStreams(BloodCathedral machine, float partialTicks, PoseStack stack,
                                    VertexConsumer buffer, float x, float y, float z, float radius, int packedLight) {
        float time = (machine.getOffsetTimer() + partialTicks) * 0.05f;
        int numStreams = 3;
        int segments = 8;

        int fullBright = 15728880;

        for (int i = 0; i < numStreams; i++) {
            float angle = time * 0.8f + (i * (float) Math.PI * 2 / numStreams);

            float p0x = x + Mth.cos(angle) * 0.5f;
            float p0y = y - 10.0f;
            float p0z = z + Mth.sin(angle) * 0.5f;

            float p2x = x;
            float p2y = y - radius * 0.4f;
            float p2z = z;

            float bulge = 4.5f + Mth.sin(time * 2 + i) * 1.5f;
            float p1x = x + Mth.cos(angle) * bulge;
            float p1y = y - 5.0f;
            float p1z = z + Mth.sin(angle) * bulge;

            float prevX = p0x, prevY = p0y, prevZ = p0z;

            for (int j = 1; j <= segments; j++) {
                float t = (float) j / segments;
                float invT = 1.0f - t;

                float currX = invT * invT * p0x + 2 * invT * t * p1x + t * t * p2x;
                float currY = invT * invT * p0y + 2 * invT * t * p1y + t * t * p2y;
                float currZ = invT * invT * p0z + 2 * invT * t * p1z + t * t * p2z;

                float width = 0.12f * (1.0f - t * 0.4f) + 0.03f * Mth.sin(time * 15 - t * 10);

                renderBeam(stack, buffer, prevX, prevY, prevZ, currX, currY, currZ, width, fullBright);

                prevX = currX;
                prevY = currY;
                prevZ = currZ;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void renderBeam(PoseStack poseStack, VertexConsumer buffer,
                            float sx, float sy, float sz, float ex, float ey, float ez,
                            float width, int light) {
        float dx = ex - sx;
        float dy = ey - sy;
        float dz = ez - sz;
        float dist = Mth.sqrt(dx * dx + dy * dy + dz * dz);

        poseStack.pushPose();
        poseStack.translate(sx, sy, sz);

        float yaw = (float) (Math.atan2(dx, dz));
        float pitch = (float) (Math.atan2(dy, Mth.sqrt(dx * dx + dz * dz)));

        poseStack.mulPose(Axis.YP.rotation(yaw));
        poseStack.mulPose(Axis.XP.rotation(-pitch));

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        float u0 = 0.0f;
        float u1 = 1.0f;
        float v0 = 0.0f;
        float v1 = 1.0f;

        float topColor = 1.0f;
        float sideColor = 0.75f;
        float bottomColor = 0.5f;

        float zStart = -width * 0.75f;
        float zEnd = dist + width * 0.75f;

        buffer.vertex(pose, -width, width, zStart).color(topColor, topColor, topColor, 1.0f).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();
        buffer.vertex(pose, -width, width, zEnd).color(topColor, topColor, topColor, 1.0f).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();
        buffer.vertex(pose, width, width, zEnd).color(topColor, topColor, topColor, 1.0f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();
        buffer.vertex(pose, width, width, zStart).color(topColor, topColor, topColor, 1.0f).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();

        buffer.vertex(pose, width, -width, zStart).color(bottomColor, bottomColor, bottomColor, 1.0f).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, -1, 0).endVertex();
        buffer.vertex(pose, width, -width, zEnd).color(bottomColor, bottomColor, bottomColor, 1.0f).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, -1, 0).endVertex();
        buffer.vertex(pose, -width, -width, zEnd).color(bottomColor, bottomColor, bottomColor, 1.0f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, -1, 0).endVertex();
        buffer.vertex(pose, -width, -width, zStart).color(bottomColor, bottomColor, bottomColor, 1.0f).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, -1, 0).endVertex();

        buffer.vertex(pose, -width, -width, zStart).color(sideColor, sideColor, sideColor, 1.0f).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, -1, 0, 0).endVertex();
        buffer.vertex(pose, -width, -width, zEnd).color(sideColor, sideColor, sideColor, 1.0f).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, -1, 0, 0).endVertex();
        buffer.vertex(pose, -width, width, zEnd).color(sideColor, sideColor, sideColor, 1.0f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, -1, 0, 0).endVertex();
        buffer.vertex(pose, -width, width, zStart).color(sideColor, sideColor, sideColor, 1.0f).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, -1, 0, 0).endVertex();

        buffer.vertex(pose, width, width, zStart).color(sideColor, sideColor, sideColor, 1.0f).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 1, 0, 0).endVertex();
        buffer.vertex(pose, width, width, zEnd).color(sideColor, sideColor, sideColor, 1.0f).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 1, 0, 0).endVertex();
        buffer.vertex(pose, width, -width, zEnd).color(sideColor, sideColor, sideColor, 1.0f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 1, 0, 0).endVertex();
        buffer.vertex(pose, width, -width, zStart).color(sideColor, sideColor, sideColor, 1.0f).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 1, 0, 0).endVertex();

        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderTexturedSphere(PoseStack poseStack, VertexConsumer buffer, float x, float y, float z,
                                      float radius, int longitudes, int latitudes,
                                      float red, float green, float blue, float alpha,
                                      int light) {
        Matrix4f mat = poseStack.last().pose();
        Matrix3f normalMat = poseStack.last().normal();

        for (int i = 0; i < latitudes; i++) {
            float fLat0 = (float) i / latitudes;
            float fLat1 = (float) (i + 1) / latitudes;
            float angleLat0 = (float) Math.PI * (-0.5f + fLat0);
            float angleLat1 = (float) Math.PI * (-0.5f + fLat1);

            float sinLat0 = Mth.sin(angleLat0);
            float cosLat0 = Mth.cos(angleLat0);
            float sinLat1 = Mth.sin(angleLat1);
            float cosLat1 = Mth.cos(angleLat1);

            float v0 = fLat0;
            float v1 = fLat1;

            for (int j = 0; j < longitudes; j++) {
                float fLng0 = (float) j / longitudes;
                float fLng1 = (float) (j + 1) / longitudes;
                float lng0 = (float) (2.0 * Math.PI * fLng0);
                float lng1 = (float) (2.0 * Math.PI * fLng1);

                float u0 = fLng0;
                float u1 = fLng1;

                addVertex(buffer, mat, normalMat, x, y, z, radius, lng0, sinLat0, cosLat0, u0, v0, red, green, blue, alpha, light);
                addVertex(buffer, mat, normalMat, x, y, z, radius, lng0, sinLat1, cosLat1, u0, v1, red, green, blue, alpha, light);
                addVertex(buffer, mat, normalMat, x, y, z, radius, lng1, sinLat1, cosLat1, u1, v1, red, green, blue, alpha, light);
                addVertex(buffer, mat, normalMat, x, y, z, radius, lng1, sinLat0, cosLat0, u1, v0, red, green, blue, alpha, light);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void addVertex(VertexConsumer buffer, Matrix4f mat, Matrix3f normalMat,
                           float x, float y, float z, float radius,
                           float lng, float sinLat, float cosLat,
                           float u, float v, float r, float g, float b, float a, int light) {
        float nx = Mth.cos(lng) * cosLat;
        float ny = sinLat;
        float nz = Mth.sin(lng) * cosLat;

        buffer.vertex(mat, x + nx * radius, y + ny * radius, z + nz * radius)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normalMat, nx, ny, nz)
                .endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(BloodCathedral machine) {
        return machine.getRecipeLogic().isWorking() || delta > 0;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }

    @Override
    public AABB getRenderBoundingBox(BloodCathedral machine) {
        return new AABB(machine.getPos()).inflate(64.0D);
    }

    public static void init(){}
}