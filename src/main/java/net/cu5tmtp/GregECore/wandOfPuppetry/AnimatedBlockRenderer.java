package net.cu5tmtp.GregECore.wandOfPuppetry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class AnimatedBlockRenderer extends EntityRenderer<AnimatedBlockEntity> {

    public AnimatedBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AnimatedBlockEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BlockState blockState = entity.getBlockState();
        if (blockState == null || blockState.isAir()) return;

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        TextureAtlasSprite sprite = blockRenderer.getBlockModel(blockState).getParticleIcon();

        float singlePixelU = sprite.getU(8);
        float singlePixelV = sprite.getV(8);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));

        float walkAnimPos = entity.walkAnimation.position(partialTicks);
        float walkAnimSpeed = entity.walkAnimation.speed(partialTicks);

        poseStack.pushPose();

        float bodyYaw = Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));

        poseStack.translate(-0.5, 0.0, -0.5);

        try {
            blockRenderer.renderSingleBlock(blockState, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.cutout());

            poseStack.translate(0.5, 0.25, 0.5);

            for (int i = 0; i < 8; i++) {
                boolean isLeft = (i % 2 == 0);
                int pairIndex = i / 2;

                poseStack.pushPose();

                float zOffset = -0.35f + (pairIndex * 0.23f);
                float xOffset = isLeft ? -0.55f : 0.55f;
                poseStack.translate(xOffset, 0.0f, zOffset);

                float phaseOffset = (isLeft ? 0 : (float)Math.PI) + (pairIndex * (float)Math.PI / 2.0f);
                float phase = walkAnimPos * 2.5f + phaseOffset;

                float lift = Math.max(0, Mth.sin(phase)) * walkAnimSpeed * 0.5f;
                float sweep = Mth.cos(phase) * walkAnimSpeed * 0.4f;

                float fanAngle = (1.5f - pairIndex) * 35f;
                float baseYaw = isLeft ? -fanAngle : fanAngle;

                baseYaw += (isLeft ? -sweep : sweep) * 40f;

                poseStack.mulPose(Axis.YP.rotationDegrees(baseYaw));

                float zBase = isLeft ? 40f : -40f;
                float zKnee = isLeft ? 100f : -100f;

                float liftBend = lift * 60f;
                float zAngle = zBase + (isLeft ? -liftBend : liftBend);

                poseStack.mulPose(Axis.ZP.rotationDegrees(zAngle));
                renderLegSegment(poseStack, vertexConsumer, 0.35f, singlePixelU, singlePixelV, packedLight);

                poseStack.translate(0, 0.35f, 0);

                poseStack.mulPose(Axis.ZP.rotationDegrees(zKnee));
                renderLegSegment(poseStack, vertexConsumer, 0.45f, singlePixelU, singlePixelV, packedLight);

                poseStack.popPose();
            }

        } catch (Exception e) {
            System.err.println("Error rendering the block: " + e.getMessage());
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderLegSegment(PoseStack poseStack, VertexConsumer consumer, float length, float u, float v, int light) {
        float t = 0.03f;
        Matrix4f p = poseStack.last().pose();
        Matrix3f n = poseStack.last().normal();

        float[][] vertices = {
                {-t, 0, t, 0, 0, 1},
                {t, 0, t, 0, 0, 1},
                {t, length, t, 0, 0, 1},
                {-t, length, t, 0, 0, 1},
                {t, 0, -t, 0, 0, -1},
                {-t, 0, -t, 0, 0, -1},
                {-t, length, -t, 0, 0, -1},
                {t, length, -t, 0, 0, -1},
                {-t, 0, -t, -1, 0, 0},
                {-t, 0, t, -1, 0, 0},
                {-t, length, t, -1, 0, 0},
                {-t, length, -t, -1, 0, 0},
                {t, 0, t, 1, 0, 0},
                {t, 0, -t, 1, 0, 0},
                {t, length, -t, 1, 0, 0},
                {t, length, t, 1, 0, 0},
                {-t, length, t, 0, 1, 0},
                {t, length, t, 0, 1, 0},
                {t, length, -t, 0, 1, 0},
                {-t, length, -t, 0, 1, 0},
                {-t, 0, -t, 0, -1, 0},
                {t, 0, -t, 0, -1, 0},
                {t, 0, t, 0, -1, 0},
                {-t, 0, t, 0, -1, 0}
        };

        for (int i = 0; i < vertices.length; i += 4) {
            for (int j = 0; j < 4; j++) {
                float[] vtx = vertices[i + j];
                consumer.vertex(p, vtx[0], vtx[1], vtx[2])
                        .color(255, 255, 255, 255)
                        .uv(u, v)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(light)
                        .normal(n, vtx[3], vtx[4], vtx[5])
                        .endVertex();
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(AnimatedBlockEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
