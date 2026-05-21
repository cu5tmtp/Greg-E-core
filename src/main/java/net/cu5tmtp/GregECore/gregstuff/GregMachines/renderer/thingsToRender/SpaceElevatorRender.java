package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.SpaceElevator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("removal")
public class SpaceElevatorRender extends DynamicRender<SpaceElevator, SpaceElevatorRender> {

    public static final Codec<SpaceElevatorRender> CODEC = Codec.unit(new SpaceElevatorRender());
    public static final DynamicRenderType<SpaceElevator, SpaceElevatorRender> TYPE = new DynamicRenderType<>(SpaceElevatorRender.CODEC);

    private static TextureAtlasSprite CABLE_SPRITE_CACHE;

    private static final Map<BlockPos, Float> SMOOTH_HEIGHT = new ConcurrentHashMap<>();

    private static final String[][] CABIN_LAYOUT = {
            {
                    "     ",
                    "     ",
                    "  C  ",
                    "     ",
                    "     "
            },
            {
                    "     ",
                    "     ",
                    "  C  ",
                    "     ",
                    "     "
            },
            {
                    "     ",
                    "  A  ",
                    " AAA ",
                    "  A  ",
                    "     ",
            },
            {
                    "     ",
                    "  A  ",
                    " AAA ",
                    "  A  ",
                    "     ",
            },
            {
                    "     ",
                    " AAA ",
                    " AAA ",
                    " AAA ",
                    "     ",
            },
            {
                    " AAA ",
                    "A   A",
                    "A   A",
                    "A   A",
                    " AAA ",
            },
            {
                    " ABA ",
                    "A   A",
                    "B   B",
                    "A   A",
                    " ABA ",
            },
            {
                    " ABA ",
                    "A   A",
                    "B   B",
                    "A   A",
                    " ABA ",
            },
            {
                    " AAA ",
                    "AAAAA",
                    "AAAAA",
                    "AAAAA",
                    " AAA ",
            },
            {
                    "     ",
                    " AAA ",
                    " AAA ",
                    " AAA ",
                    "     ",
            },
            {
                    "     ",
                    "  A  ",
                    " AAA ",
                    "  A  ",
                    "     ",
            },
            {
                    "     ",
                    "  A  ",
                    " AAA ",
                    "  A  ",
                    "     ",
            },
            {
                    "     ",
                    "     ",
                    "  C  ",
                    "     ",
                    "     "
            },
            {
                    "     ",
                    "     ",
                    "  C  ",
                    "     ",
                    "     "
            }
    };

    private static BlockState getModBlockState(String registryName, BlockState fallback) {
        try {
            ResourceLocation location = ResourceLocation.parse(registryName);
            Block block = ForgeRegistries.BLOCKS.getValue(location);
            if (block != null && block != Blocks.AIR) {
                return block.defaultBlockState();
            }
        } catch (Exception ignored) {
        }
        return fallback;
    }

    private static BlockState getBlockStateForChar(char c) {
        return switch (c) {
            case 'A' -> getModBlockState("gtceu:atomic_casing", Blocks.SMOOTH_STONE.defaultBlockState());
            case 'B' -> Blocks.GLASS.defaultBlockState();
            case 'C' -> getModBlockState("gtceu:tungstensteel_gearbox", Blocks.SMOOTH_STONE.defaultBlockState());
            default -> null;
        };
    }

    public SpaceElevatorRender() {}

    @Override
    public DynamicRenderType<SpaceElevator, SpaceElevatorRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(SpaceElevator machine, Vec3 cameraPos) {
        return machine.isFormed();
    }

    @Override
    public void render(SpaceElevator machine, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        if (CABLE_SPRITE_CACHE == null) {
            CABLE_SPRITE_CACHE = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation("minecraft:block/iron_block"));
        }

        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var back = RelativeDirection.BACK.getRelative(front, upwards, flipped);
        var up = RelativeDirection.UP.getRelative(front, upwards, flipped);

        float posX = (back.getStepX() * 15.0F) + (up.getStepX() * 1.0F) + 0.5F;
        float posY = (back.getStepY() * 15.0F) + (up.getStepY() * 1.0F) + 0.5F;
        float posZ = (back.getStepZ() * 15.0F) + (up.getStepZ() * 1.0F) + 0.5F;

        renderElevatorCableAndCrate(machine, partialTick, poseStack, buffer, posX, posY, posZ, packedLight, packedOverlay);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderElevatorCableAndCrate(SpaceElevator machine, float partialTick, PoseStack stack,
                                             MultiBufferSource bufferSource, float x, float y, float z,
                                             int packedLight, int packedOverlay) {

        stack.pushPose();
        stack.translate(x, y, z);

        float cableRadius = 0.1F;
        VertexConsumer cableBuffer = bufferSource.getBuffer(RenderType.translucent());

        BlockPos pos = machine.getPos();
        float currentHeight = SMOOTH_HEIGHT.getOrDefault(pos, 0.0F);
        var recipeLogic = machine.getRecipeLogic();

        float visualProgress = 0.0F;

        if (recipeLogic.isWorking() && recipeLogic.getMaxProgress() > 0) {
            float serverProgress = (float) recipeLogic.getProgress() / (float) recipeLogic.getMaxProgress();
            float tickIncrement = 1.0F / (float) recipeLogic.getMaxProgress();
            float targetProgress = Mth.clamp(serverProgress + (partialTick * tickIncrement), 0.0F, 1.0F);

            float targetHeight = Mth.sin(targetProgress * (float) Math.PI);

            if (Math.abs(currentHeight - targetHeight) > 0.8F) {
                currentHeight = targetHeight;
            } else {
                currentHeight = currentHeight + (targetHeight - currentHeight) * 0.15F;
            }
            SMOOTH_HEIGHT.put(pos, currentHeight);
            visualProgress = currentHeight;
        } else {
            if (currentHeight > 0.0F) {
                currentHeight -= 0.02F * partialTick;
                if (currentHeight < 0.0F) {
                    currentHeight = 0.0F;
                    SMOOTH_HEIGHT.remove(pos);
                } else {
                    SMOOTH_HEIGHT.put(pos, currentHeight);
                }
            } else {
                SMOOTH_HEIGHT.remove(pos);
            }
            visualProgress = currentHeight;
        }

        float crateY = 3.5F + (visualProgress * 145.0F);

        int lightAtHeight = packedLight;
        if (machine.getLevel() != null) {
            BlockPos cabinPos = machine.getPos().above(Mth.clamp((int) crateY, 0, 255));
            lightAtHeight = LevelRenderer.getLightColor(machine.getLevel(), cabinPos);

            int blockLight = (lightAtHeight & 0xF0) >> 4;
            int skyLight = (lightAtHeight >> 16) & 0xF0;
            if (blockLight < 11) blockLight = 11;
            if (skyLight < 11) skyLight = 11;
            lightAtHeight = (skyLight << 16) | (blockLight << 4);
        }

        renderBox(stack, cableBuffer,
                -cableRadius, 0.0F, -cableRadius,
                cableRadius, 120.0F, cableRadius,
                0.8F, 0.8F, 0.8F, 1.0F, 1.0F, CABLE_SPRITE_CACHE, lightAtHeight);

        int segments = 15;
        float fadeStart = 120.0F;
        float fadeEnd = 150.0F;
        float segmentHeight = (fadeEnd - fadeStart) / segments;

        for (int i = 0; i < segments; i++) {
            float yStart = fadeStart + (i * segmentHeight);
            float yEnd = yStart + segmentHeight;

            float progressStart = (yStart - fadeStart) / (fadeEnd - fadeStart);
            float progressEnd = (yEnd - fadeStart) / (fadeEnd - fadeStart);

            float alphaStart = Mth.cos(progressStart * (float) Math.PI / 2.0F);
            float alphaEnd = Mth.cos(progressEnd * (float) Math.PI / 2.0F);

            renderBox(stack, cableBuffer,
                    -cableRadius, yStart, -cableRadius,
                    cableRadius, yEnd, cableRadius,
                    0.8F, 0.8F, 0.8F, alphaStart, alphaEnd, CABLE_SPRITE_CACHE, lightAtHeight);
        }

        float crateAlpha = 1.0F;
        if (crateY > 120.0F) {
            crateAlpha = Mth.clamp((140.0F - crateY) / 30.0F, 0.0F, 1.0F);
        }

        if(crateY > 142){
            stack.popPose();
            return;
        }

        stack.pushPose();
        stack.translate(0.0F, crateY, 0.0F);

        final float finalAlpha = crateAlpha;
        MultiBufferSource alphaBufferSource = renderType -> {
            VertexConsumer original = bufferSource.getBuffer(renderType);
            return new AlphaOverrideVertexConsumer(original, finalAlpha);
        };

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

        for (int yOffset = 0; yOffset < CABIN_LAYOUT.length; yOffset++) {
            for (int zOffset = 0; zOffset < 5; zOffset++) {
                for (int xOffset = 0; xOffset < 5; xOffset++) {
                    char blockChar = CABIN_LAYOUT[yOffset][zOffset].charAt(xOffset);
                    BlockState state = getBlockStateForChar(blockChar);

                    if (state != null) {
                        stack.pushPose();

                        float offsetX = xOffset - 2.5F;
                        float offsetY = yOffset - 1.0F;
                        float offsetZ = zOffset - 2.5F;

                        stack.translate(offsetX, offsetY, offsetZ);

                        if(crateY < 120) {
                            dispatcher.renderSingleBlock(
                                    state,
                                    stack,
                                    alphaBufferSource,
                                    lightAtHeight,
                                    packedOverlay,
                                    ModelData.EMPTY,
                                    null
                            );
                        } else {
                            dispatcher.renderSingleBlock(
                                    state,
                                    stack,
                                    alphaBufferSource,
                                    lightAtHeight,
                                    packedOverlay,
                                    ModelData.EMPTY,
                                    RenderType.translucent()
                            );
                        }

                        stack.popPose();
                    }
                }
            }
        }

        stack.popPose();
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

        //Down
        addBoxVertex(buffer, mat, normalMat, minX, minY, minZ, u0, v0, r, g, b, bottomAlpha, 0, -1, 0, light);
        addBoxVertex(buffer, mat, normalMat, maxX, minY, minZ, u1, v0, r, g, b, bottomAlpha, 0, -1, 0, light);
        addBoxVertex(buffer, mat, normalMat, maxX, minY, maxZ, u1, v1, r, g, b, bottomAlpha, 0, -1, 0, light);
        addBoxVertex(buffer, mat, normalMat, minX, minY, maxZ, u0, v1, r, g, b, bottomAlpha, 0, -1, 0, light);

        //Up
        addBoxVertex(buffer, mat, normalMat, minX, maxY, maxZ, u0, v1, r, g, b, topAlpha, 0, 1, 0, light);
        addBoxVertex(buffer, mat, normalMat, maxX, maxY, maxZ, u1, v1, r, g, b, topAlpha, 0, 1, 0, light);
        addBoxVertex(buffer, mat, normalMat, maxX, maxY, minZ, u1, v0, r, g, b, topAlpha, 0, 1, 0, light);
        addBoxVertex(buffer, mat, normalMat, minX, maxY, minZ, u0, v0, r, g, b, topAlpha, 0, 1, 0, light);

        //North
        addBoxVertex(buffer, mat, normalMat, minX, maxY, minZ, u0, v1, r, g, b, topAlpha, 0, 0, -1, light);
        addBoxVertex(buffer, mat, normalMat, maxX, maxY, minZ, u1, v1, r, g, b, topAlpha, 0, 0, -1, light);
        addBoxVertex(buffer, mat, normalMat, maxX, minY, minZ, u1, v0, r, g, b, bottomAlpha, 0, 0, -1, light);
        addBoxVertex(buffer, mat, normalMat, minX, minY, minZ, u0, v0, r, g, b, bottomAlpha, 0, 0, -1, light);

        //South
        addBoxVertex(buffer, mat, normalMat, minX, minY, maxZ, u0, v0, r, g, b, bottomAlpha, 0, 0, 1, light);
        addBoxVertex(buffer, mat, normalMat, maxX, minY, maxZ, u1, v0, r, g, b, bottomAlpha, 0, 0, 1, light);
        addBoxVertex(buffer, mat, normalMat, maxX, maxY, maxZ, u1, v1, r, g, b, topAlpha, 0, 0, 1, light);
        addBoxVertex(buffer, mat, normalMat, minX, maxY, maxZ, u0, v1, r, g, b, topAlpha, 0, 0, 1, light);

        //West
        addBoxVertex(buffer, mat, normalMat, minX, maxY, maxZ, u0, v1, r, g, b, topAlpha, -1, 0, 0, light);
        addBoxVertex(buffer, mat, normalMat, minX, maxY, minZ, u1, v1, r, g, b, topAlpha, -1, 0, 0, light);
        addBoxVertex(buffer, mat, normalMat, minX, minY, minZ, u1, v0, r, g, b, bottomAlpha, -1, 0, 0, light);
        addBoxVertex(buffer, mat, normalMat, minX, minY, maxZ, u0, v0, r, g, b, bottomAlpha, -1, 0, 0, light);

        //East
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
    public boolean shouldRenderOffScreen(SpaceElevator machine) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public AABB getRenderBoundingBox(SpaceElevator machine) {
        return new AABB(machine.getPos()).expandTowards(0.0D, 160.0D, 0.0D).inflate(15.0D, 0.0D, 15.0D);
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