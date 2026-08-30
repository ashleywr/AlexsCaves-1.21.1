package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;

public class HazmatArmorItem extends ArmorItem {

    private static final int[] DURABILITY_PER_SLOT = new int[]{13, 15, 16, 11};
    private static final int DURABILITY_MULTIPLIER = 25;
    
    public HazmatArmorItem(Holder<ArmorMaterial> armorMaterial, Type slot) {
        super(armorMaterial, slot, new Properties().durability(DURABILITY_PER_SLOT[slot.getSlot().getIndex()] * DURABILITY_MULTIPLIER));
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getArmorProperties());
    }

    // See DivingArmorItem: onArmorTick no longer exists in 1.21, so the mask's breathing
    // particles never played.
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!(entity instanceof Player player) || player.getItemBySlot(EquipmentSlot.HEAD) != stack) {
            return;
        }
        if (stack.is(ACItemRegistry.HAZMAT_MASK.get()) && Math.cos(player.tickCount * 0.05F) >= 0.9F) {
            Vec3 eyes = player.getEyePosition();
            if (level.random.nextBoolean()) {
                Vec3 leftOffset = new Vec3(0.25F, -0.3F, 0.25F).xRot((float) Math.toRadians(-player.getXRot())).yRot((float) Math.toRadians(-player.getYHeadRot()));
                level.addParticle(ACParticleRegistry.HAZMAT_BREATHE.get(), eyes.x + leftOffset.x, eyes.y + leftOffset.y, eyes.z + leftOffset.z, (level.random.nextFloat() - 0.5F) * 0.1F, (level.random.nextFloat() - 0.5F) * 0.1F, (level.random.nextFloat() - 0.5F) * 0.1F);
            }
            if (level.random.nextBoolean()) {
                Vec3 rightOffset = new Vec3(-0.25F, -0.3F, 0.25F).xRot((float) Math.toRadians(-player.getXRot())).yRot((float) Math.toRadians(-player.getYHeadRot()));
                level.addParticle(ACParticleRegistry.HAZMAT_BREATHE.get(), eyes.x + rightOffset.x, eyes.y + rightOffset.y, eyes.z + rightOffset.z, (level.random.nextFloat() - 0.5F) * 0.1F, (level.random.nextFloat() - 0.5F) * 0.1F, (level.random.nextFloat() - 0.5F) * 0.1F);
            }
        }
    }

    @Override
    @Nullable
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        if (slot == EquipmentSlot.LEGS) {
            return ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/armor/hazmat_suit_1.png");
        } else {
            return ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/armor/hazmat_suit_0.png");
        }
    }

    public static int getWornAmount(LivingEntity entity) {
        // TACT: counted via the tact:radioactive_and_acid_resistant_armor tag instead of
        // hardcoded slot checks, so other mods' protective gear can be recognised. The
        // tag already contains all four hazmat pieces, so this replaces the old checks
        // rather than adding to them; adding would count hazmat armour twice.
        return com.telepathicgrunt.tact.ItemModifications
                .howManyEquippedRadioactiveOrAcidResistantArmorOnEntity(entity);
    }
}
