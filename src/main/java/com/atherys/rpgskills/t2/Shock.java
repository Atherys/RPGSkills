package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.CommonProperties;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;

import java.util.Collection;

public class Shock extends RPGSkill {
    public Shock() {
        super(
                SkillSpec.create()
                        .id("shock")
                        .name("Shock")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Collection<Entity> nearby = user.getNearbyEntities(asDouble(user, getProperty(CommonProperties.RANGE, String.class, "50")));
        Living closest;
        for (Entity e : nearby) {
        }
        return CastResult.success();
    }
}
