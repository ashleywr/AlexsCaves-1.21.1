package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.tags.FluidTags;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.NeoForgeMod;

import javax.annotation.Nullable;
import java.util.UUID;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.EquipmentSlotGroup;

public class DivingArmorItem extends ArmorItem {
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B77"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E12"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B43F"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB111")};
    private final ItemAttributeModifiers divingArmorAttributes;

    private static final int[] DURABILITY_PER_SLOT = new int[]{13, 15, 16, 11};
    private static final int DURABILITY_MULTIPLIER = 25;

    public DivingArmorItem(Holder<ArmorMaterial> armorMaterial, Type slot) {
        super(armorMaterial, slot, new Properties().durability(DURABILITY_PER_SLOT[slot.getSlot().getIndex()] * DURABILITY_MULTIPLIER));
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(type.getSlot());
        ResourceLocation armorId = ResourceLocation.fromNamespaceAndPath("alexscaves", "armor." + type.getSlot().getName());
        builder.add(Attributes.ARMOR, new AttributeModifier(armorId, (double)this.getDefense(), AttributeModifier.Operation.ADD_VALUE), group);
        if (slot == Type.LEGGINGS) {
            builder.add(NeoForgeMod.SWIM_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("alexscaves", "swim_speed"), 0.5D, AttributeModifier.Operation.ADD_VALUE), group);
        }else if (slot == Type.CHESTPLATE) {
            builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("alexscaves", "armor_toughness"), (double)armorMaterial.value().toughness(), AttributeModifier.Operation.ADD_VALUE), group);
        }
        float knockbackResistance = armorMaterial.value().knockbackResistance();
        if (knockbackResistance > 0) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("alexscaves", "knockback_resistance"), (double)knockbackResistance, AttributeModifier.Operation.ADD_VALUE), group);
        }
        divingArmorAttributes = builder.build();
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getArmorProperties());
    }

    // See SpearItem: the EquipmentSlot-based signature no longer overrides anything, so
    // the swim speed, extra toughness and knockback resistance never reached the player
    // (official #959). The slot restriction now lives in the EquipmentSlotGroup instead.
    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return this.divingArmorAttributes;
    }

    // NeoForge removed onArmorTick in 1.21, so this never ran and the diving helmet
    // granted no water breathing at all. DarknessArmorItem was already moved to
    // inventoryTick for the same reason; this one and the hazmat mask were missed.
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!(entity instanceof Player player) || player.getItemBySlot(EquipmentSlot.HEAD) != stack) {
            return;
        }
        if (!level.isClientSide && this.type == Type.HELMET) {
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 220, 0, false, false, true));
            if (player.isEyeInFluid(FluidTags.WATER) || player.getVehicle() instanceof SubmarineEntity) {
                int maxAir = player.getMaxAirSupply();
                if (player.getAirSupply() < maxAir) {
                    player.setAirSupply(maxAir);
                }
            }
        }
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (this.type == Type.LEGGINGS) {
            String amount = "0.5";
            Component attributeName = Component.translatable(NeoForgeMod.SWIM_SPEED.value().getDescriptionId());
            tooltip.add(Component.translatable("attribute.modifier.plus.0", amount, attributeName).withStyle(ChatFormatting.BLUE));
        }
    }

    @Override
    @Nullable
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        if (slot == EquipmentSlot.LEGS) {
            return ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/armor/diving_suit_1.png");
        } else {
            return ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/armor/diving_suit_0.png");
        }
    }
}
