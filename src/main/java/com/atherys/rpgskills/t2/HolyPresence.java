package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.rpgskills.util.skill.RadiusSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.util.Tuple;

import java.util.Collection;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static org.spongepowered.api.text.TextTemplate.arg;

public class HolyPresence extends RPGSkill implements PartySkill, RadiusSkill {

    private static final String DEFAULT_RADIUS = "10";
    private static final String DEFAULT_DURATION = "4000";

    private static final ParticleEffect particleEffect = ParticleEffect.builder()
            .type(ParticleTypes.INSTANT_SPELL)
            .quantity(3)
            .build();

    public HolyPresence() {
        super(
                SkillSpec.create()
                        .id("holy-presence")
                        .name("Holy Presence")
                        .resourceCost("0")
                        .cooldown("0")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "You and all enemies within ", arg(RADIUS), " blocks are slowed for ", arg(TIME), " seconds."
                        ))
        );

        setDescriptionArguments(
                Tuple.of(RADIUS, ofProperty(this, RADIUS, DEFAULT_RADIUS)),
                Tuple.of(TIME, DescriptionArguments.timeProperty(this, TIME, DEFAULT_DURATION))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        double radius = asDouble(user, getProperty(RADIUS, String.class, DEFAULT_RADIUS));
        int duration = asInt(user, getProperty(TIME, String.class, DEFAULT_DURATION));
        int amplifier = asInt(user, getProperty(AMPLIFIER, String.class, "2"));

        applyToRadius(user.getLocation(), radius, living -> {
            if (!arePlayersInParty(user, living) || living == user) {
                AtherysSkills.getInstance().getEffectService().applyEffect(living, Effects.ofSlowness("holy-presence", "Holy Presence", duration, amplifier));
            }
        });

        PhysicsUtils.spawnParticleCircle(particleEffect, user.getLocation(), radius);

        return CastResult.success();
    }
}
