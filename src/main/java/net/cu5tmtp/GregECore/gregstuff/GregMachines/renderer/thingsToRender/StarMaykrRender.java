package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.StarMaykr;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.math.Axis;
import org.joml.Matrix4f;
import org.joml.Matrix3f;

import static net.minecraft.world.level.block.Blocks.WHITE_CONCRETE;

@SuppressWarnings("removal")
public class StarMaykrRender extends DynamicRender<StarMaykr, StarMaykrRender> {

    public static final Codec<StarMaykrRender> CODEC = Codec.unit(new StarMaykrRender());
    public static final DynamicRenderType<StarMaykr, StarMaykrRender> TYPE = new DynamicRenderType<>(StarMaykrRender.CODEC);
    protected float delta = 0;
    private static TextureAtlasSprite LAVA_SPRITE_CACHE;
    private static TextureAtlasSprite WHITE_SPRITE_CACHE;

    public StarMaykrRender() {}

    @Override
    public DynamicRenderType<StarMaykr, StarMaykrRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(StarMaykr machine, Vec3 cameraPos) {
        return machine.isFormed();
    }

    @Override
    public void render(StarMaykr machine, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        if (LAVA_SPRITE_CACHE == null) {
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(Fluids.LAVA);
            LAVA_SPRITE_CACHE = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(props.getStillTexture());
        }
        if (WHITE_SPRITE_CACHE == null) {
            WHITE_SPRITE_CACHE = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation("minecraft:block/white_concrete"));
        }

        VertexConsumer lavaBuffer = buffer.getBuffer(RenderType.cutout());

        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var back = RelativeDirection.BACK.getRelative(front, upwards, flipped);
        var up = RelativeDirection.UP.getRelative(front, upwards, flipped);

        float posX = (back.getStepX() * 5.0F) + (up.getStepX() * 5.0F) + 0.5F;
        float posY = (back.getStepY() * 5.0F) + (up.getStepY() * 5.0F) + 0.5F;
        float posZ = (back.getStepZ() * 5.0F) + (up.getStepZ() * 5.0F) + 0.5F;

        renderLavaSphere(machine, partialTick, poseStack, lavaBuffer, posX, posY, posZ);
        renderRotatingRings(machine, partialTick, poseStack, buffer, posX, posY, posZ, packedLight);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderLavaSphere(StarMaykr machine, float partialTicks, PoseStack stack,
                                  VertexConsumer buffer, float x, float y, float z) {
        var alpha = 1f;
        int fullBright = 15728880;

        renderTexturedSphere(stack, buffer, x, y, z, 1.2F, 24, 24, 1.0f, 1.0f, 1.0f, alpha, LAVA_SPRITE_CACHE, fullBright);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderRotatingRings(StarMaykr machine, float partialTicks, PoseStack stack,
                                     MultiBufferSource bufferSource, float x, float y, float z, int packedLight) {
        float time = (machine.getOffsetTimer() + partialTicks);

        VertexConsumer ringBuffer = bufferSource.getBuffer(RenderType.cutout());

        float r = 0.15f;
        float g = 0.15f;
        float b = 0.15f;

        stack.pushPose();
        stack.translate(x, y, z);
        stack.mulPose(Axis.YP.rotationDegrees(time * 2.0F));
        stack.mulPose(Axis.XP.rotationDegrees(45.0F));
        renderTorus(stack, ringBuffer, 2.5F, 0.15F, 32, 16, r, g, b, 1.0f, WHITE_SPRITE_CACHE, packedLight);
        stack.popPose();

        stack.pushPose();
        stack.translate(x, y, z);
        stack.mulPose(Axis.ZP.rotationDegrees(time * -1.5F));
        stack.mulPose(Axis.XP.rotationDegrees(-45.0F));
        renderTorus(stack, ringBuffer, 3.2F, 0.12F, 32, 16, r, g, b, 1.0f, WHITE_SPRITE_CACHE, packedLight);
        stack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderTorus(PoseStack poseStack, VertexConsumer buffer, float majorRadius, float minorRadius,
                             int majorSegments, int minorSegments, float r, float g, float b, float a,
                             TextureAtlasSprite sprite, int light) {
        Matrix4f mat = poseStack.last().pose();
        Matrix3f normalMat = poseStack.last().normal();

        for (int i = 0; i < majorSegments; i++) {
            float uFraction0 = (float) i / majorSegments;
            float uFraction1 = (float) (i + 1) / majorSegments;
            float theta0 = uFraction0 * (float) Math.PI * 2.0f;
            float theta1 = uFraction1 * (float) Math.PI * 2.0f;

            float cosTheta0 = Mth.cos(theta0);
            float sinTheta0 = Mth.sin(theta0);
            float cosTheta1 = Mth.cos(theta1);
            float sinTheta1 = Mth.sin(theta1);

            for (int j = 0; j < minorSegments; j++) {
                float vFraction0 = (float) j / minorSegments;
                float vFraction1 = (float) (j + 1) / minorSegments;
                float phi0 = vFraction0 * (float) Math.PI * 2.0f;
                float phi1 = vFraction1 * (float) Math.PI * 2.0f;

                float cosPhi0 = Mth.cos(phi0);
                float sinPhi0 = Mth.sin(phi0);
                float cosPhi1 = Mth.cos(phi1);
                float sinPhi1 = Mth.sin(phi1);

                float texU0 = sprite.getU(uFraction0 * 16.0f);
                float texU1 = sprite.getU(uFraction1 * 16.0f);
                float texV0 = sprite.getV(vFraction0 * 16.0f);
                float texV1 = sprite.getV(vFraction1 * 16.0f);

                addTorusVertex(buffer, mat, normalMat, majorRadius, minorRadius, cosTheta0, sinTheta0, cosPhi0, sinPhi0, texU0, texV0, r, g, b, a, light);
                addTorusVertex(buffer, mat, normalMat, majorRadius, minorRadius, cosTheta0, sinTheta0, cosPhi1, sinPhi1, texU0, texV1, r, g, b, a, light);
                addTorusVertex(buffer, mat, normalMat, majorRadius, minorRadius, cosTheta1, sinTheta1, cosPhi1, sinPhi1, texU1, texV1, r, g, b, a, light);
                addTorusVertex(buffer, mat, normalMat, majorRadius, minorRadius, cosTheta1, sinTheta1, cosPhi0, sinPhi0, texU1, texV0, r, g, b, a, light);
            }
        }
    }

    private void addTorusVertex(VertexConsumer buffer, Matrix4f mat, Matrix3f normalMat,
                                float R, float r, float cosTheta, float sinTheta, float cosPhi, float sinPhi,
                                float u, float v, float red, float green, float blue, float alpha, int light) {
        float vx = (R + r * cosPhi) * cosTheta;
        float vy = r * sinPhi;
        float vz = (R + r * cosPhi) * sinTheta;

        float nx = cosPhi * cosTheta;
        float ny = sinPhi;
        float nz = cosPhi * sinTheta;

        buffer.vertex(mat, vx, vy, vz).color(red, green, blue, alpha).uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMat, nx, ny, nz).endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderTexturedSphere(PoseStack poseStack, VertexConsumer buffer, float x, float y, float z,
                                      float radius, int longitudes, int latitudes,
                                      float red, float green, float blue, float alpha,
                                      TextureAtlasSprite sprite, int light) {
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

            float v0 = sprite.getV(fLat0 * 16.0f);
            float v1 = sprite.getV(fLat1 * 16.0f);

            for (int j = 0; j < longitudes; j++) {
                float fLng0 = (float) j / longitudes;
                float fLng1 = (float) (j + 1) / longitudes;
                float lng0 = (float) (2.0 * Math.PI * fLng0);
                float lng1 = (float) (2.0 * Math.PI * fLng1);

                float u0 = sprite.getU(fLng0 * 16.0f);
                float u1 = sprite.getU(fLng1 * 16.0f);

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
    public boolean shouldRenderOffScreen(StarMaykr machine) {
        return machine.getRecipeLogic().isWorking() || delta > 0;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }

    @Override
    public AABB getRenderBoundingBox(StarMaykr machine) {
        return new AABB(machine.getPos()).inflate(12.0D);
    }

    public static void init(){}
}