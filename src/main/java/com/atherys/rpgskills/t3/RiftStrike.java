package com.atherys.rpgskills.t3;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;

public class RiftStrike extends RPGSkill {
    protected RiftStrike() {
        super(
                SkillSpec.create().id("rift-strike").name("Rift Strike")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        return null;
    }
}
