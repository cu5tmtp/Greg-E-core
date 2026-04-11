package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.AscencionAltar;
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

@SuppressWarnings("removal")
public class AscencionAltarRender extends DynamicRender<AscencionAltar, AscencionAltarRender> {

    public static final Codec<AscencionAltarRender> CODEC = Codec.unit(new AscencionAltarRender());
    public static final DynamicRenderType<AscencionAltar, AscencionAltarRender> TYPE = new DynamicRenderType<>(AscencionAltarRender.CODEC);

    private static TextureAtlasSprite LAVA_SPRITE_CACHE;
    private static TextureAtlasSprite WHITE_SPRITE_CACHE;
    private static TextureAtlasSprite BLACK_SPRITE_CACHE;

    public AscencionAltarRender() {}

    @Override
    public DynamicRenderType<AscencionAltar, AscencionAltarRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(AscencionAltar machine, Vec3 cameraPos) {
        return machine.getRecipeLogic().isWorking();
    }

    @Override
    public void render(AscencionAltar machine, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        if (LAVA_SPRITE_CACHE == null) {
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(Fluids.LAVA);
            LAVA_SPRITE_CACHE = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(props.getStillTexture());
        }
        if (WHITE_SPRITE_CACHE == null) {
            WHITE_SPRITE_CACHE = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation("minecraft:block/white_concrete"));
        }
        if (BLACK_SPRITE_CACHE == null) {
            BLACK_SPRITE_CACHE = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation("minecraft:block/black_concrete"));
        }

        VertexConsumer solidBuffer = buffer.getBuffer(RenderType.cutout());
        int fullBright = 15728880;

        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();

        var dirUp = RelativeDirection.UP.getRelative(front, upwards, flipped);
        var dirLeft = RelativeDirection.LEFT.getRelative(front, upwards, flipped);
        var dirRight = RelativeDirection.RIGHT.getRelative(front, upwards, flipped);
        var dirBack = RelativeDirection.BACK.getRelative(front, upwards, flipped);

        Vec3 vUp = new Vec3(dirUp.getStepX(), dirUp.getStepY(), dirUp.getStepZ());
        Vec3 vLeft = new Vec3(dirLeft.getStepX(), dirLeft.getStepY(), dirLeft.getStepZ());
        Vec3 vRight = new Vec3(dirRight.getStepX(), dirRight.getStepY(), dirRight.getStepZ());
        Vec3 vBack = new Vec3(dirBack.getStepX(), dirBack.getStepY(), dirBack.getStepZ());

        Vec3 blockCenter = new Vec3(0.5, 0.5, 0.5);
        Vec3 portalCenter = blockCenter.add(vUp.scale(7.0)).add(vLeft.scale(5.0));
        Vec3 vHandDir = vRight;
        float maxOffset = 2.25f;

        float time = machine.getOffsetTimer() + partialTick;
        float cycleLength = 400.0f;
        float cycle = time % cycleLength;

        float portalScale = 0.0f;
        float handOffset = 0.0f;
        boolean fireLasers = false;

        if (cycle < 80) {
            portalScale = cycle / 80.0f;
        } else if (cycle < 160) {
            portalScale = 1.0f;
            handOffset = ((cycle - 80) / 80.0f) * maxOffset;
        } else if (cycle < 240) {
            portalScale = 1.0f;
            handOffset = maxOffset;
            fireLasers = true;
        } else if (cycle < 320) {
            portalScale = 1.0f;
            handOffset = maxOffset * (1.0f - (cycle - 240) / 80.0f);
        } else {
            portalScale = 1.0f - ((cycle - 320) / 80.0f);
        }

        if (portalScale > 0) {
            poseStack.pushPose();
            poseStack.translate(portalCenter.x, portalCenter.y, portalCenter.z);
            poseStack.scale(portalScale * 1.5f, portalScale * 1.5f, portalScale * 1.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(time * 3.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(time * 1.5F));
            renderTexturedSphere(poseStack, solidBuffer, 0, 0, 0, 1.0f, 24, 24, 0.1f, 0.1f, 0.1f, 1.0f, BLACK_SPRITE_CACHE, fullBright);
            poseStack.popPose();
        }

        Vec3 handPos = portalCenter.add(vHandDir.scale(handOffset));
        if (handOffset > 0.01f) {
            renderHand(poseStack, solidBuffer, handPos, portalCenter, vHandDir, vUp, vBack, time, fullBright, handOffset);
        }

        if (fireLasers) {
            Vec3 laserTarget = handPos;
            Vec3[] laserSources = new Vec3[]{
                    blockCenter.add(vRight.scale(6)).add(vUp.scale(5)),
                    blockCenter.add(vBack.scale(-2).add(vRight.scale(5)).add(vUp.scale(4))),
                    blockCenter.add(vBack.scale(2).add(vRight.scale(6)).add(vUp.scale(4))),
                    blockCenter.add(vBack.scale(3).add(vRight.scale(3)).add(vUp.scale(4))),
                    blockCenter.add(vBack.scale(5).add(vLeft.scale(1).add(vUp.scale(2))))
            };

            for (Vec3 source : laserSources) {
                Vec3 laserVec = laserTarget.subtract(source);
                float length = (float) laserVec.length();
                if (length < 0.01f) continue;
                poseStack.pushPose();
                poseStack.translate(source.x, source.y, source.z);
                float yawL = (float) Mth.atan2(laserVec.x, laserVec.z);
                float pitchL = (float) Math.asin(laserVec.y / length);
                poseStack.mulPose(Axis.YP.rotation(yawL));
                poseStack.mulPose(Axis.XP.rotation(-pitchL));
                poseStack.translate(0, 0, length / 2.0);
                poseStack.scale(0.08f, 0.08f, length / 2.0f);
                renderTexturedSphere(poseStack, solidBuffer, 0, 0, 0, 1.0f, 8, 8, 1.0f, 0.2f, 0.2f, 1.0f, LAVA_SPRITE_CACHE, fullBright);
                poseStack.popPose();
            }
        }
    }

    private void renderHand(PoseStack poseStack, VertexConsumer buffer, Vec3 pos, Vec3 portalPos, Vec3 vForward, Vec3 vUp, Vec3 vSide, float time, int light, float distToPortal) {
        poseStack.pushPose();
        poseStack.translate(pos.x, pos.y, pos.z);

        float yaw = (float) Mth.atan2(vForward.x, vForward.z);
        poseStack.mulPose(Axis.YP.rotation(yaw));

        poseStack.mulPose(Axis.XP.rotationDegrees(-90));

        poseStack.pushPose();
        poseStack.scale(0.5f, 0.15f, 0.5f);
        renderTexturedSphere(poseStack, buffer, 0, 0, 0, 1.0f, 12, 12, 1.0f, 1.0f, 1.0f, 1.0f, WHITE_SPRITE_CACHE, light);
        poseStack.popPose();

        float fingerMovement = Mth.sin(time * 0.15f) * 12.0f;

        float[][] fingerPositions = {
                {0.45f, 0.0f, 0.32f},
                {0.15f, 0.0f, 0.45f},
                {0.0f, 0.0f, 0.48f},
                {-0.15f, 0.0f, 0.45f},
                {-0.35f, 0.0f, 0.35f}
        };

        for (int i = 0; i < 5; i++) {
            poseStack.pushPose();
            poseStack.translate(fingerPositions[i][0], fingerPositions[i][1], fingerPositions[i][2]);

            float baseRot = (i == 0) ? 45 : 25;
            poseStack.mulPose(Axis.XP.rotationDegrees(baseRot + fingerMovement));

            poseStack.pushPose();
            poseStack.scale(0.07f, 0.09f, 0.22f);
            poseStack.translate(0, 0, 0.5);
            renderTexturedSphere(poseStack, buffer, 0, 0, 0, 1.0f, 8, 8, 1.0f, 1.0f, 1.0f, 1.0f, WHITE_SPRITE_CACHE, light);
            poseStack.popPose();

            poseStack.translate(0, 0, 0.25);
            poseStack.mulPose(Axis.XP.rotationDegrees(20 + fingerMovement * 0.5f));
            poseStack.pushPose();
            poseStack.scale(0.05f, 0.07f, 0.18f);
            poseStack.translate(0, 0, 0.5);
            renderTexturedSphere(poseStack, buffer, 0, 0, 0, 1.0f, 8, 8, 0.1f, 0.1f, 0.1f, 1.0f, BLACK_SPRITE_CACHE, light);
            poseStack.popPose();

            poseStack.popPose();
        }

        poseStack.popPose();
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
    public boolean shouldRenderOffScreen(AscencionAltar machine) { return machine.isFormed(); }

    @Override
    public int getViewDistance() { return 128; }

    @Override
    public AABB getRenderBoundingBox(AscencionAltar m) { return new AABB(m.getPos()).inflate(16.0D); }

    public static void init() {
    }
}