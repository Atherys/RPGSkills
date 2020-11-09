package com.atherys.rpgskills.other;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.CommonProperties;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.skill.MeleeAttackSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;

public class Intimidate extends RPGSkill implements MeleeAttackSkill {
    public static final String INTIMIDATE_EFFECT = "intimidate-effect";

    public Intimidate() {
        super(
                SkillSpec.create()
                        .id("intimidate")
                        .name("Intimidate")
                        .cooldown("0")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        AtherysSkills.getInstance().getEffectService().applyEffect(user, INTIMIDATE_EFFECT);
        return CastResult.success();
    }

    @Override
    public boolean meleeAttack(Living user, Living target) {
        if (AtherysSkills.getInstance().getEffectService().hasEffect(target, INTIMIDATE_EFFECT)) {
            int duration = asInt(user, getProperty(CommonProperties.TIME, String.class, "5000"));
            AtherysSkills.getInstance().getEffectService().applyEffect(user, Effects.ofSlowness("intimidate", "Slowness", duration, 1));
        }
        return false;
    }
}
