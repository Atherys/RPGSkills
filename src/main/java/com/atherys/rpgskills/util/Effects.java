package com.atherys.rpgskills.util;

import com.atherys.rpg.api.effect.TemporaryAttributesEffect;
import com.atherys.rpg.api.stat.AttributeType;
import com.atherys.skills.api.effect.Applyable;
import com.atherys.skills.api.effect.ApplyableCarrier;
import com.atherys.skills.api.effect.PeriodicEffect;
import com.atherys.skills.api.effect.TemporaryPotionEffect;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;

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
            character.removeEffect(this);
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

    /**
     * An effect which prevents the target from attacking with melee or ranged.
     */
    private static class DisarmEffect extends TemporaryPotionEffect {
        private static PotionEffect.Builder builder = PotionEffect.builder()
                .potionType(PotionEffectTypes.MINING_FATIGUE);

        public DisarmEffect(int duration) {
            super(
                    "disarm",
                    "Disarm",
                    builder
                        .amplifier(200)
                        .duration(duration)
                        .build(),
                    false
            );
        }
    }

    /*
    @Listener(order = Order.FIRST)
    public void onAttack(AttackEntityEvent event, @Root EntityDamageSource source) {
        if (source instanceof IndirectEntityDamageSource) {
            if (((IndirectEntityDamageSource) source).getIndirectSource() instanceof Living) {
            }
        }

        if (source.getSource() instanceof Living) {

        }
        if (AtherysSkills.getInstance().getEffectService().hasEffect(living, "disarm")) {
            event.setCancelled(true);
        }
    }
     */
}
