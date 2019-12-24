package com.atherys.rpgskills.util;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;

import static com.atherys.rpgskills.util.CommonProperties.*;

public abstract class AtherysSkill extends RPGSkill {
    protected AtherysSkill(SkillSpec skillSpec) {
        super(skillSpec);
    }

    protected String getDamage(String defaultDamage) {
        return getProperty(DAMAGE, String.class, defaultDamage);
    }

    protected String getTime(String defaultTime) {
        return getProperty(TIME, String.class, defaultTime);
    }

    protected String getHealing(String defaultHealing) {
        return getProperty(HEALING, String.class, defaultHealing);
    }

    protected String getAmplifier(String defaultAmplifier) {
        return getProperty(AMPLIFIER, String.class, defaultAmplifier);
    }
}
