package net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.AscencionAltar;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.FornaxUniversi;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.thingsToRender.*;
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

    public static final ResourceLocation DYSONLAUNCHER_ID =
            new ResourceLocation("gregecore", "dysonlauncher");

    public static final ResourceLocation DYSONCOLLECTOR_ID =
            new ResourceLocation("gregecore", "dysoncollector");

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
    public static DynamicRender<?, ?> createDysonSwarmLauncherRender() {
        return new DysonSwarmLauncherRender();
    }

    public static DynamicRender<?, ?> createDysonSwarmEnergyCollectorRender() {
        return new DysonSwarmEnergyCollectorRender();
    }


    public static void init() {

        DynamicRenderManager.register(ENHANCED_FUSION_RING_ID, EnhancedFusionRingRender.TYPE);
        DynamicRenderManager.register(STAR_MAYKR_STAR_ID, StarMaykrRender.TYPE);
        DynamicRenderManager.register(FORNAX_UNIVERSI_ID, FornaxUniversiRender.TYPE);
        DynamicRenderManager.register(ASCENCION_ALTAR_ID, AscencionAltarRender.TYPE);
        DynamicRenderManager.register(DYSONLAUNCHER_ID, DysonSwarmLauncherRender.TYPE);
        DynamicRenderManager.register(DYSONCOLLECTOR_ID, DysonSwarmEnergyCollectorRender.TYPE);

        EnhancedFusionRingRender.init();
        StarMaykrRender.init();
        FornaxUniversiRender.init();
        AscencionAltarRender.init();
        DysonSwarmLauncherRender.init();
        DysonSwarmEnergyCollectorRender.init();
    }
}