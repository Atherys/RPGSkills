package com.atherys.rpgskills.util;

import com.atherys.core.utils.EntityUtils;
import com.atherys.rpg.AtherysRPG;
import com.atherys.rpg.api.effect.TemporaryAttributesEffect;
import com.atherys.rpg.api.stat.AttributeType;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.*;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gamerule.DefaultGameRules;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public final class Effects {

    public static Applyable ofAttributes(String id, String name, long duration, Map<AttributeType, Double> buffs, boolean isPositive) {
        return new TemporaryAttributesEffect(id, name, duration, buffs, isPositive);
    }

    public static Applyable damageOverTime(String id, String name, long duration, double damage, EntityDamageSource damageSource) {
        return new DamageOverTimeEffect(id, name, duration, damage, damageSource);
    }

    public static Applyable magicalDamageOverTime(String id, String name, long duration, double damage, Living user) {
        return damageOverTime(id, name, duration, damage, DamageUtils.directMagical(user));
    }

    public static Applyable physicalDamageOverTime(String id, String name, long duration, double damage, Living user) {
        return damageOverTime(id, name, duration, damage, DamageUtils.directPhysical(user));
    }

    public static Applyable pureDamageOverTime(String id, String name, long duration, double damage, Living user) {
        return damageOverTime(id, name, duration, damage, DamageUtils.directPure(user));
    }

    public static Applyable blankTemporary(String id, String name, int duration, boolean isPositive) {
        return new BlankTemporaryEffect(id, name, duration, isPositive);
    }

    public static Applyable shield(String id, String name, int duration, int modifier) {
        return new ShieldEffect(id, name, duration, modifier);
    }

    public static Applyable ofSlowness(String id, String name, int duration, int modifier) {
        return new SlowEffect(id, name, duration, modifier);
    }

    public static Applyable ofSpeed(String id, String name, int duration, int modifier) {
        return new SpeedEffect(id, name, duration, modifier);
    }

    public static Applyable ofBlindness(String id, String name, int duration) {
        return new BlindEffect(id, name, duration, false);
    }

    public static Applyable disarm(int duration) {
        return new DisarmEffect(duration);
    }

    public static class DamageOverTimeEffect extends PeriodicEffect {

        private double damagePerTick;
        private Cause source;
        private boolean keepInventory;

        public DamageOverTimeEffect(String id, String name, long duration, double totalDamage, EntityDamageSource source) {
            super(id, name, 1000, (int) (duration / 1000), false);
            // total divided by ticks
            this.damagePerTick = totalDamage / (duration / 1000.0);
            this.source = Cause.of(EventContext.empty(), source);
            this.keepInventory = Boolean.getBoolean(source.getSource().getWorld().getGameRule(DefaultGameRules.KEEP_INVENTORY).orElse("true"));
        }

        @Override
        protected boolean apply(ApplyableCarrier<?> character) {
            character.getLiving().ifPresent(living -> {
                DamageEntityEvent event = SpongeEventFactory.createDamageEntityEvent(
                        source, Collections.emptyList(), living, damagePerTick
                );
                Sponge.getEventManager().post(event);
                if (!event.isCancelled()) {

                    if (event.willCauseDeath() && living.health().get() > 0) {
                        DestructEntityEvent.Death deathEvent = SpongeEventFactory.createDestructEntityEventDeath(
                                source,
                                MessageChannel.TO_PLAYERS,
                                Optional.empty(),
                                new MessageEvent.MessageFormatter(),
                                living,
                                keepInventory,
                                true
                        );
                        Sponge.getEventManager().post(deathEvent);
                    }

                    living.damage(event.getBaseDamage(), DamageSources.VOID);
                }
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


        private SlowEffect(String id, String name, int duration, int amplifier) {
            super(
                    id,
                    name,
                    builder
                        .amplifier(amplifier)
                        .duration(duration / 50)
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


        private SpeedEffect(String id, String name, int duration, int amplifier) {
            super(
                    id,
                    name,
                    builder
                        .amplifier(amplifier)
                        .duration(duration / 50)
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
                        .duration(duration / 50)
                        .amplifier(1)
                        .build(),
                    isPositive
            );
        }
    }

    private static class ShieldEffect extends TemporaryPotionEffect {
        private static PotionEffect.Builder builder = PotionEffect.builder()
                .particles(true)
                .potionType(PotionEffectTypes.ABSORPTION)
                .ambience(true);

        public ShieldEffect(String id, String name, int duration, int amplifier) {
            super(
                    id,
                    name,
                    builder
                        .duration(duration / 50)
                        .amplifier(amplifier)
                        .build(),
                    true
            );
        }
    }

    private static class BlankTemporaryEffect extends TemporaryEffect {

        protected BlankTemporaryEffect(String id, String name, long duration, boolean isPositive) {
            super(id, name, duration, isPositive);
        }

        @Override
        protected boolean apply(ApplyableCarrier<?> character) {
            return true;
        }

        @Override
        protected boolean remove(ApplyableCarrier<?> character) {
            return true;
        }
    }

    /**
     * An effect which prevents the target from attacking with melee or ranged.
     */
    private static class DisarmEffect extends TemporaryPotionEffect {
        public static final String DISARM_ID = "disarm";
        private static PotionEffect.Builder builder = PotionEffect.builder()
                .particles(true)
                .potionType(PotionEffectTypes.MINING_FATIGUE)
                .ambience(true)
                .amplifier(200);

        public DisarmEffect(int duration) {
            super(
                    DISARM_ID,
                    "Disarm",
                    builder.duration(duration / 50).build(),
                    false
            );
        }
    }

    @Listener(order = Order.FIRST)
    public void onAttack(AttackEntityEvent event, @Root EntityDamageSource source) {
        Entity root = EntityUtils.getRootEntity(source);

        if (root instanceof Living) {
            if (AtherysSkills.getInstance().getEffectService().hasEffect((Living) root, DisarmEffect.DISARM_ID)) {
                event.setCancelled(true);
            }
        }
    }

    @Listener(order = Order.FIRST)
    public void onShoot(SpawnEntityEvent event, @Root Living source) {
        if (AtherysSkills.getInstance().getEffectService().hasEffect(source, DisarmEffect.DISARM_ID)) {
            event.getEntities().forEach(entity -> {
                if (entity instanceof Projectile) {
                    entity.remove();
                }
            });
        }
    }
}
