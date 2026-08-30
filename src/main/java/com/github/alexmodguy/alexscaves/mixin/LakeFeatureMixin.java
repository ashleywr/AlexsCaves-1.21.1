package com.github.alexmodguy.alexscaves.mixin;

import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.feature.FeaturePositionValidator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LakeFeature.class)
public class LakeFeatureMixin {

    @Inject(
            method = {"Lnet/minecraft/world/level/levelgen/feature/LakeFeature;place(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    private void ac_place(FeaturePlaceContext context, CallbackInfoReturnable<Boolean> cir) {
        if (FeaturePositionValidator.isBiome(context, ACBiomeRegistry.ABYSSAL_CHASM)) {
            cir.cancel();
        }
    }

    /**
     * LakeFeature only reaches its ice-freezing loop for WATER lakes, and vanilla 1.21 ships
     * none -- lake_lava is lava -- so the loop is effectively dead upstream. primordial_caves_lake
     * is a water lake, which brings it back to life.
     *
     * The loop walks +0..15 on X and Z from the lake origin and calls the fuzzy
     * LevelReader#getBiome on each position. That applies a hashed offset on top, and
     * WorldGenRegion#getChunk throws "Requested chunk unavailable during world generation"
     * for anything outside the generating step's radius -- it ignores the requireChunk flag,
     * so getNoiseBiome is no safer.
     *
     * getUncachedNoiseBiome asks the chunk generator's biome source directly and never touches
     * a chunk, so it cannot throw. For deciding whether a lake surface freezes it is also the
     * more correct answer than a jittered lookup.
     */
    @Redirect(
            method = {"Lnet/minecraft/world/level/levelgen/feature/LakeFeature;place(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"),
            remap = true
    )
    private Holder<Biome> ac_getBiomeWithoutRequiringTheChunk(WorldGenLevel level, BlockPos pos) {
        return level.getUncachedNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()));
    }
}
