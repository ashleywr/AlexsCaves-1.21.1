package com.telepathicgrunt.tact;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(TACT.MODID)
public class TACT {
    public static final String MODID = "tact";
    public static final String ALEXS_CAVES_MODID = "alexscaves";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static TagKey<Biome> MANUALLY_CARVED = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(MODID, "manually_carved"));
    public static final net.minecraft.tags.TagKey<net.minecraft.world.entity.EntityType<?>> PRIMITIVE_CLUB_STUN_IMMUNE =
            net.minecraft.tags.TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "primitive_club_stun_immune"));

    public TACT(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(DataAndResourcePacks::setupBuiltInDataPack);
        modEventBus.addListener(EntityAttributeModifications::AttributeModifications);
        modEventBus.addListener(Config::onLoad);

        IEventBus forgeBus = NeoForge.EVENT_BUS;
        forgeBus.addListener(EventPriority.LOWEST, ItemModifications::stunEffectAdjustment);
        forgeBus.addListener(CompendiumUnlock::playerLoggedIn);
        forgeBus.addListener(BlockModifications::burnTimeModifications);

        // STARTUP rather than COMMON: EntityAttributeModificationEvent fires before COMMON
        // configs are loaded, which is why the 1.20.1 version reflected into ConfigTracker
        // to force an early read. STARTUP configs are loaded during mod construction, so
        // the values are available by the time any event needs them.
        modContainer.registerConfig(ModConfig.Type.STARTUP, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BlockModifications.doModifications(event);
            ItemModifications.doItemAttributeModifications(event);
        });
    }
}
