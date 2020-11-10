package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.rpgskills.util.skill.RadiusSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;

import java.util.Collection;

import static com.atherys.rpgskills.util.CommonProperties.*;

public class HolyPresence extends RPGSkill implements PartySkill, RadiusSkill {

    private static final String DEFAULT_RADIUS = "10";
    private static final String DEFAULT_DURATION = "4000";

    public HolyPresence() {
        super(
                SkillSpec.create()
                        .id("holy-presence")
                        .name("Holy Presence")
                        .resourceCost("0")
                        .cooldown("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        double radius = asDouble(user, getProperty(AMPLIFIER, String.class, DEFAULT_RADIUS));
        int duration = asInt(user, getProperty(TIME, String.class, DEFAULT_DURATION));
        int amplifier = asInt(user, getProperty(AMPLIFIER, String.class, "2"));

        applyToRadius(user.getLocation(), radius, living -> {
            if (!arePlayersInParty(user, living) || living == user) {
                AtherysSkills.getInstance().getEffectService().applyEffect(living, Effects.ofSlowness("holy-presence", "Holy Presence", duration, amplifier));
            }
        });

        return CastResult.success();
    }
}
