package com.github.alexmodguy.alexscaves.server.entity.util;

import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import javax.annotation.Nullable;
import java.util.Optional;

public class VillagerUndergroundCabinMapTrade implements VillagerTrades.ItemListing {
    private final int emeraldCost;
    private final int maxUses;
    private final int villagerXp;

    public VillagerUndergroundCabinMapTrade(int emeraldCost, int maxUses, int villagerXp) {
        this.emeraldCost = emeraldCost;
        this.maxUses = maxUses;
        this.villagerXp = villagerXp;
    }

    @Nullable
    public MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
        if (!(entity.level() instanceof ServerLevel)) {
            return null;
        } else {
            ServerLevel serverlevel = (ServerLevel)entity.level();
            // findNearestMapStructure blocks on ServerChunkCache.getChunk. Off the server thread that
            // parks on a CompletableFuture only the server thread can complete, and a trader placed by
            // a structure is serialised mid-generation (ProtoChunk.addEntity -> Entity.save -> getOffers
            // -> updateTrades -> here) on a worldgen worker the server thread is itself waiting on, so
            // the search would deadlock world generation. Skip the trade instead; updateTrades runs
            // again on the server thread while the trader still has no offers.
            if (!serverlevel.getServer().isSameThread()) {
                return null;
            }
            BlockPos blockpos = serverlevel.findNearestMapStructure(ACTagRegistry.ON_UNDERGROUND_CABIN_MAPS, entity.blockPosition(), 100, true);
            if (blockpos != null) {
                ItemStack itemstack = MapItem.create(serverlevel, blockpos.getX(), blockpos.getZ(), (byte)2, true, true);
                MapItem.renderBiomePreviewMap(serverlevel, itemstack);
                // In 1.21, use MapDecorationTypes.TARGET_POINT or similar for map decorations
                MapItemSavedData.addTargetDecoration(itemstack, blockpos, "+", MapDecorationTypes.TARGET_POINT);
                // In 1.21, use DataComponents to set custom name
                itemstack.set(DataComponents.CUSTOM_NAME, Component.translatable("item.alexscaves.underground_cabin_explorer_map"));
                return new MerchantOffer(
                    new ItemCost(Items.EMERALD, this.emeraldCost),
                    Optional.of(new ItemCost(Items.COMPASS, 1)),
                    itemstack,
                    this.maxUses,
                    this.villagerXp,
                    0.2F
                );
            } else {
                return null;
            }
        }
    }
}
