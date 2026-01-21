package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender.EnhancedFusionRingRender;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender.StarMaykrRender;
import net.minecraft.resources.ResourceLocation;


public class GregERenederRegistries {

    public static final ResourceLocation ENHANCED_FUSION_RING_ID =
            new ResourceLocation("gregecore", "enhanced_fusion_ring");

    public static final ResourceLocation STAR_MAYKR_STAR_ID =
            new ResourceLocation("gregecore", "star_maykr_star");

    public static DynamicRender<?, ?> createEnhancedFusionRingRender() {
        return new EnhancedFusionRingRender();
    }
    public static DynamicRender<?, ?> createStarMaykrRender() {
        return new StarMaykrRender();
    }

    public static void init() {

        DynamicRenderManager.register(ENHANCED_FUSION_RING_ID, EnhancedFusionRingRender.TYPE);
        DynamicRenderManager.register(STAR_MAYKR_STAR_ID, StarMaykrRender.TYPE);

        EnhancedFusionRingRender.init();
        StarMaykrRender.init();
    }
}