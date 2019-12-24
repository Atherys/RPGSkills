package com.atherys.rpgskills.util.skill;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.skills.api.skill.TargetedCastable;
import org.spongepowered.api.entity.living.Living;

import static com.atherys.rpg.api.skill.TargetedRPGSkill.MAX_RANGE_PROPERTY;

public abstract class TargetedAtherysSkill extends AtherysSkill implements TargetedCastable {
    protected TargetedAtherysSkill(SkillSpec skillSpec) {
        super(skillSpec);
    }

    @Override
    public double getRange(Living user) {
        return asDouble(user, getProperty(MAX_RANGE_PROPERTY, String.class, "100.0"));
    }
}
