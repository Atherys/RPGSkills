package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.TextTemplate;

import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;
import static com.atherys.rpgskills.util.CommonProperties.TIME;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Blindbolt extends TargetedRPGSkill implements PartySkill {
    private static final String DEFAULT_DAMAGE = "5.0";
    private static final String DEFAULT_TIME = "5000";
    private static final String BLINDBOLT_EFFECT = "blindbolt-effect";

    public Blindbolt() {
        super(
                SkillSpec.create()
                        .id("blindbolt")
                        .name("Blindbolt")
                        .descriptionTemplate(TextTemplate.of(
                                "Strike your target with a bolt of energy, dealing ",
                                arg(DAMAGE), " magical damage and blinding them for ", arg(TIME), " seconds."
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        if (arePlayersInParty(user, target)) throw isInParty();

        double damage = asDouble(user, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE));
        target.damage(damage, DamageUtils.directMagical(user));
        int time = asInt(user, getProperty(TIME, String.class, DEFAULT_TIME));

        AtherysSkills.getInstance().getEffectService().applyEffect(
                target,
                Effects.ofBlindness(BLINDBOLT_EFFECT, "Blindbolt", time, 2)
        );
        return CastResult.success();
    }
}
