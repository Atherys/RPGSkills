package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.CommonProperties;
import com.atherys.rpgskills.util.Effects;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;

public class Vanish extends RPGSkill {
    public Vanish() {
        super(
                SkillSpec.create()
                        .id("vanish")
                        .name("Vanish")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        return null;
    }
}
