package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.endgame.DeepSpaceExplorer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("removal")
public class DeepSpaceExplorerRender extends DynamicRender<DeepSpaceExplorer, DeepSpaceExplorerRender> {

    public static final Codec<DeepSpaceExplorerRender> CODEC = Codec.unit(new DeepSpaceExplorerRender());
    public static final DynamicRenderType<DeepSpaceExplorer, DeepSpaceExplorerRender> TYPE = new DynamicRenderType<>(DeepSpaceExplorerRender.CODEC);

    private static TextureAtlasSprite BASE_TEXTURE_CACHE;

    private static final Map<BlockPos, Float> SMOOTH_PROGRESS = new ConcurrentHashMap<>();

    public DeepSpaceExplorerRender() {}

    @Override
    public DynamicRenderType<DeepSpaceExplorer, DeepSpaceExplorerRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(DeepSpaceExplorer machine, Vec3 cameraPos) {
        return machine.isFormed();
    }

    private static ItemStack getModRocketItem(String registryName) {
        try {
            ResourceLocation location = ResourceLocation.parse(registryName);
            net.minecraft.world.item.Item item = ForgeRegistries.ITEMS.getValue(location);
            if (item != null && item != net.minecraft.world.item.Items.AIR) {
                return new ItemStack(item);
            }
        } catch (Exception ignored) {
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void render(DeepSpaceExplorer machine, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        if (BASE_TEXTURE_CACHE == null) {
            BASE_TEXTURE_CACHE = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation("minecraft:block/iron_block"));
        }

        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var back = RelativeDirection.BACK.getRelative(front, upwards, flipped);

        float posX = (back.getStepX() * 2.0F) + 0.5F;
        float posY = (back.getStepY() * 2.0F)  + 0.5F;
        float posZ = (back.getStepZ() * 2.0F) + 0.5F;

        renderRocketAndSatelliteCycle(machine, partialTick, poseStack, buffer, posX, posY, posZ, packedLight, packedOverlay);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderRocketAndSatelliteCycle(DeepSpaceExplorer machine, float partialTick, PoseStack stack,
                                               MultiBufferSource bufferSource, float x, float y, float z,
                                               int packedLight, int packedOverlay) {
        if (machine.getLevel() == null) return;

        BlockPos pos = machine.getPos();
        float currentProgress = SMOOTH_PROGRESS.getOrDefault(pos, 0.0F);
        var recipeLogic = machine.getRecipeLogic();

        float visualProgress = 0.0F;

        int lightAtHeight = packedLight;
        int blockLight = (lightAtHeight & 0xF0) >> 4;
        int skyLight = (lightAtHeight >> 16) & 0xF0;
        if (blockLight < 12) blockLight = 12;
        if (skyLight < 12) skyLight = 12;
        lightAtHeight = (skyLight << 16) | (blockLight << 4);

        if (recipeLogic.isWorking() && recipeLogic.getMaxProgress() > 0) {
            float serverProgress = (float) recipeLogic.getProgress() / (float) recipeLogic.getMaxProgress();
            float tickIncrement = 1.0F / (float) recipeLogic.getMaxProgress();
            float targetProgress = Mth.clamp(serverProgress + (partialTick * tickIncrement), 0.0F, 1.0F);

            if (Math.abs(currentProgress - targetProgress) > 0.8F) {
                currentProgress = targetProgress;
            } else {
                currentProgress = currentProgress + (targetProgress - currentProgress) * 0.15F;
            }
            SMOOTH_PROGRESS.put(pos, currentProgress);
            visualProgress = currentProgress;
        } else {
            if (currentProgress > 0.0F) {
                currentProgress -= 0.05F * partialTick;
                if (currentProgress < 0.0F) {
                    currentProgress = 0.0F;
                    SMOOTH_PROGRESS.remove(pos);
                } else {
                    SMOOTH_PROGRESS.put(pos, currentProgress);
                }
            } else {
                SMOOTH_PROGRESS.remove(pos);
            }
            visualProgress = currentProgress;
        }

        stack.pushPose();
        stack.translate(x, y, z);

        VertexConsumer transBuffer = bufferSource.getBuffer(RenderType.translucent());

        if (visualProgress <= 0.5F) {
            float phaseT = visualProgress * 4.0F;
            float rocketY = 0.0F;
            float rocketAlpha = 1.0F;
            boolean showFlame = false;

            if (phaseT < 0.15F) {
                rocketAlpha = phaseT / 0.15F;
                rocketY = 0.0F;
            } else if (phaseT < 0.85F) {
                float t = (phaseT - 0.15F) / 0.70F;
                rocketY = 130.0F * (t * t);
                rocketAlpha = 1.0F;
                showFlame = true;
            } else {
                float t = (phaseT - 0.85F) / 0.15F;
                rocketY = 130.0F + (t * 15.0F);
                rocketAlpha = Mth.clamp(1.0F - t, 0.0F, 1.0F);
                showFlame = true;
            }

            stack.pushPose();
            stack.translate(0.0F, rocketY, 0.0F);

            final float finalAlpha = rocketAlpha;
            MultiBufferSource alphaBufferSource = renderType -> {
                VertexConsumer original = bufferSource.getBuffer(renderType);
                return new AlphaOverrideVertexConsumer(original, finalAlpha);
            };

            ItemStack rocketStack = getModRocketItem("ad_extendra:tier_11_rocket");

            if (rocketStack.isEmpty()) {
                renderBox(stack, transBuffer, -0.4F, 0.0F, -0.4F, 0.4F, 3.2F, 0.4F, 0.9F, 0.9F, 0.9F, rocketAlpha, rocketAlpha, BASE_TEXTURE_CACHE, lightAtHeight);

                renderBox(stack, transBuffer, -0.3F, 3.2F, -0.3F, 0.3F, 4.0F, 0.3F, 0.9F, 0.15F, 0.15F, rocketAlpha, rocketAlpha, BASE_TEXTURE_CACHE, lightAtHeight);

                renderBox(stack, transBuffer, -0.41F, 1.8F, -0.41F, 0.41F, 2.2F, 0.41F, 0.9F, 0.15F, 0.15F, rocketAlpha, rocketAlpha, BASE_TEXTURE_CACHE, lightAtHeight);

                renderBox(stack, transBuffer, -0.7F, 0.0F, -0.1F, -0.4F, 1.0F, 0.1F, 0.9F, 0.15F, 0.15F, rocketAlpha, rocketAlpha, BASE_TEXTURE_CACHE, lightAtHeight);
                renderBox(stack, transBuffer, 0.4F, 0.0F, -0.1F, 0.7F, 1.0F, 0.1F, 0.9F, 0.15F, 0.15F, rocketAlpha, rocketAlpha, BASE_TEXTURE_CACHE, lightAtHeight);
                renderBox(stack, transBuffer, -0.1F, 0.0F, -0.7F, 0.1F, 1.0F, -0.4F, 0.9F, 0.15F, 0.15F, rocketAlpha, rocketAlpha, BASE_TEXTURE_CACHE, lightAtHeight);
                renderBox(stack, transBuffer, -0.1F, 0.0F, 0.4F, 0.1F, 1.0F, 0.7F, 0.9F, 0.15F, 0.15F, rocketAlpha, rocketAlpha, BASE_TEXTURE_CACHE, lightAtHeight);
            } else {
                stack.pushPose();
                stack.translate(0.0F, 1.5F, 0.0F);
                stack.scale(3.0F, 3.0F, 3.0F);

                Minecraft.getInstance().getItemRenderer().renderStatic(
                        rocketStack,
                        ItemDisplayContext.FIXED,
                        lightAtHeight,
                        packedOverlay,
                        stack,
                        alphaBufferSource,
                        machine.getLevel(),
                        0
                );
                stack.popPose();
            }

            if (showFlame) {
                float flicker = 0.85F + 0.3F * (float) Math.sin((machine.getLevel().getGameTime() + partialTick) * 2.5F);
                renderBox(stack, transBuffer,
                        -0.2F * flicker, -1.2F * flicker, -0.2F * flicker,
                        0.2F * flicker, 0.0F, 0.2F * flicker,
                        1.0F, 0.9F, 0.3F, rocketAlpha, 0.0F, BASE_TEXTURE_CACHE, 15728880);

                renderBox(stack, transBuffer,
                        -0.38F * flicker, -2.4F * flicker, -0.38F * flicker,
                        0.38F * flicker, 0.0F, 0.38F * flicker,
                        1.0F, 0.3F, 0.0F, rocketAlpha, 0.0F, BASE_TEXTURE_CACHE, 15728880);
            }

            stack.popPose();

        } else if(visualProgress >= 0.75F){
            float phaseT = (visualProgress - 0.75F) * 4.0F;
            float satY = 0.0F;
            float satAlpha = 1.0F;

            if (phaseT < 0.15F) {
                satAlpha = phaseT / 0.15F;
                satY = 130.0F;
            } else if (phaseT < 0.80F) {
                float t = (phaseT - 0.15F) / 0.65F;
                float deceleratingMultiplier = 1.0F - t;
                satY = 130.0F * (deceleratingMultiplier * deceleratingMultiplier);
                satAlpha = 1.0F;
            } else {
                satY = 0.0F;
                float t = (phaseT - 0.80F) / 0.20F;
                satAlpha = Mth.clamp(1.0F - t, 0.0F, 1.0F);
            }

            stack.pushPose();
            stack.translate(0.0F, satY + 1.0F, 0.0F);

            float rotationAngle = (machine.getLevel().getGameTime() + partialTick) * 2.0F;
            stack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotationAngle));

            renderBox(stack, transBuffer,
                    -0.35F, -0.35F, -0.35F,
                    0.35F, 0.35F, 0.35F,
                    1.0F, 0.84F, 0.22F, satAlpha, satAlpha, BASE_TEXTURE_CACHE, lightAtHeight);

            renderBox(stack, transBuffer,
                    -0.15F, -0.45F, -0.15F,
                    0.15F, 0.45F, 0.15F,
                    0.1F, 0.4F, 0.8F, satAlpha, satAlpha, BASE_TEXTURE_CACHE, lightAtHeight);

            float antennaLen = 1.2F;
            float antThickness = 0.025F;

            for (int i = 0; i < 4; i++) {
                stack.pushPose();
                stack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(i * 90.0F));
                stack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(45.0F));

                renderBox(stack, transBuffer,
                        -antThickness, 0.0F, -antThickness,
                        antThickness, antennaLen, antThickness,
                        0.75F, 0.75F, 0.75F, satAlpha, satAlpha, BASE_TEXTURE_CACHE, lightAtHeight);

                renderBox(stack, transBuffer,
                        -0.05F, antennaLen, -0.05F,
                        0.05F, antennaLen + 0.1F, 0.05F,
                        0.9F, 0.1F, 0.1F, satAlpha, satAlpha, BASE_TEXTURE_CACHE, lightAtHeight);

                stack.popPose();
            }

            stack.popPose();
        }

        stack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderBox(PoseStack poseStack, VertexConsumer buffer,
                           float minX, float minY, float minZ,
                           float maxX, float maxY, float maxZ,
                           float r, float g, float b, float bottomAlpha, float topAlpha,
                           TextureAtlasSprite sprite, int light) {
        Matrix4f mat = poseStack.last().pose();
        Matrix3f normalMat = poseStack.last().normal();

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        // Down face
        addBoxVertex(buffer, mat, normalMat, minX, minY, minZ, u0, v0, r, g, b, bottomAlpha, 0, -1, 0, light);
        addBoxVertex(buffer, mat, normalMat, maxX, minY, minZ, u1, v0, r, g, b, bottomAlpha, 0, -1, 0, light);
        addBoxVertex(buffer, mat, normalMat, maxX, minY, maxZ, u1, v1, r, g, b, bottomAlpha, 0, -1, 0, light);
        addBoxVertex(buffer, mat, normalMat, minX, minY, maxZ, u0, v1, r, g, b, bottomAlpha, 0, -1, 0, light);

        // Up face
        addBoxVertex(buffer, mat, normalMat, minX, maxY, maxZ, u0, v1, r, g, b, topAlpha, 0, 1, 0, light);
        addBoxVertex(buffer, mat, normalMat, maxX, maxY, maxZ, u1, v1, r, g, b, topAlpha, 0, 1, 0, light);
        addBoxVertex(buffer, mat, normalMat, maxX, maxY, minZ, u1, v0, r, g, b, topAlpha, 0, 1, 0, light);
        addBoxVertex(buffer, mat, normalMat, minX, maxY, minZ, u0, v0, r, g, b, topAlpha, 0, 1, 0, light);

        // North face
        addBoxVertex(buffer, mat, normalMat, minX, maxY, minZ, u0, v1, r, g, b, topAlpha, 0, 0, -1, light);
        addBoxVertex(buffer, mat, normalMat, maxX, maxY, minZ, u1, v1, r, g, b, topAlpha, 0, 0, -1, light);
        addBoxVertex(buffer, mat, normalMat, maxX, minY, minZ, u1, v0, r, g, b, bottomAlpha, 0, 0, -1, light);
        addBoxVertex(buffer, mat, normalMat, minX, minY, minZ, u0, v0, r, g, b, bottomAlpha, 0, 0, -1, light);

        // South face
        addBoxVertex(buffer, mat, normalMat, minX, minY, maxZ, u0, v0, r, g, b, bottomAlpha, 0, 0, 1, light);
        addBoxVertex(buffer, mat, normalMat, maxX, minY, maxZ, u1, v0, r, g, b, bottomAlpha, 0, 0, 1, light);
        addBoxVertex(buffer, mat, normalMat, maxX, maxY, maxZ, u1, v1, r, g, b, topAlpha, 0, 0, 1, light);
        addBoxVertex(buffer, mat, normalMat, minX, maxY, maxZ, u0, v1, r, g, b, topAlpha, 0, 0, 1, light);

        // West face
        addBoxVertex(buffer, mat, normalMat, minX, maxY, maxZ, u0, v1, r, g, b, topAlpha, -1, 0, 0, light);
        addBoxVertex(buffer, mat, normalMat, minX, maxY, minZ, u1, v1, r, g, b, topAlpha, -1, 0, 0, light);
        addBoxVertex(buffer, mat, normalMat, minX, minY, minZ, u1, v0, r, g, b, bottomAlpha, -1, 0, 0, light);
        addBoxVertex(buffer, mat, normalMat, minX, minY, maxZ, u0, v0, r, g, b, bottomAlpha, -1, 0, 0, light);

        // East face
        addBoxVertex(buffer, mat, normalMat, maxX, minY, maxZ, u0, v0, r, g, b, bottomAlpha, 1, 0, 0, light);
        addBoxVertex(buffer, mat, normalMat, maxX, minY, minZ, u1, v0, r, g, b, bottomAlpha, 1, 0, 0, light);
        addBoxVertex(buffer, mat, normalMat, maxX, maxY, minZ, u1, v1, r, g, b, topAlpha, 1, 0, 0, light);
        addBoxVertex(buffer, mat, normalMat, maxX, maxY, maxZ, u0, v1, r, g, b, topAlpha, 1, 0, 0, light);
    }

    private void addBoxVertex(VertexConsumer buffer, Matrix4f mat, Matrix3f normalMat,
                              float x, float y, float z, float u, float v,
                              float r, float g, float b, float a,
                              float nx, float ny, float nz, int light) {
        buffer.vertex(mat, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normalMat, nx, ny, nz)
                .endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(DeepSpaceExplorer machine) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public AABB getRenderBoundingBox(DeepSpaceExplorer machine) {
        return new AABB(machine.getPos()).expandTowards(0.0D, 150.0D, 0.0D).inflate(10.0D, 0.0D, 10.0D);
    }

    public static void init(){}

    @OnlyIn(Dist.CLIENT)
    private static class AlphaOverrideVertexConsumer implements VertexConsumer {
        private final VertexConsumer parent;
        private final float alphaMultiplier;

        public AlphaOverrideVertexConsumer(VertexConsumer parent, float alphaMultiplier) {
            this.parent = parent;
            this.alphaMultiplier = alphaMultiplier;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            parent.vertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer color(int r, int g, int b, int a) {
            parent.color(r, g, b, (int) (a * alphaMultiplier));
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            parent.uv(u, v);
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            parent.overlayCoords(u, v);
            return this;
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            parent.uv2(u, v);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            parent.normal(x, y, z);
            return this;
        }

        @Override
        public void endVertex() {
            parent.endVertex();
        }

        @Override
        public void defaultColor(int r, int g, int b, int a) {
            parent.defaultColor(r, g, b, (int) (a * alphaMultiplier));
        }

        @Override
        public void unsetDefaultColor() {
            parent.unsetDefaultColor();
        }
    }
}