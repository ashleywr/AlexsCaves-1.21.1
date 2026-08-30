package com.github.alexmodguy.alexscaves.mixin.client;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 1.20.1 stored the tab's root as an AdvancementHolder field named rootAdvancement.
 * 1.21 replaced it with rootNode, an AdvancementNode; call holder() for the holder.
 */
@Mixin(AdvancementTab.class)
public interface AdvancementTabAccessor {

    @Accessor("rootNode")
    AdvancementNode getRootNode();
}
