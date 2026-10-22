package com.energiarural;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Arma que causa dano extra enquanto tiver energia. Recarregue clicando com ela numa Bateria. */
public class EnergyWeaponItem extends Item {
    public final WeaponTier tier;

    public EnergyWeaponItem(Properties properties, WeaponTier tier) {
        super(properties);
        this.tier = tier;
    }

    public int getEnergy(ItemStack stack) {
        return stack.getOrDefault(ModRegistry.ENERGY.get(), 0);
    }

    /** Coloca energia na arma e devolve quanto realmente entrou. */
    public int charge(ItemStack stack, int amount) {
        int current = getEnergy(stack);
        int added = Math.max(0, Math.min(tier.capacity - current, amount));
        if (added > 0) {
            stack.set(ModRegistry.ENERGY.get(), current + added);
        }
        return added;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        int energy = getEnergy(stack);
        if (energy >= tier.cost && attacker.level() instanceof ServerLevel serverLevel) {
            stack.set(ModRegistry.ENERGY.get(), energy - tier.cost);
            DamageSource source = serverLevel.damageSources().indirectMagic(attacker, attacker);
            target.hurtServer(serverLevel, source, tier.extraDamage);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getEnergy(stack) / tier.capacity);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x3FA7FF;
    }
}
