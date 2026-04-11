package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.AscencionAltar;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.FornaxUniversi;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender.AscencionAltarRender;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender.EnhancedFusionRingRender;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender.FornaxUniversiRender;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender.StarMaykrRender;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public class GregERenederRegistries {

    public static final ResourceLocation ENHANCED_FUSION_RING_ID =
            new ResourceLocation("gregecore", "enhanced_fusion_ring");

    public static final ResourceLocation STAR_MAYKR_STAR_ID =
            new ResourceLocation("gregecore", "star_maykr_star");

    public static final ResourceLocation FORNAX_UNIVERSI_ID =
            new ResourceLocation("gregecore", "fornax_universi_blackhole");

    public static final ResourceLocation ASCENCION_ALTAR_ID =
            new ResourceLocation("gregecore", "ascencion_altar_hand");

    public static DynamicRender<?, ?> createEnhancedFusionRingRender() {
        return new EnhancedFusionRingRender();
    }
    public static DynamicRender<?, ?> createStarMaykrRender() {
        return new StarMaykrRender();
    }
    public static DynamicRender<?, ?> createFornaxUniversiRender() {
        return new FornaxUniversiRender();
    }
    public static DynamicRender<?, ?> createAscencionAltarRender() {
        return new AscencionAltarRender();
    }

    public static void init() {

        DynamicRenderManager.register(ENHANCED_FUSION_RING_ID, EnhancedFusionRingRender.TYPE);
        DynamicRenderManager.register(STAR_MAYKR_STAR_ID, StarMaykrRender.TYPE);
        DynamicRenderManager.register(FORNAX_UNIVERSI_ID, FornaxUniversiRender.TYPE);
        DynamicRenderManager.register(ASCENCION_ALTAR_ID, AscencionAltarRender.TYPE);

        EnhancedFusionRingRender.init();
        StarMaykrRender.init();
        FornaxUniversiRender.init();
        AscencionAltarRender.init();
    }
}