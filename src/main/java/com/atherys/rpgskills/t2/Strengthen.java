package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;

public class Strengthen extends RPGSkill {
    public Strengthen() {
        super(
                SkillSpec.create()
                        .id("strengthen")
                        .name("Strengthen")
                        .resourceCost("0")
                        .cooldown("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        return null;
    }
}
