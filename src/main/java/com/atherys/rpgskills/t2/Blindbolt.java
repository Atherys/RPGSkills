package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.TextTemplate;

import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;

public class Blindbolt extends TargetedRPGSkill implements PartySkill {
    private static final String DEFAULT_DAMAGE = "5.0";

    public Blindbolt() {
        super(
                SkillSpec.create()
                        .id("blindbolt")
                        .name("Blindbolt")
                        .descriptionTemplate(TextTemplate.of(
                                ""
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
        return CastResult.success();
    }
}
