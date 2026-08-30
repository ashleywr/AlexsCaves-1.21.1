package com.github.alexmodguy.alexscaves.mixin;

import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * SurfaceRules.Context#updateXZ and #updateY became protected in 1.21. The conversion
 * crucible drives a surface rule by hand to work out which blocks a biome would place,
 * which needs both.
 */
@Mixin(SurfaceRules.Context.class)
public interface SurfaceRulesContextAccessor {

    @Invoker("updateXZ")
    void ac_updateXZ(int blockX, int blockZ);

    @Invoker("updateY")
    void ac_updateY(int stoneDepthAbove, int stoneDepthBelow, int waterHeight, int blockX, int blockY, int blockZ);
}
