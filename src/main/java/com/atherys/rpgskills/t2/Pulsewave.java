package com.atherys.rpgskills.t2;

import com.atherys.rpg.AtherysRPG;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.Applyable;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpg.api.skill.DescriptionArguments.timeProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Pulsewave extends RPGSkill implements PartySkill {
    private static final String DEFAULT_RADIUS = "5.0";
    private static final String DEFAULT_DAMAGE = "5.0";
    private static final String DEFAULT_DOT = "5.0";
    private static final String DEFAULT_TIME = "10000";

    private static final ParticleEffect particleEffect = ParticleEffect.builder()
            .type(ParticleTypes.FLAME)
            .quantity(3)
            .build();

    public Pulsewave() {
        super(
                SkillSpec.create()
                        .id("immolate")
                        .name("Immolate")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Send out a wave of flames in all directions, dealing ", arg(DAMAGE), " magical damage to all enemies " +
                                        "within ", arg(RADIUS), " blocks and an additional ", arg(AMPLIFIER), " magical damage over ", arg(TIME), " seconds."

                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DAMAGE)),
                Tuple.of(RADIUS, ofProperty(this, RADIUS, DEFAULT_RADIUS)),
                Tuple.of(TIME, timeProperty(this, TIME, DEFAULT_TIME)),
                Tuple.of(AMPLIFIER, ofProperty(this, AMPLIFIER, DEFAULT_DOT))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        double radius = asDouble(user, getProperty(RADIUS, String.class, DEFAULT_RADIUS));
        Collection<Entity> inRadius = user.getNearbyEntities(radius);

        if (inRadius.size() > 0) {
            double damage = asDouble(user, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE));
            int duration = asInt(user, getProperty(TIME, String.class, DEFAULT_TIME));
            DamageSource damageSource = DamageUtils.directMagical(user);

            inRadius.forEach(entity -> {
                if (entity instanceof Living && !entity.equals(user) && !arePlayersInParty(user, (Living) entity)) {
                    Living target = (Living) entity;
                    target.damage(damage, damageSource);
                    Applyable damageEffect = Effects.magicalDamageOverTime(
                            "immolate",
                            "Immolate",
                            duration,
                            damage,
                            user
                    );
                    AtherysSkills.getInstance().getEffectService().applyEffect(target, damageEffect);
                }
            });
        }

        Location<World> particleLocation = new Location<>(user.getWorld(), user.getLocation().getPosition().sub(0, 0.5, 0));

        PhysicsUtils.spawnParticleCircle(particleEffect, particleLocation, radius / 3);

        Task.builder()
                .delayTicks(2)
                .execute(() -> PhysicsUtils.spawnParticleCircle(particleEffect, particleLocation, radius * 2/3))
                .submit(AtherysRPG.getInstance());

        Task.builder()
                .delayTicks(4)
                .execute(() -> PhysicsUtils.spawnParticleCircle(particleEffect, particleLocation, radius))
                .submit(AtherysRPG.getInstance());

        return CastResult.success();
    }
}
