package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.InfusionAltar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Random;

@SuppressWarnings("removal")
public class InfusionAltarRender extends DynamicRender<InfusionAltar, InfusionAltarRender> {

    public static final Codec<InfusionAltarRender> CODEC = Codec.unit(new InfusionAltarRender());
    public static final DynamicRenderType<InfusionAltar, InfusionAltarRender> TYPE = new DynamicRenderType<>(InfusionAltarRender.CODEC);

    private static TextureAtlasSprite RUNIC_MATRIX_SPRITE;
    private static TextureAtlasSprite GLOW_SPRITE;
    private static TextureAtlasSprite CLAW_SPRITE;
    private final Random rand = new Random();

    public InfusionAltarRender() {}

    @Override
    public DynamicRenderType<InfusionAltar, InfusionAltarRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(InfusionAltar machine, Vec3 cameraPos) {
        return machine.isFormed();
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

        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var back = RelativeDirection.BACK.getRelative(front, upwards, flipped);

        float posX = (back.getStepX() * 4.0F) + 0.5F;
        float posY = (back.getStepY() * 4.0F) + 1.0F;
        float posZ = (back.getStepZ() * 4.0F) + 0.5F;

        float time = machine.getOffsetTimer() + partialTick;
        boolean isWorking = machine.getRecipeLogic().isWorking();

        int magicalLight = 15728880;

        renderClawsAndPedestal(poseStack, buffer, posX, posY, posZ, magicalLight, packedOverlay);

        renderRunicMatrix(poseStack, buffer, posX, posY, posZ, time, magicalLight, packedOverlay);

        renderOrbitingItems(machine, poseStack, buffer, posX, posY, posZ, time, isWorking, magicalLight, packedOverlay);

        if (isWorking) {
            renderEnergyBeams(machine, poseStack, buffer, posX, posY, posZ, time);
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
                                   float x, float y, float z, float time,int packedLight, int packedOverlay) {
        float bobbing = Mth.sin(time * 0.05F) * 0.12F;
        float matrixY = y + 2.53F + bobbing;

        stack.pushPose();
        stack.translate(x, matrixY, z);

        stack.mulPose(Axis.YP.rotationDegrees(time * 1.2F));
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
    private void renderOrbitingItems(InfusionAltar machine, PoseStack stack, MultiBufferSource bufferSource,
                                     float x, float y, float z, float time, boolean isWorking,
                                     int packedLight, int packedOverlay) {
        List<ItemStack> items = machine.itemsForRenderer;
        if (items.isEmpty()) return;

        int count = items.size();
        float orbitRadius = 1.8F;
        float speedMultiplier = isWorking ? 2.5F : 1.0F;

        for (int i = 0; i < count; i++) {
            ItemStack itemStack = items.get(i);
            if (itemStack.isEmpty()) continue;

            float angle = (360.0F / count) * i + (time * speedMultiplier);
            float rad = angle * (float) Math.PI / 180.0F;

            float itemX = x + Mth.cos(rad) * orbitRadius;
            float itemZ = z + Mth.sin(rad) * orbitRadius;
            float itemY = y + 1.2F + Mth.sin(time * 0.08F + i) * 0.08F;

            stack.pushPose();
            stack.translate(itemX, itemY, itemZ);

            stack.mulPose(Axis.YP.rotationDegrees(time * 2.0F));
            stack.scale(0.55F, 0.55F, 0.55F);

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
    }

    @OnlyIn(Dist.CLIENT)
    private void renderEnergyBeams(InfusionAltar machine, PoseStack stack, MultiBufferSource bufferSource,
                                   float x, float y, float z, float time) {
        List<ItemStack> items = machine.itemsForRenderer;
        if (items.isEmpty()) return;

        VertexConsumer beamBuffer = bufferSource.getBuffer(RenderType.lightning());
        int count = items.size();
        float orbitRadius = 1.8F;

        float matrixY = y + 2.2F + (Mth.sin(time * 0.05F) * 0.12F);

        for (int i = 0; i < count; i++) {
            if (items.get(i).isEmpty()) continue;

            float angle = (360.0F / count) * i + (time * 2.5F);
            float rad = angle * (float) Math.PI / 180.0F;

            float startX = x + Mth.cos(rad) * orbitRadius;
            float startZ = z + Mth.sin(rad) * orbitRadius;
            float startY = y + 1.2F + Mth.sin(time * 0.08F + i) * 0.08F;

            drawLightningBeam(stack, beamBuffer, startX, startY, startZ, x, matrixY, z, time, i);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void drawLightningBeam(PoseStack stack, VertexConsumer buffer,
                                   float sx, float sy, float sz,
                                   float ex, float ey, float ez, float time, int index) {
        Matrix4f mat = stack.last().pose();

        int segments = 6;
        float curX = sx;
        float curY = sy;
        float curZ = sz;

        rand.setSeed((long) (index * 1000 + (time * 0.4F)));

        for (int i = 1; i <= segments; i++) {
            float pct = (float) i / segments;

            float targetX = sx + (ex - sx) * pct;
            float targetY = sy + (ey - sy) * pct;
            float targetZ = sz + (ez - sz) * pct;

            if (i < segments) {
                targetX += (rand.nextFloat() - 0.5F) * 0.15F;
                targetY += (rand.nextFloat() - 0.5F) * 0.15F;
                targetZ += (rand.nextFloat() - 0.5F) * 0.15F;
            }

            float thickness = 0.03F;
            buffer.vertex(mat, curX, curY, curZ).color(0.8F, 0.1F, 1.0F, 0.8F).uv(0, 0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
            buffer.vertex(mat, targetX, targetY, targetZ).color(0.8F, 0.1F, 1.0F, 0.8F).uv(1, 1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();

            buffer.vertex(mat, curX, curY + thickness, curZ).color(0.9F, 0.3F, 1.0F, 0.8F).uv(0, 0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(1, 0, 0).endVertex();
            buffer.vertex(mat, targetX, targetY + thickness, targetZ).color(0.9F, 0.3F, 1.0F, 0.8F).uv(1, 1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(1, 0, 0).endVertex();

            curX = targetX;
            curY = targetY;
            curZ = targetZ;
        }
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