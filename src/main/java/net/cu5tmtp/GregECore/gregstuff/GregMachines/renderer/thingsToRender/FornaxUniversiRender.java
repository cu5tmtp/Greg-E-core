package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.FornaxUniversi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.math.Axis;
import org.joml.Matrix4f;
import org.joml.Matrix3f;

import java.util.Random;
import java.util.function.Function;

public class FornaxUniversiRender extends DynamicRender<FornaxUniversi, FornaxUniversiRender> {

    public static final Codec<FornaxUniversiRender> CODEC = Codec.unit(new FornaxUniversiRender());
    public static final DynamicRenderType<FornaxUniversi, FornaxUniversiRender> TYPE = new DynamicRenderType<>(FornaxUniversiRender.CODEC);

    private static final ResourceLocation WHITE_TEXTURE = new ResourceLocation("minecraft:textures/block/white_concrete.png");
    private static final ResourceLocation WHITE_TEXTURE_LOC = new ResourceLocation("minecraft:block/white_concrete");

    private static final ResourceLocation MAGMA_TEXTURE_LOC = new ResourceLocation("minecraft:block/magma");
    private static final ResourceLocation STONE_TEXTURE_LOC = new ResourceLocation("minecraft:block/stone");
    private static final ResourceLocation COBBLESTONE_TEXTURE_LOC = new ResourceLocation("minecraft:block/cobblestone");
    private static final ResourceLocation LAPIS_TEXTURE_LOC = new ResourceLocation("minecraft:block/lapis_block");

    public FornaxUniversiRender() {}

    @Override
    public DynamicRenderType<FornaxUniversi, FornaxUniversiRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(FornaxUniversi machine, Vec3 cameraPos) {
        return machine.isFormed();
    }

    @Override
    public void render(FornaxUniversi machine, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var back = RelativeDirection.BACK.getRelative(front, upwards, flipped);
        var up = RelativeDirection.UP.getRelative(front, upwards, flipped);

        float posX = (back.getStepX() * 5.0F) + (up.getStepX() * 5.0F) + 0.5F;
        float posY = (back.getStepY() * 5.0F) + (up.getStepY() * 5.0F) + 0.5F;
        float posZ = (back.getStepZ() * 5.0F) + (up.getStepZ() * 5.0F) + 0.5F;

        int fullBright = 15728880;

        poseStack.pushPose();
        poseStack.translate(posX, posY, posZ);

        TextureAtlasSprite whiteSprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(WHITE_TEXTURE_LOC);

        renderEventHorizon(poseStack, buffer.getBuffer(RenderType.solid()), whiteSprite, fullBright);

        renderPlanets(machine, partialTick, poseStack, buffer.getBuffer(RenderType.solid()), fullBright);

        renderAccretionDisk(machine, partialTick, poseStack, buffer.getBuffer(RenderType.eyes(WHITE_TEXTURE)), fullBright, posX, posY, posZ);

        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderEventHorizon(PoseStack stack, VertexConsumer buffer, TextureAtlasSprite sprite, int packedLight) {
        stack.pushPose();
        float radius = 1.8F;
        float u = sprite.getU(0.5f);
        float v = sprite.getV(0.5f);
        renderSphere(stack, buffer, radius, 32, 32, 0.0f, 0.0f, 0.0f, 1.0f, u, v, packedLight);
        stack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderPlanets(FornaxUniversi machine, float partialTicks, PoseStack stack, VertexConsumer buffer, int packedLight) {
        float time = (machine.getOffsetTimer() + partialTicks);
        Random r = new Random(42);

        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS);

        ResourceLocation[] textures = new ResourceLocation[] {

                MAGMA_TEXTURE_LOC,
                LAPIS_TEXTURE_LOC,

                STONE_TEXTURE_LOC,
                MAGMA_TEXTURE_LOC,
                COBBLESTONE_TEXTURE_LOC,

                LAPIS_TEXTURE_LOC,
                MAGMA_TEXTURE_LOC,
                STONE_TEXTURE_LOC,
                MAGMA_TEXTURE_LOC
        };

        stack.pushPose();
        stack.mulPose(Axis.XP.rotationDegrees(5.0F));

        for (int i = 0; i < 9; i++) {
            stack.pushPose();

            float orbitalSpeed;
            float radiusBase;
            float radiusVar;

            if (i < 2) {
                orbitalSpeed = 8.0f;
                radiusBase = 2.8f;
                radiusVar = 0.5f;
            } else if (i < 5) {
                orbitalSpeed = 2.0f;
                radiusBase = 4.5f;
                radiusVar = 1.0f;
            } else {
                orbitalSpeed = 1.5f;
                radiusBase = 6.5f;
                radiusVar = 1.5f;
            }

            float angleOffset = r.nextFloat() * 360.0f;
            float currentAngle = (time * orbitalSpeed) + angleOffset;

            float radius = radiusBase + r.nextFloat() * radiusVar;

            float bobSpeed = 0.05f + r.nextFloat() * 0.05f;
            float bobOffset = r.nextFloat() * 10.0f;
            float bobHeight = Mth.sin(time * bobSpeed + bobOffset) * 0.3f;

            stack.mulPose(Axis.YP.rotationDegrees(currentAngle));
            stack.translate(radius, bobHeight, 0);

            float scale = 0.2f + r.nextFloat() * 0.2f;
            stack.scale(scale, scale, scale);

            TextureAtlasSprite sprite = atlas.apply(textures[i]);
            renderTexturedSphere(stack, buffer, 1.0f, 16, 16, 1.0f, 1.0f, 1.0f, 1.0f, sprite, packedLight);

            stack.popPose();
        }
        stack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderAccretionDisk(FornaxUniversi machine, float partialTicks, PoseStack stack,
                                     VertexConsumer buffer, int packedLight, float x, float y, float z) {
        float time = (machine.getOffsetTimer() + partialTicks);

        float u = 0.5f;
        float v = 0.5f;
        float intensity = 0.4f;

        stack.pushPose();
        stack.mulPose(Axis.YP.rotationDegrees(time * 3.0F));
        stack.mulPose(Axis.XP.rotationDegrees(5.0F));
        stack.scale(1.0F, 0.1F, 1.0F);

        renderTorus(stack, buffer, 2.5F, 1.0F, 50, 20, intensity, intensity, intensity, 1.0f, u, v, packedLight);
        stack.popPose();

        stack.pushPose();

        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        BlockPos blockPos = machine.getPos();
        double worldX = blockPos.getX() + x;
        double worldY = blockPos.getY() + y;
        double worldZ = blockPos.getZ() + z;

        double dx = camPos.x - worldX;
        double dy = camPos.y - worldY;
        double dz = camPos.z - worldZ;
        double dHor = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) (Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
        float pitch = (float) (-(Math.atan2(dy, dHor) * (180.0 / Math.PI)));

        stack.mulPose(Axis.YP.rotationDegrees(-yaw));
        stack.mulPose(Axis.XP.rotationDegrees(pitch));
        stack.mulPose(Axis.XP.rotationDegrees(90.0F));

        stack.scale(1.0F, 0.15F, 1.0F);

        renderTorus(stack, buffer, 2.2F, 0.4F, 40, 20, intensity, intensity, intensity, 1.0f, u, v, packedLight);

        stack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderTorus(PoseStack poseStack, VertexConsumer buffer, float majorRadius, float minorRadius,
                             int majorSegments, int minorSegments, float r, float g, float b, float a,
                             float u, float v, int light) {
        Matrix4f mat = poseStack.last().pose();
        Matrix3f fixedNormalMat = new Matrix3f();

        for (int i = 0; i < majorSegments; i++) {
            float uFraction0 = (float) i / majorSegments;
            float theta0 = uFraction0 * (float) Math.PI * 2.0f;
            float theta1 = ((float) (i + 1) / majorSegments) * (float) Math.PI * 2.0f;

            float cosTheta0 = Mth.cos(theta0);
            float sinTheta0 = Mth.sin(theta0);
            float cosTheta1 = Mth.cos(theta1);
            float sinTheta1 = Mth.sin(theta1);

            for (int j = 0; j < minorSegments; j++) {
                float vFraction0 = (float) j / minorSegments;
                float phi0 = vFraction0 * (float) Math.PI * 2.0f;
                float phi1 = ((float) (j + 1) / minorSegments) * (float) Math.PI * 2.0f;

                float cosPhi0 = Mth.cos(phi0);
                float sinPhi0 = Mth.sin(phi0);
                float cosPhi1 = Mth.cos(phi1);
                float sinPhi1 = Mth.sin(phi1);

                addTorusVertex(buffer, mat, fixedNormalMat, majorRadius, minorRadius, cosTheta0, sinTheta0, cosPhi0, sinPhi0, u, v, r, g, b, a, light);
                addTorusVertex(buffer, mat, fixedNormalMat, majorRadius, minorRadius, cosTheta0, sinTheta0, cosPhi1, sinPhi1, u, v, r, g, b, a, light);
                addTorusVertex(buffer, mat, fixedNormalMat, majorRadius, minorRadius, cosTheta1, sinTheta1, cosPhi1, sinPhi1, u, v, r, g, b, a, light);
                addTorusVertex(buffer, mat, fixedNormalMat, majorRadius, minorRadius, cosTheta1, sinTheta1, cosPhi0, sinPhi0, u, v, r, g, b, a, light);
            }
        }
    }

    private void addTorusVertex(VertexConsumer buffer, Matrix4f mat, Matrix3f normalMat,
                                float R, float r, float cosTheta, float sinTheta, float cosPhi, float sinPhi,
                                float u, float v, float red, float green, float blue, float alpha, int light) {
        float vx = (R + r * cosPhi) * cosTheta;
        float vy = r * sinPhi;
        float vz = (R + r * cosPhi) * sinTheta;

        buffer.vertex(mat, vx, vy, vz)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normalMat, 0.0f, 1.0f, 0.0f)
                .endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderSphere(PoseStack poseStack, VertexConsumer buffer, float radius, int longitudes, int latitudes,
                              float red, float green, float blue, float alpha,
                              float u, float v, int light) {
        Matrix4f mat = poseStack.last().pose();
        Matrix3f normalMat = poseStack.last().normal();

        for (int i = 0; i < latitudes; i++) {
            float fLat0 = (float) i / latitudes;
            float angleLat0 = (float) Math.PI * (-0.5f + fLat0);
            float angleLat1 = (float) Math.PI * (-0.5f + (float) (i + 1) / latitudes);
            float sinLat0 = Mth.sin(angleLat0);
            float cosLat0 = Mth.cos(angleLat0);
            float sinLat1 = Mth.sin(angleLat1);
            float cosLat1 = Mth.cos(angleLat1);

            for (int j = 0; j < longitudes; j++) {
                float fLng0 = (float) j / longitudes;
                float lng0 = (float) (2.0 * Math.PI * fLng0);
                float lng1 = (float) (2.0 * Math.PI * (float) (j + 1) / longitudes);

                addVertex(buffer, mat, normalMat, 0, 0, 0, radius, lng0, sinLat0, cosLat0, u, v, red, green, blue, alpha, light);
                addVertex(buffer, mat, normalMat, 0, 0, 0, radius, lng0, sinLat1, cosLat1, u, v, red, green, blue, alpha, light);
                addVertex(buffer, mat, normalMat, 0, 0, 0, radius, lng1, sinLat1, cosLat1, u, v, red, green, blue, alpha, light);
                addVertex(buffer, mat, normalMat, 0, 0, 0, radius, lng1, sinLat0, cosLat0, u, v, red, green, blue, alpha, light);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void renderTexturedSphere(PoseStack poseStack, VertexConsumer buffer, float radius, int longitudes, int latitudes,
                                      float red, float green, float blue, float alpha,
                                      TextureAtlasSprite sprite, int light) {
        Matrix4f mat = poseStack.last().pose();
        Matrix3f normalMat = poseStack.last().normal();

        for (int i = 0; i < latitudes; i++) {
            float fLat0 = (float) i / latitudes;
            float angleLat0 = (float) Math.PI * (-0.5f + fLat0);
            float angleLat1 = (float) Math.PI * (-0.5f + (float) (i + 1) / latitudes);
            float sinLat0 = Mth.sin(angleLat0);
            float cosLat0 = Mth.cos(angleLat0);
            float sinLat1 = Mth.sin(angleLat1);
            float cosLat1 = Mth.cos(angleLat1);

            float v0 = sprite.getV(fLat0 * 16.0f);
            float v1 = sprite.getV(((float) (i + 1) / latitudes) * 16.0f);

            for (int j = 0; j < longitudes; j++) {
                float fLng0 = (float) j / longitudes;
                float lng0 = (float) (2.0 * Math.PI * fLng0);
                float lng1 = (float) (2.0 * Math.PI * (float) (j + 1) / longitudes);

                float u0 = sprite.getU(fLng0 * 16.0f);
                float u1 = sprite.getU(((float) (j + 1) / longitudes) * 16.0f);

                addVertex(buffer, mat, normalMat, 0, 0, 0, radius, lng0, sinLat0, cosLat0, u0, v0, red, green, blue, alpha, light);
                addVertex(buffer, mat, normalMat, 0, 0, 0, radius, lng0, sinLat1, cosLat1, u0, v1, red, green, blue, alpha, light);
                addVertex(buffer, mat, normalMat, 0, 0, 0, radius, lng1, sinLat1, cosLat1, u1, v1, red, green, blue, alpha, light);
                addVertex(buffer, mat, normalMat, 0, 0, 0, radius, lng1, sinLat0, cosLat0, u1, v0, red, green, blue, alpha, light);
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
                .color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMat, nx, ny, nz).endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(FornaxUniversi machine) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    @Override
    public AABB getRenderBoundingBox(FornaxUniversi machine) {
        return new AABB(machine.getPos()).inflate(20.0D);
    }

    public static void init(){}
}