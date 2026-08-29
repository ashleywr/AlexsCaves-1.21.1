package com.telepathicgrunt.tact;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.SpearItem;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.google.common.collect.Multimap;
import com.telepathicgrunt.tact.mixin.MobEffectInstanceAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Map;

public class ItemModifications {
    public static TagKey<Item> RADIOACTIVE_AND_ACID_RESISTANT_ARMOR_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(TACT.MODID, "radioactive_and_acid_resistant_armor"));

    static void stunEffectAdjustment(final MobEffectEvent.Added event) {
        LivingEntity affectedEntity = event.getEntity();
        if (affectedEntity == null || affectedEntity.level().isClientSide()) {
            return;
        }

        MobEffectInstance currentEffect = event.getEffectInstance();
        if (currentEffect.getEffect() == ACEffectRegistry.STUNNED.get() && affectedEntity.getLastAttacker() != null) {
            LivingEntity attacker = affectedEntity.getLastAttacker();
            ItemStack usedItem = attacker.getMainHandItem();

            if (usedItem.is(ACItemRegistry.PRIMITIVE_CLUB.get())) {
                int extraTime = Config.PRIMITIVE_CLUB_RANDOM_EXTRA_STUN_TIME.get() == 0 ?
                        0 : affectedEntity.getRandom().nextInt(Config.PRIMITIVE_CLUB_RANDOM_EXTRA_STUN_TIME.get());

                ((MobEffectInstanceAccessor)currentEffect).setDuration(Config.PRIMITIVE_CLUB_BASE_STUN_TIME.get() + extraTime);
            }
        }
    }

    static void doItemAttributeModifications(final FMLCommonSetupEvent event) {
        // 1.21 builds the spear's modifiers on demand from its damage field rather than
        // caching a Multimap, so the configured melee damage is applied to that field.
        ((SpearItem) ACItemRegistry.EXTINCTION_SPEAR.get()).setSpearDamage(Config.EXTINCTION_SPEAR_MELEE_DAMAGE.get());
    }

    public static int howManyEquippedRadioactiveOrAcidResistantArmorOnEntity(final LivingEntity entity) {
        int resistantArmor = 0;
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(RADIOACTIVE_AND_ACID_RESISTANT_ARMOR_TAG)) {
            ++resistantArmor;
        }

        if (entity.getItemBySlot(EquipmentSlot.CHEST).is(RADIOACTIVE_AND_ACID_RESISTANT_ARMOR_TAG)) {
            ++resistantArmor;
        }

        if (entity.getItemBySlot(EquipmentSlot.LEGS).is(RADIOACTIVE_AND_ACID_RESISTANT_ARMOR_TAG)) {
            ++resistantArmor;
        }

        if (entity.getItemBySlot(EquipmentSlot.FEET).is(RADIOACTIVE_AND_ACID_RESISTANT_ARMOR_TAG)) {
            ++resistantArmor;
        }

        return resistantArmor;
    }
}
