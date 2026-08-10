package net.cu5tmtp.GregECore.wandOfPuppetry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
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
import net.minecraft.client.model.geom.ModelPart;

public class AnimatedBlockRenderer extends EntityRenderer<AnimatedBlockEntity> {

    public static final ModelLayerLocation ANIMATED_BLOCK_LAYER = new ModelLayerLocation(
            new ResourceLocation("gregecore", "animated_block"), "main");

    private final AnimatedBlockModel<AnimatedBlockEntity> model;

    public AnimatedBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new AnimatedBlockModel<>(context.bakeLayer(ANIMATED_BLOCK_LAYER));
    }

    @Override
    public void render(AnimatedBlockEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BlockState blockState = entity.getBlockState();
        if (blockState == null || blockState.isAir()) return;

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        TextureAtlasSprite sprite = blockRenderer.getBlockModel(blockState).getParticleIcon();
        float u = sprite.getU(8);
        float v = sprite.getV(8);

        poseStack.pushPose();

        float bodyYaw = Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));

        poseStack.pushPose();
        poseStack.translate(-0.5, 0.375, -0.5);
        blockRenderer.renderSingleBlock(blockState, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.cutout());
        poseStack.popPose();

        float walkAnimPos = entity.walkAnimation.position(partialTicks);
        float walkAnimSpeed = entity.walkAnimation.speed(partialTicks);

        this.model.setupAnim(entity, walkAnimPos, walkAnimSpeed, entity.tickCount + partialTicks, 0, 0);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
        ModelPart[] legs = this.model.getLegs();

        for (int i = 0; i < 8; i++) {
            poseStack.pushPose();
            legs[i].translateAndRotate(poseStack);
            renderSolidLeg(poseStack, vertexConsumer, u, v, packedLight);
            poseStack.popPose();
        }

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderSolidLeg(PoseStack poseStack, VertexConsumer consumer, float u, float v, int light) {
        float w = 0.5f / 16.0f;
        float h = 6.0f / 16.0f;

        float yT = 0.0f;
        float yB = -h;

        renderFace(poseStack, consumer, u, v, light, -w, yB, w,  w, yB, w,  w, yT, w,  -w, yT, w,  0, 0, 1);
        renderFace(poseStack, consumer, u, v, light, w, yB, -w,  -w, yB, -w,  -w, yT, -w,  w, yT, -w,  0, 0, -1);
        renderFace(poseStack, consumer, u, v, light, -w, yB, -w,  -w, yB, w,  -w, yT, w,  -w, yT, -w,  -1, 0, 0);
        renderFace(poseStack, consumer, u, v, light, w, yB, w,  w, yB, -w,  w, yT, -w,  w, yT, w,  1, 0, 0);
        renderFace(poseStack, consumer, u, v, light, -w, yT, w,  w, yT, w,  w, yT, -w,  -w, yT, -w,  0, 1, 0);
        renderFace(poseStack, consumer, u, v, light, -w, yB, -w,  w, yB, -w,  w, yB, w,  -w, yB, w,  0, -1, 0);
    }

    private void renderFace(PoseStack poseStack, VertexConsumer consumer, float u, float v, int light,
                            float x1, float y1, float z1, float x2, float y2, float z2,
                            float x3, float y3, float z3, float x4, float y4, float z4,
                            float nx, float ny, float nz) {
        Matrix4f p = poseStack.last().pose();
        Matrix3f n = poseStack.last().normal();
        consumer.vertex(p, x1, y1, z1).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, nx, ny, nz).endVertex();
        consumer.vertex(p, x2, y2, z2).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, nx, ny, nz).endVertex();
        consumer.vertex(p, x3, y3, z3).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, nx, ny, nz).endVertex();
        consumer.vertex(p, x4, y4, z4).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, nx, ny, nz).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(AnimatedBlockEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}