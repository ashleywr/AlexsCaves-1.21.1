package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.EquipmentSlotGroup;

public class GingerbreadArmorItem extends ArmorItem {

    private static final int[] DURABILITY_PER_SLOT = new int[]{13, 15, 16, 11};
    private static final int DURABILITY_MULTIPLIER = 15;
    private static final double MIN_SPEED_BOOST = 0.1D;
    private static final double MAX_SPEED_BOOST = 1.0D;
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B77"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E12"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B43F"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB111")};
    private final Map<Integer, ItemAttributeModifiers> gingerbreadDurabilityDependentAttributes = new HashMap<>();
    private final ItemAttributeModifiers defaultAttributes;

    public GingerbreadArmorItem(Holder<ArmorMaterial> armorMaterial, Type slot) {
        super(armorMaterial, slot, new Properties().durability(DURABILITY_PER_SLOT[slot.getSlot().getIndex()] * DURABILITY_MULTIPLIER));
        ResourceLocation armorId = ResourceLocation.fromNamespaceAndPath("alexscaves", "armor." + type.getSlot().getName());
        EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(type.getSlot());
        defaultAttributes = ItemAttributeModifiers.builder()
                .add(Attributes.ARMOR, new AttributeModifier(armorId, (double)this.getDefense(), AttributeModifier.Operation.ADD_VALUE), group)
                .add(Attributes.MOVEMENT_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("alexscaves", "movement_speed"), MIN_SPEED_BOOST, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), group)
                .build();
    }

    private ItemAttributeModifiers getOrCreateDurabilityAttributes(int durabilityIn, int maxDurability) {
        if (gingerbreadDurabilityDependentAttributes.containsKey(durabilityIn)) {
            return gingerbreadDurabilityDependentAttributes.get(durabilityIn);
        } else {
            float scaledDurability = durabilityIn / (float) maxDurability;
            double speed = MIN_SPEED_BOOST + (MAX_SPEED_BOOST - MIN_SPEED_BOOST) * scaledDurability;
            ResourceLocation armorId = ResourceLocation.fromNamespaceAndPath("alexscaves", "armor." + type.getSlot().getName());
            EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(type.getSlot());
            ItemAttributeModifiers modifiers = ItemAttributeModifiers.builder()
                    .add(Attributes.ARMOR, new AttributeModifier(armorId, (double)this.getDefense(), AttributeModifier.Operation.ADD_VALUE), group)
                    .add(Attributes.MOVEMENT_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("alexscaves", "movement_speed"), speed, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), group)
                    .build();
            gingerbreadDurabilityDependentAttributes.put(durabilityIn, modifiers);
            return modifiers;
        }
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getArmorProperties());
    }

    // See SpearItem: the EquipmentSlot-based signature no longer overrides anything. The
    // stack-sensitive form also lets the durability-scaled speed boost be used, which
    // getOrCreateDurabilityAttributes had been computing for a caller that never existed.
    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        int maxDurability = stack.getMaxDamage();
        if (maxDurability <= 0) {
            return defaultAttributes;
        }
        return getOrCreateDurabilityAttributes(maxDurability - stack.getDamageValue(), maxDurability);
    }

    @Override
    @Nullable
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        if (slot == EquipmentSlot.LEGS) {
            return ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/armor/gingerbread_armor_1.png");
        } else {
            return ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/armor/gingerbread_armor_0.png");
        }
    }
}
