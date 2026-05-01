package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.RealityFractureEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@SuppressWarnings("removal")
public class RealityFractureEngineRender extends DynamicRender<RealityFractureEngine, RealityFractureEngineRender> {

    public static final Codec<RealityFractureEngineRender> CODEC = Codec.unit(new RealityFractureEngineRender());
    public static final DynamicRenderType<RealityFractureEngine, RealityFractureEngineRender> TYPE = new DynamicRenderType<>(RealityFractureEngineRender.CODEC);

    private static TextureAtlasSprite WHITE_SPRITE_CACHE;
    private static TextureAtlasSprite BLACK_SPRITE_CACHE;
    private final RandomSource random = RandomSource.create();

    public RealityFractureEngineRender() {}

    @Override
    public DynamicRenderType<RealityFractureEngine, RealityFractureEngineRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(RealityFractureEngine machine, Vec3 cameraPos) {
        return machine.isFormed();
    }

    @Override
    public void render(RealityFractureEngine machine, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        if (WHITE_SPRITE_CACHE == null) {
            WHITE_SPRITE_CACHE = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation("minecraft:block/white_concrete"));
        }
        if (BLACK_SPRITE_CACHE == null) {
            BLACK_SPRITE_CACHE = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation("minecraft:block/black_concrete"));
        }

        VertexConsumer solidBuffer = buffer.getBuffer(RenderType.entitySolid(InventoryMenu.BLOCK_ATLAS));
        int fullBright = 15728880;

        var frontDir = machine.getFrontFacing();
        var upwardsDir = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();

        var dirUp = RelativeDirection.UP.getRelative(frontDir, upwardsDir, flipped);
        var dirBack = RelativeDirection.BACK.getRelative(frontDir, upwardsDir, flipped);
        var dirLeft = RelativeDirection.LEFT.getRelative(frontDir, upwardsDir, flipped);
        var dirRight = RelativeDirection.RIGHT.getRelative(frontDir, upwardsDir, flipped);
        var dirFront = RelativeDirection.FRONT.getRelative(frontDir, upwardsDir, flipped);

        Vec3 vUp = new Vec3(dirUp.getStepX(), dirUp.getStepY(), dirUp.getStepZ());
        Vec3 vBack = new Vec3(dirBack.getStepX(), dirBack.getStepY(), dirBack.getStepZ());
        Vec3 vLeft = new Vec3(dirLeft.getStepX(), dirLeft.getStepY(), dirLeft.getStepZ());
        Vec3 vRight = new Vec3(dirRight.getStepX(), dirRight.getStepY(), dirRight.getStepZ());
        Vec3 vFront = new Vec3(dirFront.getStepX(), dirFront.getStepY(), dirFront.getStepZ());

        Vec3 blockCenter = new Vec3(0.5, 0.5, 0.5);

        Vec3 lightningDirCenter = vFront;
        Vec3 lightningDirRight = vFront.add(vRight.scale(90.0)).normalize();
        Vec3 lightningDirLeft = vFront.add(vLeft.scale(90.0)).normalize();

        Vec3 eyeLookCenter = vFront;
        Vec3 eyeLookRight = lightningDirRight.reverse();
        Vec3 eyeLookLeft = lightningDirLeft.reverse();

        Vec3 posEye1 = blockCenter.add(vUp.scale(15.0).add(vBack.scale(21.0)));
        Vec3 posEye2 = blockCenter.add(vUp.scale(15.0)).add(vLeft.scale(17.0)).add(vBack.scale(4.0));
        Vec3 posEye3 = blockCenter.add(vUp.scale(15.0)).add(vRight.scale(17.0)).add(vBack.scale(4.0));

        renderEye(poseStack, solidBuffer, posEye1, 0.6f, 0.2f, 1.0f, 0.2f, fullBright, eyeLookCenter);
        renderEye(poseStack, solidBuffer, posEye2, 0.5f, 1.0f, 0.3f, 0.3f, fullBright, eyeLookRight);
        renderEye(poseStack, solidBuffer, posEye3, 0.5f, 0.6f, 0.6f, 1.0f, fullBright, eyeLookLeft);

        if (machine.getOffsetTimer() % 8 < 3) {
            VertexConsumer lightningBuffer = buffer.getBuffer(RenderType.lightning());
            random.setSeed((long) (machine.getOffsetTimer() / 3));

            float lightningLength = 12.0f;
            float startOffset = 2.0f;

            renderPrismLightning(poseStack, lightningBuffer, posEye1.add(lightningDirCenter.scale(startOffset)), lightningDirCenter, lightningLength, 0.4f, 1.0f, 0.4f);
            renderPrismLightning(poseStack, lightningBuffer, posEye2.add(lightningDirRight.scale(startOffset)), lightningDirRight, lightningLength, 1.0f, 0.3f, 0.3f);
            renderPrismLightning(poseStack, lightningBuffer, posEye3.add(lightningDirLeft.scale(startOffset)), lightningDirLeft, lightningLength, 0.6f, 0.6f, 1.0f);
        }
    }

    private void renderEye(PoseStack poseStack, VertexConsumer buffer, Vec3 pos, float scale, float r, float g, float b, int light, Vec3 lookDir) {
        poseStack.pushPose();
        poseStack.translate(pos.x, pos.y, pos.z);

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        renderTexturedSphere(poseStack, buffer, 0, 0, 0, 1.0f, 16, 16, r, g, b, 1.0f, WHITE_SPRITE_CACHE, light);
        poseStack.popPose();
        
        poseStack.pushPose();
        Vector3f look = new Vector3f((float)lookDir.x, (float)lookDir.y, (float)lookDir.z).normalize();
        Quaternionf rotation = new Quaternionf().lookAlong(look, new Vector3f(0, 1, 0));
        poseStack.mulPose(rotation);

        poseStack.translate(0, 0, scale * 0.95f);
        poseStack.scale(scale * 0.35f, scale * 0.35f, scale * 0.1f);

        renderTexturedSphere(poseStack, buffer, 0, 0, 0, 1.0f, 12, 12, 0.01f, 0.01f, 0.01f, 1.0f, BLACK_SPRITE_CACHE, light);
        poseStack.popPose();

        poseStack.popPose();
    }

    private void renderPrismLightning(PoseStack poseStack, VertexConsumer buffer, Vec3 start, Vec3 direction, float length, float r, float g, float b) {
        Matrix4f matrix = poseStack.last().pose();
        Vec3 current = start;
        int segments = 8;
        float segmentLength = length / segments;
        float deviation = 0.65f;
        float thickness = 0.18f;

        for (int i = 0; i < segments; i++) {
            Vec3 nextBase = start.add(direction.scale((i + 1) * segmentLength));
            Vec3 next;
            if (i < segments - 1) {
                next = nextBase.add(
                        (random.nextFloat() - 0.5f) * deviation,
                        (random.nextFloat() - 0.5f) * deviation,
                        (random.nextFloat() - 0.5f) * deviation
                );
            } else {
                next = nextBase;
            }

            drawLightningPrism(buffer, matrix, current, next, thickness, r, g, b, 1.0f);
            current = next;
        }
    }

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

    private void addFace(VertexConsumer buffer, Matrix4f matrix, Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4, float r, float g, float b, float a) {
        buffer.vertex(matrix, v1.x, v1.y, v1.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, v2.x, v2.y, v2.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, v3.x, v3.y, v3.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, v4.x, v4.y, v4.z).color(r, g, b, a).endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderTexturedSphere(PoseStack poseStack, VertexConsumer buffer, float x, float y, float z, float radius, int lngs, int lats, float r, float g, float b, float a, TextureAtlasSprite sprite, int light) {
        Matrix4f mat = poseStack.last().pose();
        Matrix3f normalMat = poseStack.last().normal();
        for (int i = 0; i < lats; i++) {
            float fL0 = (float) i / lats, fL1 = (float) (i + 1) / lats;
            float aL0 = (float) Math.PI * (-0.5f + fL0), aL1 = (float) Math.PI * (-0.5f + fL1);
            for (int j = 0; j < lngs; j++) {
                float fG0 = (float) j / lngs, fG1 = (float) (j + 1) / lngs;
                float g0 = (float) (2.0 * Math.PI * fG0), g1 = (float) (2.0 * Math.PI * fG1);
                addVertex(buffer, mat, normalMat, x, y, z, radius, g0, Mth.sin(aL0), Mth.cos(aL0), sprite.getU(fG0*16), sprite.getV(fL0*16), r, g, b, a, light);
                addVertex(buffer, mat, normalMat, x, y, z, radius, g0, Mth.sin(aL1), Mth.cos(aL1), sprite.getU(fG0*16), sprite.getV(fL1*16), r, g, b, a, light);
                addVertex(buffer, mat, normalMat, x, y, z, radius, g1, Mth.sin(aL1), Mth.cos(aL1), sprite.getU(fG1*16), sprite.getV(fL1*16), r, g, b, a, light);
                addVertex(buffer, mat, normalMat, x, y, z, radius, g1, Mth.sin(aL0), Mth.cos(aL0), sprite.getU(fG1*16), sprite.getV(fL0*16), r, g, b, a, light);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void addVertex(VertexConsumer b, Matrix4f m, Matrix3f n, float x, float y, float z, float rad, float lng, float sL, float cL, float u, float v, float r, float g, float bl, float a, int light) {
        float nx = Mth.cos(lng) * cL, ny = sL, nz = Mth.sin(lng) * cL;
        b.vertex(m, x + nx * rad, y + ny * rad, z + nz * rad).color(r, g, bl, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, nx, ny, nz).endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(RealityFractureEngine machine) { return machine.isFormed(); }

    @Override
    public int getViewDistance() { return 128; }

    @Override
    public AABB getRenderBoundingBox(RealityFractureEngine m) { return new AABB(m.getPos()).inflate(40.0D); }

    public static void init() {}
}