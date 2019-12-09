package com.atherys.rpgskills.util;

import com.atherys.rpg.api.effect.TemporaryAttributesEffect;
import com.atherys.rpg.api.stat.AttributeType;
import com.atherys.skills.api.effect.Applyable;
import com.atherys.skills.api.effect.ApplyableCarrier;
import com.atherys.skills.api.effect.PeriodicEffect;
import com.atherys.skills.api.effect.TemporaryPotionEffect;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;

import java.util.Map;

public final class Effects {

    public static Applyable ofAttributes(String id, String name, long duration, Map<AttributeType, Double> buffs) {
        return new TemporaryAttributesEffect(id, name, duration, buffs);
    }

    public static Applyable damageOverTime(String id, String name, long duration, double damage) {
        return new DamageOverTimeEffect(id, name, duration, damage);
    }

    public static Applyable ofSlowness(String id, String name, int duration, int modifier) {
        return new SlowEffect(id, name, duration, modifier);
    }

    public static Applyable ofSpeed(String id, String name, int duration, int modifier) {
        return new SlowEffect(id, name, duration, modifier);
    }

    private static class DamageOverTimeEffect extends PeriodicEffect {

        private double damagePerTick;

        private DamageOverTimeEffect(String id, String name, long duration, double totalDamage) {
            super(id, name, 1000, (int) (duration / 1000));
            this.damagePerTick = totalDamage / duration * 1000;
        }

        @Override
        protected boolean apply(ApplyableCarrier<?> character) {
            character.getLiving().ifPresent(living -> {
                living.damage(damagePerTick, DamageSources.VOID);
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
                        .build()
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
                            .build()
            );
        }
    }
}
