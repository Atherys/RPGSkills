package com.atherys.rpgskills.util;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.TextTemplate;

public class BlankSkill extends RPGSkill {
    public BlankSkill(String id, String name) {
        super(
                SkillSpec.create()
                .id(id)
                .name(name)
                .descriptionTemplate(TextTemplate.EMPTY)
                .cooldown("0")
                .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        return CastResult.success();
    }
}
