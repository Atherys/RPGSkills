package com.atherys.rpgskills.t3;

import com.atherys.core.utils.Sound;
import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.RpgSkills;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.skill.MeleeAttackSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.ApplyableCarrier;
import com.atherys.skills.api.effect.TemporaryEffect;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static com.atherys.rpgskills.util.DescriptionUtils.otherText;
import static org.spongepowered.api.text.TextTemplate.arg;

public class ShieldSpikes extends RPGSkill implements MeleeAttackSkill {
    public static final String SHIELD_SPIKES_EFFECT = "shield-spikes-effect";

    private static final String DEFAULT_TIME = "10000";
    private static final String DEFAULT_DAMAGE = "5.0";
    private static final String DEFAULT_OTHER_TEXT = "";

    private static ParticleEffect particleEffect = ParticleEffect.builder()
            .quantity(1)
            .type(ParticleTypes.ANGRY_VILLAGER)
            .build();

    private static Sound zombie = Sound.builder(SoundTypes.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1).build();
    private static Sound blaze = Sound.builder(SoundTypes.ENTITY_BLAZE_HURT, 1).build();

    private Set<Living> shieldSpikers;

    public ShieldSpikes() {
        super(
                SkillSpec.create()
                        .id("shield-spikes")
                        .name("ShieldSpikes")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "For the next ", arg(TIME), ", any melee weapon attacks against you are blocked ",
                                " and the attacker takes ", arg(DAMAGE), " physical damage. ", arg(OTHER_TEXT)
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DAMAGE)),
                Tuple.of(TIME, DescriptionArguments.ofTimeProperty(this, TIME, DEFAULT_TIME)),
                Tuple.of(OTHER_TEXT, otherText(this))
        );

        shieldSpikers = new HashSet<>();

        Task.builder()
                .execute(() -> {
                    shieldSpikers.removeIf(living -> {

                        if (!living.isRemoved()) {
                            living.getWorld().spawnParticles(particleEffect, living.getLocation().getPosition().add(0, 1, 0));
                            return !AtherysSkills.getInstance().getEffectService().hasEffect(living, SHIELD_SPIKES_EFFECT);
                        }

                        return true;
                    });
                })
                .interval(500, TimeUnit.MILLISECONDS)
                .submit(RpgSkills.getInstance());
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        int duration = asInt(user, getProperty(TIME, String.class, DEFAULT_TIME));
        AtherysSkills.getInstance().getEffectService().applyEffect(user, new ShieldSpikesEffect(duration));
        user.getWorld().spawnParticles(particleEffect, user.getLocation().getPosition().add(0, 1, 0));
        shieldSpikers.add(user);
        return CastResult.success();
    }

    @Listener
    public void onAttack(DamageEntityEvent event, @Root EntityDamageSource source, @Getter("getTargetEntity") Living target) {
        onDamage(event, source, target);
    }

    @Override
    public boolean meleeAttack(Living user, Living target) {
        if (AtherysSkills.getInstance().getEffectService().hasEffect(target, SHIELD_SPIKES_EFFECT)) {
            double damage = asDouble(target, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE));
            user.damage(damage, DamageUtils.directPhysical(target));

            Location<World> location = target.getLocation();

            Sound.playSound(zombie, location.getExtent(), location.getPosition());
            Sound.playSound(blaze, location.getExtent(), location.getPosition());

            return true;
        }

        return false;
    }

    private static class ShieldSpikesEffect extends TemporaryEffect {
        protected ShieldSpikesEffect(int duration) {
            super(SHIELD_SPIKES_EFFECT, "Shield Spikes", duration, true);
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
}
