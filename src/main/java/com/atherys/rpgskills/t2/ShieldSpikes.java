package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.TextTemplate;

public class ShieldSpikes extends RPGSkill {
    public ShieldSpikes() {
        super(
                SkillSpec.create()
                        .id("shield-spikes")
                        .name("ShieldSpikes")
                        .descriptionTemplate(TextTemplate.of(
                                ""
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        return null;
    }
}