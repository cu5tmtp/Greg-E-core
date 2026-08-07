package net.cu5tmtp.GregECore.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.cu5tmtp.GregECore.wandOfPuppetry.AnimatedBlockEntity;

import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.MOD_ID;

public class ModEntity {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<EntityType<AnimatedBlockEntity>> ANIMATED_BLOCK = ENTITY_TYPES.register("animated_block",
            () -> EntityType.Builder.of(AnimatedBlockEntity::new, MobCategory.CREATURE)
                    .sized(1.0F, 1.0F)
                    .build("animated_block"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}