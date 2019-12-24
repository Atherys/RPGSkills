package com.atherys.rpgskills.util;

import com.atherys.core.utils.EntityUtils;
import com.atherys.rpg.api.effect.TemporaryAttributesEffect;
import com.atherys.rpg.api.stat.AttributeType;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.*;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

import java.util.Map;

public final class Effects {

    public static Applyable ofAttributes(String id, String name, long duration, Map<AttributeType, Double> buffs, boolean isPositive) {
        return new TemporaryAttributesEffect(id, name, duration, buffs, isPositive);
    }

    public static Applyable damageOverTime(String id, String name, long duration, double damage, DamageSource source) {
        return new DamageOverTimeEffect(id, name, duration, damage, source);
    }

    public static Applyable damageOverTime(String id, String name, long duration, double damage) {
        return new DamageOverTimeEffect(id, name, duration, damage, DamageSources.VOID);
    }

    public static Applyable ofSlowness(String id, String name, int duration, int modifier) {
        return new SlowEffect(id, name, duration, modifier);
    }

    public static Applyable ofSpeed(String id, String name, int duration, int modifier) {
        return new SlowEffect(id, name, duration, modifier);
    }

    public static Applyable ofBlindness(String id, String name, int duration, int modifier) {
        return new BlindEffect(id, name, duration, false);
    }

    public static Applyable disarm(int duration) {
        return new DisarmEffect(duration);
    }

    private static class DamageOverTimeEffect extends PeriodicEffect {

        private double damagePerTick;
        private DamageSource damageSource;

        private DamageOverTimeEffect(String id, String name, long duration, double totalDamage, DamageSource damageSource) {
            super(id, name, 1000, (int) (duration / 1000), false);
            this.damagePerTick = totalDamage / duration * 1000;
            this.damageSource = damageSource;
        }

        @Override
        protected boolean apply(ApplyableCarrier<?> character) {
            character.getLiving().ifPresent(living -> {
                living.damage(damagePerTick, damageSource);
            });
            return true;
        }

        @Override
        protected boolean remove(ApplyableCarrier<?> character) {
            return true;
        }
    }

    private static class SlowEffect extends TemporaryPotionEffect {
        private static PotionEffect.Builder builder = PotionEffect.builder()
                .particles(true)
                .potionType(PotionEffectTypes.SLOWNESS)
                .ambience(true);


        private SlowEffect(String id, String name, int amplifier, int duration) {
            super(
                    id,
                    name,
                    builder
                        .amplifier(amplifier)
                        .duration(duration)
                        .build(),
                    false
            );
        }
    }

    private static class SpeedEffect extends TemporaryPotionEffect {
        private static PotionEffect.Builder builder = PotionEffect.builder()
                .particles(true)
                .potionType(PotionEffectTypes.SPEED)
                .ambience(true);


        private SpeedEffect(String id, String name, int amplifier, int duration) {
            super(
                    id,
                    name,
                    builder
                        .amplifier(amplifier)
                        .duration(duration)
                        .build(),
                    true
            );
        }
    }

    private static class BlindEffect extends TemporaryPotionEffect {
        private static PotionEffect.Builder builder = PotionEffect.builder()
                .particles(true)
                .potionType(PotionEffectTypes.BLINDNESS)
                .ambience(true);

        public BlindEffect(String id, String name, int duration, boolean isPositive) {
            super(
                    id,
                    name,
                    builder
                        .duration(duration)
                        .amplifier(1)
                        .build(),
                    isPositive
            );
        }
    }

    /**
     * An effect which prevents the target from attacking with melee or ranged.
     */
    private static class DisarmEffect extends TemporaryEffect {
        public static final String DISARM_ID = "disarm";

        public DisarmEffect(int duration) {
            super(
                    DISARM_ID,
                    "Disarm",
                    duration,
                    false
            );
        }

        @Override
        protected boolean apply(ApplyableCarrier<?> character) { return true; }

        @Override
        protected boolean remove(ApplyableCarrier<?> character) { return true; }
    }

    @Listener(order = Order.FIRST)
    public void onAttack(AttackEntityEvent event, @Root EntityDamageSource source) {
        Entity root = EntityUtils.getRootEntity(source);

        if (root instanceof Living) {
            if (AtherysSkills.getInstance().getEffectService().hasEffect((Living) root,DisarmEffect.DISARM_ID)) {
                event.setCancelled(true);
            }
        }
    }
}