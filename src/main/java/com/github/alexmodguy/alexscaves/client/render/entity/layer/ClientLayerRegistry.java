package com.github.alexmodguy.alexscaves.client.render.entity.layer;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ClientLayerRegistry {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        List<EntityType<? extends LivingEntity>> entityTypes = ImmutableList.copyOf(
                BuiltInRegistries.ENTITY_TYPE.stream()
                        .filter(DefaultAttributes::hasSupplier)
                        .map(entityType -> (EntityType<? extends LivingEntity>) entityType)
                        .collect(Collectors.toList()));
        int incompatibleRenderers = 0;
        for (EntityType<? extends LivingEntity> entityType : entityTypes) {
            if (!addLayerIfApplicable(entityType, event)) {
                incompatibleRenderers++;
            }
        }
        if (incompatibleRenderers > 0) {
            AlexsCaves.LOGGER.info("Radiation glow layer skipped for {} entity types whose renderer is not a LivingEntityRenderer. This is expected for mods using GeckoLib or other custom renderers; enable debug logging for the full list.", incompatibleRenderers);
        }
        for (var skinModel : event.getSkins()) {
            var skinRenderer = event.getSkin(skinModel);
            if (skinRenderer instanceof LivingEntityRenderer livingRenderer) {
                livingRenderer.addLayer(new ACPotionEffectLayer(livingRenderer));
            }
        }
    }

    /**
     * @return true if the layer was applied, or if the type is deliberately excluded;
     *         false if the entity's renderer cannot accept a {@link LivingEntityRenderer} layer.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends LivingEntity> boolean addLayerIfApplicable(EntityType<T> entityType, EntityRenderersEvent.AddLayers event) {
        if (entityType == EntityType.ENDER_DRAGON) {
            return true;
        }
        EntityRenderer<?> renderer;
        try {
            renderer = event.getRenderer(entityType);
        } catch (Exception e) {
            AlexsCaves.LOGGER.warn("Could not look up the renderer for {} while adding the radiation glow layer: {}",
                    BuiltInRegistries.ENTITY_TYPE.getKey(entityType), e.toString());
            return false;
        }
        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new ACPotionEffectLayer(livingRenderer));
            return true;
        }
        if (renderer != null) {
            AlexsCaves.LOGGER.debug("Radiation glow layer skipped for {}: renderer {} does not extend LivingEntityRenderer.",
                    BuiltInRegistries.ENTITY_TYPE.getKey(entityType), renderer.getClass().getName());
        }
        return false;
    }
}
