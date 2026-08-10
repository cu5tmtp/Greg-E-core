package net.cu5tmtp.GregECore.wandOfPuppetry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class AnimatedBlockModel<T extends AnimatedBlockEntity> extends EntityModel<T> {
    private final ModelPart root;
    private final ModelPart[] legs = new ModelPart[8];

    public AnimatedBlockModel(ModelPart root) {
        this.root = root;
        for (int i = 0; i < 8; i++) {
            this.legs[i] = root.getChild("leg" + i);
        }
    }

    public ModelPart[] getLegs() {
        return this.legs;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        for (int i = 0; i < 8; i++) {
            boolean isLeft = (i % 2 == 0);
            int pairIndex = i / 2;

            float zOffset = -3.5f + (pairIndex * 2.3f);
            float xOffset = isLeft ? -5.5f : 5.5f;

            partdefinition.addOrReplaceChild("leg" + i, CubeListBuilder.create(),
                    PartPose.offset(xOffset, 6.0f, zOffset));
        }

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        for (int i = 0; i < 8; i++) {
            boolean isLeft = (i % 2 == 0);
            int pairIndex = i / 2;

            float phaseOffset = (isLeft ? 0 : (float)Math.PI) + (pairIndex * (float)Math.PI / 2.0f);
            float phase = limbSwing * 2.5f + phaseOffset;

            float sweep = Mth.cos(phase) * limbSwingAmount * 0.4f;
            float fanAngle = (1.5f - pairIndex) * 0.6f;

            float baseYaw = isLeft ? -fanAngle : fanAngle;
            baseYaw += (isLeft ? -sweep : sweep) * 0.7f;

            this.legs[i].yRot = baseYaw;

            float lift = Math.max(0, Mth.sin(phase)) * limbSwingAmount * 0.5f;
            this.legs[i].zRot = isLeft ? (0.2f - lift) : (-0.2f + lift);
        }
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {

    }
}