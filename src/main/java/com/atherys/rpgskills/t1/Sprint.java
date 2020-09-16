package com.atherys.rpgskills.t1;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.CommonProperties;
import com.atherys.rpgskills.util.Effects;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;

public class Sprint extends RPGSkill {
    public Sprint() {
        super(
                SkillSpec.create()
                        .id("sprint")
                        .name("Sprint")
                        .cooldown("0")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        int duration = asInt(user, getProperty(CommonProperties.TIME, String.class, "10000"));
        AtherysSkills.getInstance().getEffectService().applyEffect(user, Effects.ofSpeed("sprint", "Sprint", duration, 1));
        return CastResult.success();
    }
}
