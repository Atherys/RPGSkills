package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.TextTemplate;

public class Leap extends RPGSkill {
    public Leap() {
        super(
                SkillSpec.create()
                        .id("leap")
                        .name("Leap")
                        .descriptionTemplate(TextTemplate.of(
                                "Jump forward in the direction you're facing."
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Vector3d direction = PhysicsUtils.getUnitDirection(user);
        user.setVelocity(Vector3d.from(direction.getX() * 1.5, 0.8, direction.getZ() * 1.5));

        return CastResult.success();
    }
}
