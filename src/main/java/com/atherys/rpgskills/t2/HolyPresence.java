package com.atherys.rpgskills.t2;

import com.atherys.core.utils.Sound;
import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.rpgskills.util.skill.RadiusSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpg.api.skill.DescriptionArguments.ofSlowPercentProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static org.spongepowered.api.text.TextTemplate.arg;

public class HolyPresence extends RPGSkill implements PartySkill, RadiusSkill {

    private static final String DEFAULT_RADIUS = "10";
    private static final String DEFAULT_DURATION = "4000";
    private static final String DEFAULT_AMPLIFIER = "0";

    private static final ParticleEffect particleEffect = ParticleEffect.builder()
            .type(ParticleTypes.INSTANT_SPELL)
            .quantity(3)
            .build();

    private static final Sound sound = Sound.builder(SoundTypes.ENTITY_EVOCATION_ILLAGER_PREPARE_SUMMON, 1)
            .pitch(1.5)
            .soundCategory(SoundCategories.PLAYER).build();

    public HolyPresence() {
        super(
                SkillSpec.create()
                        .id("holy-presence")
                        .name("Holy Presence")
                        .resourceCost("0")
                        .cooldown("0")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "You and all enemies within ", arg(RADIUS), " blocks are slowed by ",
                                arg(AMPLIFIER), " for 4+(0.2*WIS) seconds."
                        ))
        );

        setDescriptionArguments(
                Tuple.of(RADIUS, ofProperty(this, RADIUS, DEFAULT_RADIUS)),
                Tuple.of(AMPLIFIER, ofSlowPercentProperty(this, AMPLIFIER, DEFAULT_AMPLIFIER))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        double radius = asDouble(user, getProperty(RADIUS, String.class, DEFAULT_RADIUS));
        int amplifier = asInt(user, getProperty(AMPLIFIER, String.class, DEFAULT_AMPLIFIER));
        Location<World> location = user.getLocation();

        applyToRadius(location, radius, living -> {
            if (!arePlayersInParty(user, living) || living == user) {
                int duration = asInt(user, living, getProperty(TIME, String.class, DEFAULT_DURATION));
                AtherysSkills.getInstance().getEffectService().applyEffect(living, Effects.ofSlowness("holy-presence", "Holy Presence", duration, amplifier));
            }
        });

        Sound.playSound(sound, location.getExtent(), location.getPosition());
        PhysicsUtils.spawnParticleCircle(particleEffect, user.getLocation(), radius);

        return CastResult.success();
    }
}
