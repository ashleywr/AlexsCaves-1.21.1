package com.github.alexmodguy.alexscaves.mixin.client;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 1.20.1 exposed the widget's advancement as an AdvancementHolder field named
 * advancementHolder. 1.21 renamed it to advancementNode and changed the type to
 * AdvancementNode; call holder() for the holder.
 */
@Mixin(AdvancementWidget.class)
public interface AdvancementWidgetAccessor {

    @Accessor("progress")
    AdvancementProgress getProgress();

    @Accessor("parent")
    AdvancementWidget getParent();

    @Accessor("advancementNode")
    AdvancementNode getAdvancementNode();
}
