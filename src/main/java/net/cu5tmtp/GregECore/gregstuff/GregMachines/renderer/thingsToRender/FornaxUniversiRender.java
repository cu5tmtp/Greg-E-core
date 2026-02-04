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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.math.Axis;
import org.joml.Matrix4f;
import org.joml.Matrix3f;

import java.util.Random;
import java.util.function.Function;

@SuppressWarnings("removal")
public class FornaxUniversiRender extends DynamicRender<FornaxUniversi, FornaxUniversiRender> {

    public static final Codec<FornaxUniversiRender> CODEC = Codec.unit(new FornaxUniversiRender());
    public static final DynamicRenderType<FornaxUniversi, FornaxUniversiRender> TYPE = new DynamicRenderType<>(FornaxUniversiRender.CODEC);

    private static final int FULL_BRIGHT = 15728880;
    private static final Matrix3f IDENTITY_MATRIX_3F = new Matrix3f();

    private static final float DISK_U = 0.5f;
    private static final float DISK_V = 0.5f;
    private static final float DISK_INTENSITY = 0.4f;

    private static final ResourceLocation WHITE_TEXTURE = new ResourceLocation("minecraft:textures/block/white_concrete.png");
    private static final ResourceLocation WHITE_TEXTURE_LOC = new ResourceLocation("minecraft:block/white_concrete");

    private static final ResourceLocation MAGMA_TEXTURE_LOC = new ResourceLocation("minecraft:block/magma");
    private static final ResourceLocation STONE_TEXTURE_LOC = new ResourceLocation("minecraft:block/stone");
    private static final ResourceLocation COBBLESTONE_TEXTURE_LOC = new ResourceLocation("minecraft:block/cobblestone");
    private static final ResourceLocation LAPIS_TEXTURE_LOC = new ResourceLocation("minecraft:block/lapis_block");


    private static final ResourceLocation[] TEXTURE_POOL = new ResourceLocation[] {
            MAGMA_TEXTURE_LOC, LAPIS_TEXTURE_LOC, STONE_TEXTURE_LOC,
            MAGMA_TEXTURE_LOC, COBBLESTONE_TEXTURE_LOC,
            LAPIS_TEXTURE_LOC, MAGMA_TEXTURE_LOC, STONE_TEXTURE_LOC, MAGMA_TEXTURE_LOC
    };

    private static final PlanetData[] PLANETS = new PlanetData[9];

    private static final ArcData[] ARCS = new ArcData[] {
            new ArcData(5.5f, 2.2f, 90.0f, 0.08f, 0.0f),
            new ArcData(4.0f, 2.5f, 90.0f, 0.1f, 120.0f),
            new ArcData(2.5f, 2.8f, 90.0f, 0.08f, 240.0f)
    };

    private static ItemStack CACHED_ROCKET_STACK = null;

    static {
        Random r = new Random(42);
        for (int i = 0; i < 9; i++) {
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
            float radius = radiusBase + r.nextFloat() * radiusVar;
            float bobSpeed = 0.05f + r.nextFloat() * 0.05f;
            float bobOffset = r.nextFloat() * 10.0f;
            float scale = 0.2f + r.nextFloat() * 0.2f;

            PLANETS[i] = new PlanetData(orbitalSpeed, angleOffset, radius, bobSpeed, bobOffset, scale, TEXTURE_POOL[i]);
        }
    }

    private static class PlanetData {
        final float speed;
        final float angleOffset;
        final float radius;
        final float bobSpeed;
        final float bobOffset;
        final float scale;
        final ResourceLocation texture;

        PlanetData(float speed, float angleOffset, float radius, float bobSpeed, float bobOffset, float scale, ResourceLocation texture) {
            this.speed = speed;
            this.angleOffset = angleOffset;
            this.radius = radius;
            this.bobSpeed = bobSpeed;
            this.bobOffset = bobOffset;
            this.scale = scale;
            this.texture = texture;
        }
    }

    private static class ArcData {
        final float speed;
        final float radius;
        final float lengthDeg;
        final float thickness;
        final float angleOffset;

        ArcData(float speed, float radius, float lengthDeg, float thickness, float angleOffset) {
            this.speed = speed;
            this.radius = radius;
            this.lengthDeg = lengthDeg;
            this.thickness = thickness;
            this.angleOffset = angleOffset;
        }
    }

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

        float posX = (back.getStepX() * 3.0F) + (up.getStepX() * 9.0F) + 0.5F;
        float posY = (back.getStepY() * 3.0F) + (up.getStepY() * 9.0F) + 0.5F;
        float posZ = (back.getStepZ() * 3.0F) + (up.getStepZ() * 9.0F) + 0.5F;

        poseStack.pushPose();
        poseStack.translate(posX, posY, posZ);

        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS);

        TextureAtlasSprite whiteSprite = atlas.apply(WHITE_TEXTURE_LOC);

        renderEventHorizon(poseStack, buffer.getBuffer(RenderType.solid()), whiteSprite, FULL_BRIGHT);

        renderPlanets(machine, partialTick, poseStack, buffer.getBuffer(RenderType.solid()), atlas, FULL_BRIGHT);

        if (machine.getRecipeLogic().isWorking()) {
            renderRocket(machine, partialTick, poseStack, buffer, FULL_BRIGHT);
        }

        renderMotionArcs(machine, partialTick, poseStack, buffer.getBuffer(RenderType.eyes(WHITE_TEXTURE)), FULL_BRIGHT);

        renderAccretionDisk(machine, partialTick, poseStack, buffer.getBuffer(RenderType.eyes(WHITE_TEXTURE)), FULL_BRIGHT, posX, posY, posZ);

        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderRocket(FornaxUniversi machine, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        if (CACHED_ROCKET_STACK == null) {
            Item adAstraRocket = ForgeRegistries.ITEMS.getValue(new ResourceLocation("ad_astra", "tier_1_rocket"));
            if (adAstraRocket != null && adAstraRocket != Items.AIR) {
                CACHED_ROCKET_STACK = new ItemStack(adAstraRocket);
            } else {
                CACHED_ROCKET_STACK = new ItemStack(Items.FIREWORK_ROCKET);
            }
        }

        float totalTime = 600.0f;
        float time = (machine.getOffsetTimer() + partialTicks) % totalTime;
        float progress = time / totalTime;

        float radius;
        float orbitSpeed = 4.0f;
        float currentAngle = time * orbitSpeed;

        stack.pushPose();

        stack.mulPose(Axis.XP.rotationDegrees(5.0F));

        if (progress < 0.15f) {
            float t = progress / 0.15f;
            radius = Mth.lerp(t, 20.0f, 3.8f);
        } else if (progress > 0.85f) {
            float t = (progress - 0.85f) / 0.15f;
            radius = Mth.lerp(t, 3.8f, 20.0f);
        } else {
            radius = 3.8f;
        }

        stack.mulPose(Axis.YP.rotationDegrees(currentAngle));
        stack.translate(radius, 0, 0);

        stack.mulPose(Axis.YP.rotationDegrees(90.0F));

        stack.mulPose(Axis.ZP.rotationDegrees(-90.0F));

        if (progress < 0.15f) {
            stack.mulPose(Axis.ZP.rotationDegrees(-20.0F));
        } else if (progress > 0.85f) {
            stack.mulPose(Axis.ZP.rotationDegrees(20.0F));
        }

        stack.scale(1.5f, 1.5f, 1.5f);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                CACHED_ROCKET_STACK,
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                stack,
                buffer,
                machine.getLevel(),
                0);

        stack.popPose();
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
    private void renderPlanets(FornaxUniversi machine, float partialTicks, PoseStack stack, VertexConsumer buffer, Function<ResourceLocation, TextureAtlasSprite> atlas, int packedLight) {
        float time = (machine.getOffsetTimer() + partialTicks);

        stack.pushPose();
        stack.mulPose(Axis.XP.rotationDegrees(5.0F));

        for (PlanetData planet : PLANETS) {
            stack.pushPose();

            float currentAngle = (time * planet.speed) + planet.angleOffset;
            float bobHeight = Mth.sin(time * planet.bobSpeed + planet.bobOffset) * 0.3f;

            stack.mulPose(Axis.YP.rotationDegrees(currentAngle));
            stack.translate(planet.radius, bobHeight, 0);

            stack.scale(planet.scale, planet.scale, planet.scale);

            TextureAtlasSprite sprite = atlas.apply(planet.texture);
            renderTexturedSphere(stack, buffer, 1.0f, 16, 16, 1.0f, 1.0f, 1.0f, 1.0f, sprite, packedLight);

            stack.popPose();
        }
        stack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderMotionArcs(FornaxUniversi machine, float partialTicks, PoseStack stack, VertexConsumer buffer, int packedLight) {
        float time = (machine.getOffsetTimer() + partialTicks);

        stack.pushPose();
        stack.mulPose(Axis.XP.rotationDegrees(5.0F));
        stack.scale(1.0F, 0.1F, 1.0F);

        for (ArcData arc : ARCS) {
            stack.pushPose();

            float currentRotation = (time * arc.speed) + arc.angleOffset;
            stack.mulPose(Axis.YP.rotationDegrees(currentRotation));

            float lengthRad = arc.lengthDeg * (float)(Math.PI / 180.0);

            renderPartialTorus(stack, buffer, arc.radius, arc.thickness, 0.0f, lengthRad, 30, 8, 0.6f, 0.6f, 0.6f, 1.0f, DISK_U, DISK_V, packedLight);

            stack.popPose();
        }

        stack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderAccretionDisk(FornaxUniversi machine, float partialTicks, PoseStack stack,
                                     VertexConsumer buffer, int packedLight, float x, float y, float z) {
        float time = (machine.getOffsetTimer() + partialTicks);

        stack.pushPose();
        stack.mulPose(Axis.XP.rotationDegrees(5.0F));
        stack.mulPose(Axis.YP.rotationDegrees(time * 3.0F));

        stack.scale(1.0F, 0.1F, 1.0F);

        renderTorus(stack, buffer, 2.5F, 1.0F, 50, 20, DISK_INTENSITY, DISK_INTENSITY, DISK_INTENSITY, 1.0f, DISK_U, DISK_V, packedLight);
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

        renderTorus(stack, buffer, 2.2F, 0.4F, 40, 20, DISK_INTENSITY, DISK_INTENSITY, DISK_INTENSITY, 1.0f, DISK_U, DISK_V, packedLight);

        stack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderTorus(PoseStack poseStack, VertexConsumer buffer, float majorRadius, float minorRadius,
                             int majorSegments, int minorSegments, float r, float g, float b, float a,
                             float u, float v, int light) {
        renderPartialTorus(poseStack, buffer, majorRadius, minorRadius, 0.0f, (float)(Math.PI * 2.0), majorSegments, minorSegments, r, g, b, a, u, v, light);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderPartialTorus(PoseStack poseStack, VertexConsumer buffer, float majorRadius, float minorRadius,
                                    float startAngle, float sweepAngle,
                                    int majorSegments, int minorSegments, float r, float g, float b, float a,
                                    float u, float v, int light) {
        Matrix4f mat = poseStack.last().pose();
        Matrix3f fixedNormalMat = IDENTITY_MATRIX_3F;

        for (int i = 0; i < majorSegments; i++) {
            float fraction0 = (float) i / majorSegments;
            float fraction1 = (float) (i + 1) / majorSegments;

            float theta0 = startAngle + fraction0 * sweepAngle;
            float theta1 = startAngle + fraction1 * sweepAngle;

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