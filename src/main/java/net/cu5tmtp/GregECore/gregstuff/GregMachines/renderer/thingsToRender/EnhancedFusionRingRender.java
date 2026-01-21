package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.GTRenderTypes;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.client.util.RenderBufferHelper;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.EnhancedFusionReactor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import static net.minecraft.util.FastColor.ARGB32.*;

public class EnhancedFusionRingRender extends DynamicRender<EnhancedFusionReactor, EnhancedFusionRingRender> {

    public static final Codec<EnhancedFusionRingRender> CODEC = Codec.unit(EnhancedFusionRingRender::new);
    public static final DynamicRenderType<EnhancedFusionReactor, EnhancedFusionRingRender> TYPE = new DynamicRenderType<>(EnhancedFusionRingRender.CODEC);

    public static final float FADEOUT = 60;
    protected float delta = 0;
    protected int lastColor = -1;

    //Modified render file from GTCEu fusion reactor arc renderer, thanks for making it public!
    public EnhancedFusionRingRender() {}

    @Override
    public DynamicRenderType<EnhancedFusionReactor, EnhancedFusionRingRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(EnhancedFusionReactor machine, Vec3 cameraPos) {
        return machine.getRecipeLogic().isWorking() || delta > 0;
    }

    @Override
    public void render(EnhancedFusionReactor machine, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        if (!machine.getRecipeLogic().isWorking() && delta <= 0) {
            return;
        }

        renderLightRing(machine, partialTick, poseStack, buffer.getBuffer(GTRenderTypes.getLightRing()));
    }

    @OnlyIn(Dist.CLIENT)
    private void renderLightRing(EnhancedFusionReactor machine, float partialTicks, PoseStack stack,
                                 VertexConsumer buffer) {
        var color = machine.getColor();
        var alpha = 1f;
        if (machine.getRecipeLogic().isWorking()) {
            lastColor = color;
            delta = FADEOUT;
        } else {
            alpha = delta / FADEOUT;
            lastColor = color(Mth.floor(alpha * 255), red(lastColor), green(lastColor), blue(lastColor));
            delta -= Minecraft.getInstance().getDeltaFrameTime();
        }

        final var lerpFactor = Math.abs((Math.abs(machine.getOffsetTimer() % 50) + partialTicks) - 25) / 25;
        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();

        var back = RelativeDirection.BACK.getRelative(front, upwards, flipped);
        var up = RelativeDirection.UP.getRelative(front, upwards, flipped);
        var axis = up.getAxis();

        var r = Mth.lerp(lerpFactor, red(lastColor), 255) / 255f;
        var g = Mth.lerp(lerpFactor, green(lastColor), 255) / 255f;
        var b = Mth.lerp(lerpFactor, blue(lastColor), 255) / 255f;

        float posX = (back.getStepX() * 6) + up.getStepX() + 0.5F;
        float posY = (back.getStepY() * 6) + up.getStepY() + 0.5F;
        float posZ = (back.getStepZ() * 6) + up.getStepZ() + 0.5F;

        RenderBufferHelper.renderRing(stack, buffer,
                posX, posY, posZ,
                4.2F, 0.2F, 10, 20,
                r, g, b, alpha, axis);
    }

    @Override
    public boolean shouldRenderOffScreen(EnhancedFusionReactor machine) {
        return machine.getRecipeLogic().isWorking() || delta > 0;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }

    @Override
    public AABB getRenderBoundingBox(EnhancedFusionReactor machine) {
        return new AABB(machine.getPos()).inflate(8.0D);
    }

    public static void init(){}
}