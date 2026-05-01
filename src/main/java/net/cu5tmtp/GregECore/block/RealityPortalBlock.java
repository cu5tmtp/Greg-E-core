package net.cu5tmtp.GregECore.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("removal")
public class RealityPortalBlock extends Block {

    public RealityPortalBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (!pLevel.isClientSide && pEntity instanceof ServerPlayer player) {

            ResourceLocation targetDimRes = new ResourceLocation("gregecore", "fracture_dimension");
            ResourceKey<Level> targetDimKey = ResourceKey.create(Registries.DIMENSION, targetDimRes);
            ServerLevel destinationLevel = player.server.getLevel(targetDimKey);

            if (destinationLevel != null) {
                player.teleportTo(destinationLevel, pPos.getX() + 0.5D, pPos.getY(), pPos.getZ() + 0.5D, player.getYRot(), player.getXRot());
            }
        }
    }
}