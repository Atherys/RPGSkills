package com.atherys.rpgskills;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;

public class RagingBlow extends TargetedRPGSkill {
    private static final String DEFAULT_DAMAGE_EXPRESSION = "CLAMP(SOURCE_STRENGTH * 1.5, 0.5, 10.0)";
    private static final double DEFAULT_RANGE = 5.0;

    public RagingBlow() {
        super(
                SkillSpec.create()
                        .id("raging-blow")
                        .name("Raging Blow")
                        .cooldown("0")
                        .descriptionTemplate("Strikes the target with a powerful blow.")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        double damage = asDouble(user, target, getProperty("damage", String.class, DEFAULT_DAMAGE_EXPRESSION));

        target.damage(damage, EntityDamageSource.builder().entity(user).type(DamageTypes.ATTACK).build());

        return CastResult.success();
    }

    @Override
    public double getRange(Living user) {
        return getProperty("range", Double.class, DEFAULT_RANGE);
    }
}
