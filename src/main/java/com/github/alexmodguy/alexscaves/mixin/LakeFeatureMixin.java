package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.feature.FeaturePositionValidator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LakeFeature.class)
public class LakeFeatureMixin {

    @Inject(
            method = "place(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ac_place(FeaturePlaceContext context, CallbackInfoReturnable<Boolean> cir) {
        if (FeaturePositionValidator.isBiome(context, ACBiomeRegistry.ABYSSAL_CHASM)) {
            cir.setReturnValue(false);
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
     *
     * Only WorldGenRegion has the bounded-chunk restriction. Any other WorldGenLevel -- a
     * ServerLevel placing this feature outside the generation pipeline, for instance -- can
     * answer getBiome safely, so it keeps vanilla behaviour.
     */
    @Redirect(
            method = "place(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/WorldGenLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"
            )
    )
    private Holder<Biome> ac_getBiomeWithoutRequiringTheChunk(WorldGenLevel level, BlockPos pos) {
        if (level instanceof WorldGenRegion) {
            return level.getUncachedNoiseBiome(
                    QuartPos.fromBlock(pos.getX()),
                    QuartPos.fromBlock(pos.getY()),
                    QuartPos.fromBlock(pos.getZ())
            );
        }
        return level.getBiome(pos);
    }
}
