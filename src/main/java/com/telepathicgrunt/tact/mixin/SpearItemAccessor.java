package com.telepathicgrunt.tact.mixin;

import com.github.alexmodguy.alexscaves.server.item.SpearItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * In 1.20.1 SpearItem cached a Multimap of default attribute modifiers that could be
 * swapped wholesale. In 1.21 it builds them on demand in getAttributeModifiers from a
 * single final damage field, so setting that field is both simpler and less brittle.
 */
@Mixin(SpearItem.class)
public interface SpearItemAccessor {
    @Mutable
    @Accessor("damage")
    void setDamage(double damage);
}
