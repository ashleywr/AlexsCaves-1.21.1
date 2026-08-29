package com.telepathicgrunt.tact;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.AddPackFindersEvent;

public class DataAndResourcePacks {

    /**
     * 1.21 replaced the manual Pack.readMetaAndCreate plus PathPackResources dance with
     * AddPackFindersEvent.addPackFinders, which resolves the pack out of the mod jar for
     * us. The 1.20.1 version also reflected into ConfigTracker to force configs to load
     * early; that is no longer needed, and its targets have changed shape anyway
     * (configSets became a field, openConfig gained a third parameter), so it is gone.
     */
    static void setupBuiltInDataPack(final AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            if (readConfig(Config.APPLY_TAG_ADJUSTMENTS::get)) {
                event.addPackFinders(
                        ResourceLocation.fromNamespaceAndPath(TACT.MODID, "datapacks/apply_tag_adjustments"),
                        PackType.SERVER_DATA,
                        Component.literal("TACT - Adjusted Tags"),
                        PackSource.BUILT_IN,
                        true,
                        Pack.Position.BOTTOM);
            }

            if (ModList.get().isLoaded("spelunkery") && readConfig(Config.APPLY_SPELUNKERY_COMPAT_ADJUSTMENTS::get)) {
                event.addPackFinders(
                        ResourceLocation.fromNamespaceAndPath(TACT.MODID, "datapacks/spelunkery_compat_adjustments"),
                        PackType.SERVER_DATA,
                        Component.literal("TACT - Spelunkery Compat Adjustments"),
                        PackSource.BUILT_IN,
                        true,
                        Pack.Position.BOTTOM);
            }
        }
        else if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            // Note for the 1.21.1 port: the unofficial Alex's Caves port does not ship the
            // assets/minecraft/texts/end.txt override at all, so this pack has nothing to
            // undo there. It is kept so the option behaves the same if that ever returns.
            if (readConfig(Config.RESTORE_END_STORY::get)) {
                event.addPackFinders(
                        ResourceLocation.fromNamespaceAndPath(TACT.MODID, "resourcepacks/restore_end_story"),
                        PackType.CLIENT_RESOURCES,
                        Component.literal("TACT - Restore End Story"),
                        PackSource.BUILT_IN,
                        true,
                        Pack.Position.TOP);
            }
        }
    }

    /**
     * Pack finders can run before this mod's config has been read, in which case asking
     * for a value throws. Treat that as "not enabled" rather than crashing startup.
     */
    private static boolean readConfig(java.util.function.BooleanSupplier supplier) {
        try {
            return supplier.getAsBoolean();
        }
        catch (Exception e) {
            TACT.LOGGER.debug("TACT config not loaded yet when pack finders ran; skipping optional pack.");
            return false;
        }
    }
}
